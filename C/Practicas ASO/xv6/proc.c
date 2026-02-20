#include "types.h"
#include "defs.h"
#include "param.h"
#include "memlayout.h"
#include "mmu.h"
#include "x86.h"
#include "proc.h"
#include "spinlock.h"

struct {
  struct spinlock lock;
  struct proc proc[NPROC];
} ptable;

// Estructura para cada nivel de prioridad
struct prio_queue {
  struct proc *first; 
  struct proc *last;  
};

// Array de 10 colas de prioridad (0 máxima, 9 mínima) 
struct {
  struct spinlock lock;
  struct prio_queue queues[10];
} prio_table;

static struct proc *initproc;

int nextpid = 1;
extern void forkret(void);
extern void trapret(void);

static void wakeup1(void *chan);

// --- FUNCIONES DE GESTIÓN DE COLAS ---

// Inserta un proceso al final de su cola de prioridad 
void
enqueue(struct proc *p)
{
  if(p->priority < 0 || p->priority > 9)
    panic("enqueue: prioridad invalida");

  struct prio_queue *q = &prio_table.queues[p->priority];
  p->next = 0; 

  if(q->last == 0){
    q->first = p;
    q->last = p;
  } else {
    q->last->next = p;
    q->last = p;
  }
}

// Quita el primer proceso de una cola específica 
struct proc*
dequeue(int prio)
{
  struct prio_queue *q = &prio_table.queues[prio];
  struct proc *p = q->first;

  if(p != 0){
    q->first = p->next; 
    if(q->first == 0)
      q->last = 0; 
    p->next = 0; 
  }
  return p;
}

// Saca un proceso específico de cualquier posición de la cola
void
remove_from_queue(struct proc *p, int prio)
{
  struct prio_queue *q = &prio_table.queues[prio];
  struct proc *curr = q->first;
  struct proc *prev = 0;

  while(curr != 0){
    if(curr == p){
      if(prev == 0) { 
        q->first = curr->next;
      } else {
        prev->next = curr->next;
      }
      if(curr->next == 0) { 
        q->last = prev;
      }
      curr->next = 0;
      return;
    }
    prev = curr;
    curr = curr->next;
  }
}

// --- SYSTEM CALLS (MODO KERNEL) ---

int
getprio(int pid)
{
  struct proc *p;
  int prio = -1;

  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid){
      prio = p->priority;
      break;
    }
  }
  release(&ptable.lock);
  return prio;
}

int
setprio(int pid, int priority)
{
  struct proc *p;
  int found = 0;

  if(priority < 0 || priority > 9)
    return -1;

  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid){
      acquire(&prio_table.lock);
      if(p->state == RUNNABLE){
        remove_from_queue(p, p->priority); 
        p->priority = priority;
        enqueue(p);
      } else {
        p->priority = priority;
      }
      release(&prio_table.lock);
      found = 1;
      break;
    }
  }
  release(&ptable.lock);
  return found ? 0 : -1;
}

// --- RESTO DE FUNCIONES DEL KERNEL ---

void
pinit(void)
{
  initlock(&ptable.lock, "ptable");
  initlock(&prio_table.lock, "prio_table");
}

int
cpuid() {
  return mycpu()-cpus;
}

struct cpu*
mycpu(void)
{
  int apicid, i;
  if(readeflags()&FL_IF)
    panic("mycpu called with interrupts enabled\n");
  apicid = lapicid();
  for (i = 0; i < ncpu; ++i) {
    if (cpus[i].apicid == apicid)
      return &cpus[i];
  }
  panic("unknown apicid\n");
}

struct proc*
myproc(void) {
  struct cpu *c;
  struct proc *p;
  pushcli();
  c = mycpu();
  p = c->proc;
  popcli();
  return p;
}

static struct proc*
allocproc(void)
{
  struct proc *p;
  char *sp;

  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++)
    if(p->state == UNUSED)
      goto found;

  release(&ptable.lock);
  return 0;

found:
  p->state = EMBRYO;
  p->pid = nextpid++;
  p->priority = 5; 
  p->next = 0;
  release(&ptable.lock);

  if((p->kstack = kalloc()) == 0){
    p->state = UNUSED;
    return 0;
  }
  sp = p->kstack + KSTACKSIZE;
  sp -= sizeof *p->tf;
  p->tf = (struct trapframe*)sp;
  sp -= 4;
  *(uint*)sp = (uint)trapret;
  sp -= sizeof *p->context;
  p->context = (struct context*)sp;
  memset(p->context, 0, sizeof *p->context);
  p->context->eip = (uint)forkret;
  return p;
}

