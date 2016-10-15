//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

//public class Message implements java.io.Serializable, Comparable<Message>{
public class Message implements Comparable<Message>{
    private static int nextId = 0;

    private final int id;
    private final int clock;
    private final boolean ack;
    private final int senderId;

    private int acks;

    public Message(int id, int senderId, int clock, boolean ack) {
        this.id = id; 
        this.senderId = senderId;
        this.clock = clock;
        this.acks = 0; //Total de acks
        this.ack = ack; // Fala se é uma mensagem de retorno ou não
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

    public boolean isAck() {
        return ack;
    }

    public int compareTo(Message nova) {
        if (this.clock > nova.getClock())
            return 1;
        if (this.clock < nova.getClock())
            return -1;
        
        return 0;
    }
}
