#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "IDL.h"

int main(int argc, char *argv[])
{	
	CLIENT *clnt;
    int *res;
    struct file_info chunk;
    FILE *file;	
	char host[50], name[50];

    printf("Type the HOST IP: ");
    scanf("%s", host);

    printf("Type the FILE NAME: \n");
    scanf("%s",name);

    //Cria conexao com o servidor
    clnt = clnt_create(host, PROG, VERSION, "tcp");
    if(clnt == NULL) {
         clnt_pcreateerror(host);
         return (0);
    } 

    //File recebe a abertura do arquivo digitado para a leitura
    file = fopen(name, "r");

    //Se o arquivo for nulo, exibimos que o arquivo nao existe, nao foi encontrado
	if(file == NULL){
		perror("The file does not exist!!\n");
		return (0);
	}


    chunk.name = name;
    
    //Comeca a transferencia    
    printf("Transferring, please, wait...\n");
    while(1)
    {
        chunk.bytes = fread(chunk.data, 1, MAXLEN, file);
        res = send_file_1(&chunk, clnt);

        if(res == NULL)
        {
            clnt_perror(clnt, host);
            return (0);
        } 

        if(*res != 0)
        {
           	fprintf(stderr, "Sorry, an error occurred! :(");
            return (0);
        } 
   
        if(chunk.bytes < MAXLEN)
        {
            printf("File successfully transferred! :D");
            break; 
        }
    }

    fclose(file);	
	return (0);
}