void
userinit(void)
{
  struct proc *p;
  extern char _binary_initcode_start[], _binary_initcode_size[];
  p = allocproc();
  initproc = p;
  if((p->pgdir = setupkvm()) == 0)
    panic("userinit: out of memory?");
  inituvm(p->pgdir, _binary_initcode_start, (int)_binary_initcode_size);
  p->sz = PGSIZE;
  memset(p->tf, 0, sizeof(*p->tf));
  p->tf->cs = (SEG_UCODE << 3) | DPL_USER;
  p->tf->ds = (SEG_UDATA << 3) | DPL_USER;
  p->tf->es = p->tf->ds;
  p->tf->ss = p->tf->ds;
  p->tf->eflags = FL_IF;
  p->tf->esp = PGSIZE;
  p->tf->eip = 0;
  safestrcpy(p->name, "initcode", sizeof(p->name));
  p->cwd = namei("/");

  acquire(&ptable.lock);
  p->state = RUNNABLE;
  acquire(&prio_table.lock); 
  enqueue(p);                
  release(&prio_table.lock); 
  release(&ptable.lock);
}

int
growproc(int n)
{
  uint sz;
  struct proc *curproc = myproc();
  sz = curproc->sz;
  if(n > 0){
    if((sz = allocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  } else if(n < 0){
    if((sz = deallocuvm(curproc->pgdir, sz, sz + n)) == 0)
      return -1;
  }
  curproc->sz = sz;
  lcr3(V2P(curproc->pgdir));
  return 0;
}

int
fork(void)
{
  int i, pid;
  struct proc *np;
  struct proc *curproc = myproc();

  if((np = allocproc()) == 0) return -1;
  if((np->pgdir = copyuvm(curproc->pgdir, curproc->sz)) == 0){
    kfree(np->kstack);
    np->kstack = 0;
    np->state = UNUSED;
    return -1;
  }
  np->sz = curproc->sz;
  np->priority = curproc->priority; 
  np->parent = curproc;
  *np->tf = *curproc->tf;
  np->tf->eax = 0;
  for(i = 0; i < NOFILE; i++)
    if(curproc->ofile[i])
      np->ofile[i] = filedup(curproc->ofile[i]);
  np->cwd = idup(curproc->cwd);
  safestrcpy(np->name, curproc->name, sizeof(curproc->name));
  pid = np->pid;

  acquire(&ptable.lock);
  np->state = RUNNABLE;
  acquire(&prio_table.lock); 
  enqueue(np);                
  release(&prio_table.lock); 
  release(&ptable.lock);
  return pid;
}

void
exit(int status)
{
  struct proc *curproc = myproc();
  struct proc *p;
  int fd;

  if(curproc == initproc) panic("init exiting");
  for(fd = 0; fd < NOFILE; fd++){
    if(curproc->ofile[fd]){
      fileclose(curproc->ofile[fd]);
      curproc->ofile[fd] = 0;
    }
  }
  begin_op();
  iput(curproc->cwd);
  end_op();
  curproc->cwd = 0;

  acquire(&ptable.lock);
  wakeup1(curproc->parent);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->parent == curproc){
      p->parent = initproc;
      if(p->state == ZOMBIE) wakeup1(initproc);
    }
  }
  curproc->xstate = status;
  acquire(&prio_table.lock);
  if(curproc->state == RUNNABLE)
     remove_from_queue(curproc, curproc->priority);
  release(&prio_table.lock);

  curproc->state = ZOMBIE;
  sched();
  panic("zombie exit");
}

int
wait(int *status)
{
  struct proc *p;
  int havekids, pid;
  struct proc *curproc = myproc();

  acquire(&ptable.lock);
  for(;;){
    havekids = 0;
    for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
      if(p->parent != curproc)
        continue;
      havekids = 1;

      if(p->state == ZOMBIE){
        acquire(&prio_table.lock);
        if(p->state == RUNNABLE)
          remove_from_queue(p, p->priority);
        release(&prio_table.lock);

        pid = p->pid;
        if(status != 0 &&
           copyout(curproc->pgdir, (uint)status, &p->xstate, sizeof(int)) < 0){
          release(&ptable.lock);
          return -1;
        }

        kfree(p->kstack);
        p->kstack = 0;
        freevm(p->pgdir, 0);
        p->pid = 0;
        p->parent = 0;
        p->name[0] = 0;
        p->killed = 0;
        p->state = UNUSED;

        release(&ptable.lock);
        return pid;
      }
    }

    if(!havekids || curproc->killed){
      release(&ptable.lock);
      return -1;
    }

    sleep(curproc, &ptable.lock);
  }
}


