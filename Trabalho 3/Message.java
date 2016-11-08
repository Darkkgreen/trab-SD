//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

//public class Message implements java.io.Serializable, Comparable<Message>{
public class Message implements Comparable<Message>{
    private static int nextId = 0;

    private final int id;
    private final int clock;
    private  int tipo;
    private int senderId;
    public static int ELEICAO = 1;    
    public static int OK = 0;
    public static int NOVO_COORD = 2;
    private int acks;

    public Message(int id, int senderId, int clock, int recurso, int tipo) {
        this.id = id; 
        this.senderId = senderId;
        this.clock = clock;
        this.acks = 0; //Total de acks
        this.tipo = tipo; // Fala se é uma mensagem de retorno ou não
    }

    public int getSenderId(){
        return senderId;
    }

    public int getId() {
        return id;
    }

    public void addAck(){
        this.acks++;
    }

    public int getAcks() {
        return acks;
    }

    public int getClock() {
        return clock;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int t){
        this.tipo = t;
    }
    public void setSenderId(int s){
        this.senderId = s;
    }

    public int compareTo(Message nova) {
        if (this.clock > nova.getClock())
            return 1;
        if (this.clock < nova.getClock())
            return -1;
        
        return 0;
    }
}
