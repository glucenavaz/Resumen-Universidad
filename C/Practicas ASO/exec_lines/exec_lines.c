//Gonzalo Lucena Vázquez, subgrupo 3.1
//Francisco Bartolomé García, subgrupo 3.3

#define _POSIX_C_SOURCE 200809L
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <signal.h>

// Variables globales
int es_senal = 0;
int procesos_activos = 0;
int cod_salida = 0;         //Indica si ha salido normal o por señal
int error_detectado = 0;    //Para saber si se ha detectado un error o no
int linea_error = 0;        //Num de línea que causó el error

//Struct para controlar la info de una linea
typedef struct {
    char *elems[128];   /* Número max. de elementos por línea */
    int num_elems;      /* Véase ls -l -a son 3 elems */
    char operador;      // '<', '>', 'a'(>>), '|', 0 (ninguno)
    int pos_operador;
} Linea;

//Función para mostrar el mensaje del -h
void mensaje_ayuda(const char *progr) {
    printf("Uso: %s [-b BUF_SIZE] [-l MAX_LINE_SIZE] [-p NUM_PROCS]\n", progr);
    printf("Lee de la entrada estándar una secuencia de líneas conteniendo órdenes\n");
    printf("para ser ejecutadas y lanza los procesos necesarios para ejecutar cada\n");
    printf("línea, esperando a su terminación para ejecutar la siguiente.\n");
    printf("-b BUF_SIZE       Tamaño del buffer de entrada 1<=BUF_SIZE<=8192\n");
    printf("-l MAX_LINE_SIZE  Tamaño máximo de línea 16<=MAX_LINE_SIZE<=1024\n");
    printf("-p NUM_PROCS      Número de procesos en ejecución de forma simultánea (1 <= NUM_PROCS <= 8)\n");
}

//Instalador del manejador
void instala_manejador_signal(int signal, void (*signal_handler)(int))
{
    struct sigaction sa;
    memset(&sa, 0, sizeof(struct sigaction));
    sa.sa_handler = signal_handler;
    sa.sa_flags = SA_NOCLDSTOP;
    sigemptyset(&sa.sa_mask);
    if (sigaction(signal, &sa, NULL) == -1) {
        perror("sigaction()");
        exit(EXIT_FAILURE);
    }
}

//Manejador para SIGCHLD
void manejador_sigchld(int sig) {
    int saved_errno = errno;
    if (sig == SIGCHLD)
    {
        int status;
        pid_t pid;
        while ((pid = waitpid(-1, &status, WNOHANG)) > 0) {
            procesos_activos--;
            if(!error_detectado){
                if (WIFEXITED(status) && WEXITSTATUS(status) != 0) {  //Si el hijo termina con cod_salida != 0
                    cod_salida = WEXITSTATUS(status);       //Nos quedamos con el codigo de salida
                    es_senal = 0;
                    error_detectado = 1;
                } else if (WIFSIGNALED(status)) {           //Si termina por señal
                    cod_salida = WTERMSIG(status);          //Nos quedamos con la señal
                    es_senal = 1;
                    error_detectado = 1;
                }
            }
        }
    }
    errno = saved_errno;
}

//Leer línea y evaluar elementos y operadores
int leer_linea(char *linea, Linea *procesada) {
    procesada->num_elems = 0;
    procesada->operador = 0;
    procesada->pos_operador = -1;

    char *elem = strtok(linea, " ");
    int num_operadores = 0;

    while (elem != NULL && procesada->num_elems < 128) {
        if (strcmp(elem, "<") == 0 || strcmp(elem, ">") == 0 || 
            strcmp(elem, ">>") == 0 || strcmp(elem, "|") == 0) {
            num_operadores++;
            if (num_operadores > 1) {
                fprintf(stderr, "Error: Solo puede haber un operador por línea.\n");
                return -1;
            }
            procesada->pos_operador = procesada->num_elems;     //Guardamos la posicion actual
            if (strcmp(elem, "<") == 0) procesada->operador = '<';
            else if (strcmp(elem, ">") == 0) procesada->operador = '>';
            else if (strcmp(elem, ">>") == 0) procesada->operador = 'a';
            else if (strcmp(elem, "|") == 0) procesada->operador = '|';
        } else {
            procesada->elems[procesada->num_elems] = strdup(elem);
            procesada->num_elems++;
        }
        elem = strtok(NULL, " ");
    }

    return 0;
}

//Liberar memoria de elementos (evitamos el memory leak)
void liberar_elems(Linea *linea) {
    for (int i = 0; i < linea->num_elems; i++) {
        free(linea->elems[i]);
    }
}

