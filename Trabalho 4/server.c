#include <stdio.h>
#include "IDL.h"

int * send_file_1_svc(struct file_info *rec, struct svc_req *rqstp) { 
    FILE *file;
    static int ret;

    file = fopen(rec->name, "a"); 

    if(file == NULL)
    {
        ret = -1;      
	   return &ret;
    } 

    fwrite(rec->data, 1, rec->bytes, file);
    fclose(file);

    ret = 0;
    return &ret;
}
