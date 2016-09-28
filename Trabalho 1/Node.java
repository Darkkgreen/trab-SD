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
    private static int nextId = 0; // Aqui é onde ocorre o controle de IDs dos objetos criados

    private boolean exec; // Verificação para a thread //(se está ativa)
    private final Thread thread; //Obviamente a thread
    private final Semaphore semaforo; //Obviamente o semáforo
	
    private final MulticastSocket socket; //Obviamente o socket para multicasting
    private static final String mIp = "228.0.0.0"; //Obviamente o IP do multicasting
    private static final int mPort = 4242; //Obviamente a porta multicasting
    private final InetAddress group; //Não entendi muito bem o que seria o grupo mas faz parte do role do socket

    public Node() throws IOException{ //Construtor do Nó. Aqui ele só cria com as configurações essa XANA
        this.iNode = Node.nextId;
        Node.nextId++;
        this.cNode = this.iNode;
        this.fMsg = new PriorityQueue<>();

        this.semaforo = new Semaphore(1, true);

        this.group = InetAddress.getByName(mIp);
        this.socket = new MulticastSocket(this.mPort);

        this.thread = new Thread(()->receiveMsg()); //Esse aqui é o processo que vai ser THREADED do objeto
        this.exec = false;
    }
	
    private void receiveMsg(){ //O processo que foi threaded, que fica escutando as mensagens que chegam
        Message msg = null; //Variável que vai ser o objeto da mensagem 
        byte[] buffer = new byte[1024]; //Buffer (que é o que será passado na conexão)
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length); //O datagrama que será utilizado para ser enviado
		
        while(exec){ //Enquanto a execução for True vai executar
            try {
                socket.receive(pacote); //Recebe o pacote datagram
                semaforo.acquire(); //Utiliza o semáforo para bloquear o acesso de outras threads ao canal

                ByteArrayInputStream conteudoMsg = new ByteArrayInputStream(pacote.getData()); //Pega a data do pacote
                ObjectInputStream ois = new ObjectInputStream(conteudoMsg); //Transforma em um Objeto
                msg = (Message) ois.readObject(); //Lê o objeto e joga no formato Message

                updateClock(msg.getClock()); //Aqui atualiza o clock a partir do clock da Mensagem

                if(msg.isAck()){ //Verifica se a mensagem é um ACK
                    for(Message i : fMsg){ //Verifica a fila de mensagens pra encontrar a mensagem e adicionar o ACK
                        if (i.getId() == msg.getId()){
                            i.addAck();
                            break;
                        }
                    }

                    long lider = 2; //Aqui vota pelo líder (não entendi muito bem essa parte, mas lembro q o fábio falou sobre isso)
                    if (this.iNode == lider){
                        for (Message i: fMsg){
                            System.out.print(i+" ");
                        }
                        System.out.println();
                    }

                    while(!fMsg.isEmpty() && fMsg.peek().getAcks() == 3){ //(ñ entendi muito bem tb)
                        msg = fMsg.remove();
                        if (this.iNode == lider){
                            System.out.println("removed : " + msg);
                        }
                    }
                }else{ // Caso não seja ACK, apenas adiciona na fila e manda um aviso de que recebeu
                    fMsg.add(msg);
                    sendAck(msg.getId());
                }

            }
            catch (SocketException e){ //captura qualquer erro do socket
//                System.out.println(this.id + ": Socket Closed");
            } catch (ClassNotFoundException | IOException e) { //erros de entrada
                e.printStackTrace();
            }
            catch (InterruptedException e) { //execução
                e.printStackTrace();
            }finally { //Caso tudo ocorra certo, ele libera o semáforo
                semaforo.release();
            }
        }
    }
	//serve pra manter o clock static e synchronized com toda as threads
    private synchronized void updateClock(long newClock){
        this.cNode = Math.max(this.cNode, newClock) + 1;
    }

    private void _send(Message msg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            updateClock(0);
            oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);
            socket.send(new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, group, mPort));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAck(int id) {
        _send(new Message(id, this.iNode, this.cNode, true));
    }

    public void send(){
        try {
            semaforo.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _send(new Message(this.iNode, this.cNode, false));
        semaforo.release();
    }

    public void start(){
        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        exec = true;
        thread.start();
    }

    public int getId(){
        return this.iNode;
    }

    public synchronized void stop(){
//        System.out.println("Stopping " + this.id + " ...");

        try {
            // disconnect
            socket.leaveGroup(group);
            socket.close();

            // break loop
            exec = false;

            // kill thread
            thread.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println("Stoped " + this.id);

    }

    public void printQueue(){
        try {
            semaforo.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Message i: fMsg){
            System.out.println(i);
        }
        semaforo.release();
    }
}