//Caso 1: comandos simples
void ejecutar_simple(Linea *linea) {
    char *args[129];
    for (int i = 0; i < linea->num_elems; i++) args[i] = linea->elems[i];
    args[linea->num_elems] = NULL;

    execvp(args[0], args);
    perror("execvp");
    exit(EXIT_FAILURE);
}

//Caso 2.1: redirección entrada (<)
void ejecutar_redir_entrada(Linea *linea) {
    int fd = open(linea->elems[linea->pos_operador], O_RDONLY);
    if (fd < 0) { 
        perror("open()"); 
        exit(EXIT_FAILURE); 
    }
    dup2(fd, STDIN_FILENO);
    close(fd);

    char *args[129];
    for (int i = 0; i < linea->pos_operador; i++) args[i] = linea->elems[i];
    args[linea->pos_operador] = NULL;

    execvp(args[0], args);
    perror("execvp");
    exit(EXIT_FAILURE);
}

//Caso 2.2: redirección salida (> y >>)
void ejecutar_redir_salida(Linea *linea, int append) {
    int flags = O_WRONLY | O_CREAT | (append ? O_APPEND : O_TRUNC);
    int fd = open(linea->elems[linea->pos_operador], flags, 0644);
    if (fd < 0) { 
        perror("open()"); 
        exit(EXIT_FAILURE); 
    }
    dup2(fd, STDOUT_FILENO);
    close(fd);

    char *args[129];
    for (int i = 0; i < linea->pos_operador; i++) args[i] = linea->elems[i];
    args[linea->pos_operador] = NULL;

    execvp(args[0], args);
    perror("execvp");
    exit(EXIT_FAILURE);
}

//Caso 2.3: tubería
void ejecutar_tuberia(Linea *leida) {
    int pipefd[2];
    if (pipe(pipefd) < 0) { 
        perror("pipe()"); 
        exit(EXIT_FAILURE); 
    }
    //Hijo escritor
    pid_t pid1 = fork();
    if (pid1 == 0) {
        close(pipefd[0]);
        dup2(pipefd[1], STDOUT_FILENO);
        close(pipefd[1]);

        char *args[129];
        for (int i = 0; i < leida->pos_operador; i++) args[i] = leida->elems[i];
        args[leida->pos_operador] = NULL;

        execvp(args[0], args);
        perror("execvp");
        exit(EXIT_FAILURE);
    }
    //Hijo lector
    pid_t pid2 = fork();
    if (pid2 == 0) {
        close(pipefd[1]);
        dup2(pipefd[0], STDIN_FILENO);
        close(pipefd[0]);

        char *args[129];
        int j = 0;
        for (int i = leida->pos_operador; i < leida->num_elems; i++)
            args[j++] = leida->elems[i];
        args[j] = NULL;

        execvp(args[0], args);
        perror("execvp");
        exit(EXIT_FAILURE);
    }

    close(pipefd[0]);
    close(pipefd[1]);
    waitpid(pid1,NULL,0);
    waitpid(pid2,NULL,0);
}

