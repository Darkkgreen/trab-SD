package com.company;

/**
 * Created by renan on 8/18/15.
 */
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

    public Message(int senderId, long clock, boolean ack) {
        this.id = Message.nextId;
        Message.nextId++;
        this.senderId = senderId;
        this.clock = clock;
        this.acks = 0;
        this.ack = ack;
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

    @Override
    public String toString() {
        return "(id: " + id +
                ", senderId: " + senderId +
                ", clock: " + clock +
                ", acks: " + acks +
                ", ack?: " + ack +
                ')';
    }

    @Override
    public int compareTo(Message o) {
        if (this.clock == o.getClock()){
            return this.id - o.getId();
        }
        return (int)(this.clock - o.getClock());
    }
}
