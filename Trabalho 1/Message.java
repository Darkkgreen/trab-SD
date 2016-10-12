//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

public class Message implements java.io.Serializable, Comparable<Message>{
    private static int nextId = 0;

    private final int id;
    private final long clock;
    private final boolean ack;
    private final int senderId;

    private int acks;

    public Message(int id, int senderId, long clock, boolean ack) {
        this.id = id;
        this.senderId = senderId;
        this.clock = clock;
        this.acks = 0;
        this.ack = ack;
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

    public long getClock() {
        return clock;
    }

    public boolean isAck() {
        return ack;
    }

    public int compTo(Message nova) {
        if (this.id > nova.getId())
            return 1;
        if (this.id < nova.getId())
            return -1;
        
        return 0;
    }
}
