//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.TreeSet;
import java.net.Socker;
import java.net.ServerSocket;

public class Node{
    private static int idNode; // Número de identificação do nó
    private long clockNode; // Clock atual do nó
    private static TreeSet<Message> filaMsg; // Fila de mensagens do tipo Message

    private boolean run; // Verificação para a thread //(se está ativa)
    private static Thread threadRecibo; //Obviamente a thread
    private static Thread threadEnvio;

    private static Socket socketClient; //Obviamente o socket
    private static ServerSocket socketServer;
    private static ip = "localhost";
    private static port;

    public static void main(String[] args) throws IOException{ //Construtor do Nó. Aqui ele só cria com as configurações
        this.idNode = 3;
        this.clockNode = this.idNode;
        this.port = 55209;

        this.filaMsg = new TreeSet<Message>(new MessageComp());

        this.threadRecibo = new Thread(()->receiveMsg()); //Esse aqui é o processo que vai ser THREADED do objeto
        this.threadEnvio = new Thread(()->sendMsg());

        this.start();
    }
	
    private void receiveMsg(){ //O processo que foi threaded, que fica escutando as mensagens que chegam
        Message msg = null; //Variável que vai ser o objeto da mensagem 
        byte[] buffer = new byte[1024]; //Buffer (que é o que será passado na conexão)
        DatagramPacket pacote = new DatagramPacket(buffer, buffer.length); //O datagrama que será utilizado para ser enviado
        socketServer = new SocketServer(port);
		
        while(true){
            try {
                socketServer.receive(pacote); //Recebe o pacote datagram

                ByteArrayInputStream conteudoMsg = new ByteArrayInputStream(pacote.getData()); //Pega a data do pacote
                ObjectInputStream ois = new ObjectInputStream(conteudoMsg); //Transforma em um Objeto
                msg = (Message) ois.readObject(); //Lê o objeto e joga no formato Message

                treatMsg(msg);
            }catch (SocketException e){ //captura qualquer erro do socket
            }catch (ClassNotFoundException | IOException e) { //erros de entrada}
            catch (InterruptedException e) { }
        }
    }

    private void treatMsg(Message msg){
        if(msg.isAck){
            for(Message i : filaMsg){ //Verifica a fila de mensagens pra encontrar a mensagem e adicionar o ACK
                if (i.getId() == msg.getId()){
                    i.addAck();
                    if(i.getAcks() == 3){
                        System.out.println(i.getId() + " " + i.getAcks());
                        filaMsg.remove(i);
                    }
                    break;
                }
            }
        }else{
            filaMsg.add(msg);
            sendAck(msg.getId, true);
        }
    }

    private void sendMsg(int id, boolean ack){

    }

    private void send(Message msg) {
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
        send(new Message(id, this.idNode, this.clockNode, true));
    }

    //serve pra manter o clock static e synchronized com toda as threads
    private void updateClock(long newClock){
        this.clockNode = Math.max(this.clockNode, newClock) + 1;
    }

    public void start(){
        threadEnvio.start();
        threadRecibo.start();
    }

    public synchronized void stop(){
        threadEnvio.interrupt();
        threadRecibo.interrupt();
    }

    public int getId(){
        return this.idNode;
    }
}