void
scheduler(void)
{
  struct proc *p;
  struct cpu *c = mycpu();
  c->proc = 0;

  for(;;){
    sti();
    acquire(&ptable.lock);
    acquire(&prio_table.lock);

    p = 0;
    for(int i = 0; i < 10; i++){
      p = dequeue(i);
      if(p != 0) break;
    }

    if(p != 0){
      release(&prio_table.lock);
      c->proc = p;
      switchuvm(p);
      p->state = RUNNING;
      swtch(&(c->scheduler), p->context);
      switchkvm();
      c->proc = 0;
    } else {
      release(&prio_table.lock);
    }
    release(&ptable.lock);
  }
}

void
sched(void)
{
  int intena;
  struct proc *p = myproc();
  if(!holding(&ptable.lock)) panic("sched ptable.lock");
  if(mycpu()->ncli != 1) panic("sched locks");
  if(p->state == RUNNING) panic("sched running");
  if(readeflags()&FL_IF) panic("sched interruptible");
  intena = mycpu()->intena;
  swtch(&p->context, mycpu()->scheduler);
  mycpu()->intena = intena;
}

void
yield(void)
{
  struct proc *p = myproc(); // Corregido: obtener proceso actual
  acquire(&ptable.lock); 
  p->state = RUNNABLE;
  acquire(&prio_table.lock);
  enqueue(p);                // Corregido: era np, ahora es p
  release(&prio_table.lock);
  sched();
  release(&ptable.lock);
}

void
forkret(void)
{
  static int first = 1;
  release(&ptable.lock);
  if (first) {
    first = 0;
    iinit(ROOTDEV);
    initlog(ROOTDEV);
  }
}

void
sleep(void *chan, struct spinlock *lk)
{
  struct proc *p = myproc();
  if(p == 0) panic("sleep");
  if(lk == 0) panic("sleep without lk");

  if(lk != &ptable.lock){
    acquire(&ptable.lock);
    release(lk);
  }
  p->chan = chan;
  p->state = SLEEPING;
  sched();
  p->chan = 0;
  if(lk != &ptable.lock){
    release(&ptable.lock);
    acquire(lk);
  }
}

static void
wakeup1(void *chan)
{
  struct proc *p;
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->state == SLEEPING && p->chan == chan){
      p->state = RUNNABLE;
      acquire(&prio_table.lock); 
      enqueue(p);                
      release(&prio_table.lock); 
    }
  }
}

void
wakeup(void *chan)
{
  acquire(&ptable.lock);
  wakeup1(chan);
  release(&ptable.lock);
}

int
kill(int pid)
{
  struct proc *p;
  acquire(&ptable.lock);
  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->pid == pid){
      p->killed = 1;
      if(p->state == SLEEPING){
        p->state = RUNNABLE;
        acquire(&prio_table.lock);
        enqueue(p);
        release(&prio_table.lock);
      }
      release(&ptable.lock);
      return 0;
    }
  }
  release(&ptable.lock);
  return -1;
}

void
procdump(void)
{
  static char *states[] = {
  [UNUSED]    "unused",
  [EMBRYO]    "embryo",
  [SLEEPING]  "sleep ",
  [RUNNABLE]  "runble",
  [RUNNING]   "run   ",
  [ZOMBIE]    "zombie"
  };
  int i;
  struct proc *p;
  char *state;
  uint pc[10];

  for(p = ptable.proc; p < &ptable.proc[NPROC]; p++){
    if(p->state == UNUSED) continue;
    if(p->state >= 0 && p->state < NELEM(states) && states[p->state])
      state = states[p->state];
    else
      state = "???";
    cprintf("%d %s %s", p->pid, state, p->name);
    if(p->state == SLEEPING){
      getcallerpcs((uint*)p->context->ebp+2, pc);
      for(i=0; i<10 && pc[i] != 0; i++)
        cprintf(" %p", pc[i]);
    }
    cprintf("\n");
  }
}
