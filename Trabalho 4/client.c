#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "IDL.h"

int main(int argc, char *argv[])
{	
	CLIENT *cliente;
    int *resposta;
    struct infoArq chunk;
    FILE *arquivo;

    cliente = clnt_create(argv[1], PROG, VERSION, "tcp");

    if(cliente == NULL) {
         clnt_pcreateerror(argv[1]);
         return (0);
    } 

    arquivo = fopen(argv[2], "r");

	if(arquivo == NULL){
		perror("Arquivo inexistente\n");
		return (0);
	}


    chunk.nome = argv[2];
     
    printf("Iniciando transferência do arquivo %s\n", argv[2]);

    while(1)
    {
        chunk.bytes = fread(chunk.dados, 1, MAXLEN, arquivo);
        resposta = send_file_1(&chunk, cliente);

        if(resposta == NULL)
        {
            clnt_perror(cliente, argv[1]);
            return (0);
        } 

        if(*resposta != 0)
        {
           	fprintf(stderr, "Ocorreu um erro durante a transferência\n");
            return (0);
        } 
   
        if(chunk.bytes < MAXLEN)
        {
            printf("Arquivo %s enviado com sucesso ao servidor\n", argv[2]);
            break; 
        }
    }

    fclose(arquivo);	
	return (0);
}
