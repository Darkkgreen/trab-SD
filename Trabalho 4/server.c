#include <stdio.h>
#include "IDL.h"

int * send_file_1_svc(struct infoArq *recebe, struct svc_req *request) { 
    FILE *arquivo;
    static int retorno;

    arquivo = fopen(recebe->nome, "a"); 

    if(arquivo == NULL)
    {
        retorno = -1;      
	   return &retorno;
    } 

    fwrite(recebe->dados, 1, recebe->bytes, arquivo);
    fclose(arquivo);

    retorno = 0;
    return &retorno;
}
