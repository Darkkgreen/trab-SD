/*Definiçao do xdr*/

const MAXARQ = 256;
const MAXLEN = 1024;

/*Nome do arquivo a ser enviado*/
typedef string file_name<MAXARQ>;

/*Define tipo de dado não estruturado à ser utilizado para enviar "trechos" do arquivo*/
typedef opaque filechunk[MAXLEN]; 

/*Estrutura de metadados do arquivos e conteudo temporario do arquivo*/
struct file_info
{
    file_name name;
    filechunk data;
    int bytes;
}; 

program PROG
{
    version VERSION
    {
        int send_file(struct file_info *) = 1;
    } = 1;
} = 0x31000699;
