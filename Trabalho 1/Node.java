//Implementação 

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class Node{
    private final int iNode; // Número de identificação do nó
    private long cNode; // Clock atual do nó
    private final Queue<Message> fMsg; // Fila de mensagens do tipo Message
    private static int nextId = 0;

    private boolean exec; // Verificação para a thread //(se está ativa)
    private final Thread thread;
    private final Semaphore semaforo;
	
    private final MulticastSocket socket;
    private static final String mIp = "228.0.0.0";
    private static final int mPort = 4242;
    private final InetAddress group;

    public Node() throws IOException{
        this.iNode = Node.nextId;
        Node.nextId++;
        this.cNode = this.iNode;
        this.fMsg = new PriorityQueue<>();

        this.semaforo = new Semaphore(1, true);

        this.group = InetAddress.getByName(mIp);
        this.socket = new MulticastSocket(this.mPort);

        this.thread = new Thread(()->receiveMsg());
        this.exec = false;
    }
	
    private void receiveMsg(){
        Message msg = null;
        byte[] buffer = new byte[1024];
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length);
		
        while(exec){
            try {
                socket.receive(pacote);
                semaforo.acquire();

                ByteArrayInputStream conteudoMsg = new ByteArrayInputStream(pacote.getData());
                ObjectInputStream ois = new ObjectInputStream(conteudoMsg);
                msg = (Message) ois.readObject();

                updateClock(msg.getClock());

                if(msg.isAck()){
                    for(Message i : fMsg){
                        if (i.getId() == msg.getId()){
                            i.addAck();
                            break;
                        }
                    }

                    long lider = 2;
                    if (this.iNode == lider){
                        for (Message i: fMsg){
                            System.out.print(i+" ");
                        }
                        System.out.println();
                    }

                    while(!fMsg.isEmpty() && fMsg.peek().getAcks() == 3){
                        msg = fMsg.remove();
                        if (this.iNode == lider){
                            System.out.println("removed : " + msg);
                        }
                    }
                }else{
                    fMsg.add(msg);
                    sendAck(msg.getId());
                }

            }
            catch (SocketException e){
//                System.out.println(this.id + ": Socket Closed");
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                semaphore.release();
            }
        }
    }
	//serve pra manter o clock static e synchronized com toda as threads
    private synchronized void updateClock(long newClock){
        this.clock = Math.max(this.clock, newClock) + 1;
    }

    private void _send(Message msg) {
	   //cria um array de bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            updateClock(0);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);
            socket.send(new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, group, multicastPort));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAck(int id) {
        _send(new Message(id, this.id, this.clock, true));
    }

    public void send(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _send(new Message(this.id, this.clock, false));
        semaphore.release();
    }

    public void start(){
        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = true;
        receiveThread.start();
    }

    public int getId(){
        return this.id;
    }

    public synchronized void stop(){
//        System.out.println("Stopping " + this.id + " ...");

        try {
            // disconnect
            socket.leaveGroup(group);
            socket.close();

            // break loop
            running = false;

            // kill thread
            receiveThread.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("Stoped " + this.id);

    }

    public void printQueue(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Message i: receiveQueue){
            System.out.println(i);
        }
        semaphore.release();
    }
}