int main(int argc, char *argv[]) {
    int tam_buffer = 16;
    int tam_max_linea = 32;
    int num_procesos = 1;

    sigset_t mask, oldmask;
    sigemptyset(&mask);
    sigaddset(&mask, SIGCHLD);
    sigprocmask(SIG_BLOCK, &mask, &oldmask); //Bloqueamos SIGCHLD temporalmente

    instala_manejador_signal(SIGCHLD, manejador_sigchld);

    int opt;
    while ((opt = getopt(argc, argv, "hb:l:p:")) != -1) {
        switch (opt) {
            case 'h':
                mensaje_ayuda(argv[0]); 
                exit(EXIT_SUCCESS);
            case 'b':
                tam_buffer = atoi(optarg);
                if (tam_buffer < 1 || tam_buffer > 8192){   //Trat. error tam_buffer
                    fprintf(stderr, "Error. El tamaño de buffer tiene que estar entre 1 y 8192\n");
                    exit(EXIT_FAILURE);
                }
                break;
            case 'l':
                tam_max_linea = atoi(optarg);
                if(tam_max_linea < 16 || tam_max_linea > 1024){   //Trat. error tam_max_linea
                    fprintf(stderr, "Error. El tamaño máximo de línea tiene que estar entre 16 y 1024\n");
                    exit(EXIT_FAILURE);
                }
                break;
            case 'p':
                num_procesos = atoi(optarg);
                if(num_procesos < 1 || num_procesos > 8) {      //Trat. error num_procesos
                    fprintf(stderr, "Error. El número de procesos en ejecución tiene que estar entre 1 y 8\n");
                    exit(EXIT_FAILURE);
                } 
                break;
            default: 
                mensaje_ayuda(argv[0]); 
                exit(EXIT_FAILURE);
        }
    }

    // Buffer para leer entrada
    char *buffer = malloc(tam_buffer);
    char *linea_actual = malloc(tam_max_linea + 1);
    ssize_t bytes_leidos;
    int pos_linea = 0;
    int num_linea = 1;

    // Ejemplo de lectura por bloques hasta EOF (simplificado)
    while ((bytes_leidos = read(STDIN_FILENO, buffer, tam_buffer)) > 0) {
        for (ssize_t i = 0; i < bytes_leidos; i++) {
            if (buffer[i] == '\n'){
                linea_actual[pos_linea] = '\0';
                if(pos_linea > 0) {
                    //Tratam. 1 (ERRORES): Si detecta un error en una de las líneas de la 1 a la n-1
                    if(error_detectado == 1){
                        // Si ya se detectó error, esperar a que todos terminen antes de salir
                        while(procesos_activos > 0) sigsuspend(&oldmask);
                    
                        //Mostrar error y salir
                        fprintf(stderr, "Error al ejecutar la línea %d. ", linea_error);
                        
                        if (es_senal) fprintf(stderr, "Terminación anormal por señal %d\n", cod_salida);
                        else fprintf(stderr, "Terminación normal con código %d\n", cod_salida);

                        free(buffer);
                        free(linea_actual);
                        exit(EXIT_FAILURE);
                    }

                    Linea procesada;
                    //Esperamos si hay demasiados procesos activos
                    while (procesos_activos >= num_procesos) sigsuspend(&oldmask);

                    if (leer_linea(linea_actual, &procesada) == 0) {
                        // Ejecutamos según tipo de operador
                        pid_t pid = fork();
                        if (pid == 0) {  
                            //Restauramos máscara en el hijo para que pueda recibir señales normalmente
                            sigprocmask(SIG_SETMASK, &oldmask, NULL);   
                            switch (procesada.operador) {
                                case 0: ejecutar_simple(&procesada); break;
                                case '<': ejecutar_redir_entrada(&procesada); break;
                                case '>': ejecutar_redir_salida(&procesada, 0); break;
                                case 'a': ejecutar_redir_salida(&procesada, 1); break;
                                case '|': 
                                    ejecutar_tuberia(&procesada); 
                                    exit(EXIT_SUCCESS);     //El hijo ha de acabar tras ejecutar la tubería
                                    break;                  //si no, se podía quedar zombie.
                        }
                        exit(EXIT_FAILURE);
                        } else if(pid > 0) {        //Proc. padre
                            procesos_activos++;
                            if (!error_detectado) linea_error = num_linea;
                        } else {
                            perror("fork()");
                            free(buffer);
                            free(linea_actual);
                            exit(EXIT_FAILURE);
                        }
                    liberar_elems(&procesada);      //Vaciamos memoria de elems
                    }
                }

                pos_linea = 0; //reiniciamos buffer de línea
                num_linea++;   //y aumentamos el numero de línea
            } else {
                //Tratam. 2 (ERRORES): Verificación de linea demasiado larga.

                if (pos_linea >= tam_max_linea) {
                    linea_actual[tam_max_linea] = '\0';
                    fprintf(stderr, "Error, línea %d demasiado larga: \"%s...\"\n", num_linea, linea_actual);
                    free(buffer);
                    free(linea_actual);
                    exit(EXIT_FAILURE);
                }
                linea_actual[pos_linea++] = buffer[i];
            }
        }
    }

    if (bytes_leidos < 0) {     //Tratam. error read()
        perror("read()");
        free(buffer);
        free(linea_actual);
        exit(EXIT_FAILURE);
    }

    // Espera final de todos los hijos
    while (procesos_activos > 0) sigsuspend(&oldmask);

    free(buffer);
    free(linea_actual);

    //Tratam. 3 (ERRORES): Detecta el error en la líena n:
    if (error_detectado) {
        fprintf(stderr, "Error al ejecutar la línea %d. ", linea_error);
        if (es_senal) fprintf(stderr, "Terminación anormal por señal %d\n", cod_salida);
        else fprintf(stderr, "Terminación normal con código %d\n", cod_salida);
        exit(EXIT_FAILURE);
    }

    return 0;
}

