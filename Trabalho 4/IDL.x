const MAXNAME = 256;
const MAXLEN = 1024;

typedef string file_name<MAXNAME>;

typedef opaque filechunk[MAXLEN]; 

struct infoArq
{
    file_name nome;
    filechunk dados;
    int bytes;
}; 

program PROG
{
    version VERSION
    {
        int enviaArq(struct infoArq *) = 1;
    } = 1;
} = 0x31000699;
