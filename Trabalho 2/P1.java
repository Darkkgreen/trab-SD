//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.TreeSet;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;

public class P1{
    private static int idNode;
    private static int port;
    private static String ip = "localhost";
    private static Clock clock;
    private static TreeSet<Message> filaMsg;
    private static int qtdACK[] = new int[100];
    private static int recurso = 1; //Igual para todos 
    //Lista para guardar os recursos q o Processo está usando
    private static ArrayList<Integer> recursoEmUso = new ArrayList<Integer>();
    //Lista para guardar os recursos q o Processo está querendo usar, que requisitou
    //Guarda a mensagem, pois esta contem o time e o recurso
    private static ArrayList<Message> queroUsar = new ArrayList<Message>();

    private static ArrayList<Message> requisicao = new ArrayList<Message>();
    

    public static void main(String[] args) throws IOException{
        int i, idMsg;
        idNode = 1;
        port = 9000+idNode;
        filaMsg = new TreeSet<Message>(new MessageComp());
        clock = new Clock(idNode);

        //Inicializa o vetor de acks
            for(i=0; i<10;i++) qtdACK[i] = 0;

        //Coloca o processo para receber mensagens
        receiveMsg();
        try{
            Thread.sleep(4000);
        }catch(Exception e){}

        //Envia mensagem para os outros processos pedindo recurso
        sendMsgMult(idNode, idNode, clock.getValor(), recurso, Message.MSG);

        //Como aconteceu um evento, o clock é incrementado
        clock.incrementaClock();
    }

    public static void receiveMsg(){
        (new Thread(){
            @Override
            public void run(){
                String conteudoMsg = null;
                try{
                    ServerSocket socketRecebimento = new ServerSocket(port);
                    while(true){
                        Socket aux = socketRecebimento.accept();
                        BufferedReader bfRecebido;
                        //Pega o texto que recebeu no socket
                        bfRecebido = new BufferedReader(new InputStreamReader(aux.getInputStream()));
                        conteudoMsg = bfRecebido.readLine();

                        try{//Para evitar concorrencia, pausa a thread.
                            Thread.sleep(4000);
                        }catch(Exception e){}
                        //Envia o texto da msg para tratamento em outra thread.
                        treatMsg(conteudoMsg);
                    }
                }catch(IOException e){e.printStackTrace();}

            }
        }).start();
    }

    public static void sendMsgMult(int idMsg, int idSender, int clock, int rec, int tipo ){
        int portaDestino1 = port;
        int portaDestino2 = 9002;
        int portaDestino3 = 9003;
        Message msg = new Message(idMsg, idSender, clock, recurso, tipo);
        sendTo(portaDestino1, msg);
        sendTo(portaDestino2, msg);
        sendTo(portaDestino3, msg);
        if(tipo == Message.MSG){
            //Indico que quero usar o recurso
            queroUsar.add(msg);
            System.out.println("Mensagem "+idMsg+" enviada por multicasting pelo P"+idSender+". Pediu acesso ao recurso: "+rec);
       }
    }

    public static void sendTo(final int destino, final Message msg){
        (new Thread(){
            @Override
            public void run(){
                String conteudoMsg = null;
                Socket socketEnvio = null;
                BufferedWriter bfEnvio = null;

                if(msg.getTipo() == Message.ACK)
                    conteudoMsg = "ACK-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getRecurso();
                else if(msg.getTipo() == Message.MSG)
                    conteudoMsg = "MSG-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getRecurso();
                else if(msg.getTipo() == Message.NACK)
                    conteudoMsg = "NACK-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getRecurso();
                try{
                    socketEnvio = new Socket(ip, destino);
                    bfEnvio = new BufferedWriter( new OutputStreamWriter(socketEnvio.getOutputStream()));

                    bfEnvio.write(conteudoMsg);
                    bfEnvio.newLine();
                    bfEnvio.flush();

                }catch(IOException e){e.printStackTrace();}
            }
        }).start();
    }

    public static void treatMsg(final String conteudoMsg){
        (new Thread(){
            @Override
            public void run(){
                String palavraMsg[];
                Message msg;
                int idMsg, idSender, timeMsg, recursoMsg, tipo;
                //Boolean ack = false;
                tipo = -1;
                palavraMsg = conteudoMsg.split("-");
                //Guarda as informações recebidas da msg em strings separadas
                if(palavraMsg[0].equals("ACK"))
                    tipo = Message.ACK;
                if(palavraMsg[0].equals("NACK"))
                    tipo = Message.NACK;
                if(palavraMsg[0].equals("MSG"))
                    tipo = Message.MSG;

                idMsg = Integer.parseInt(palavraMsg[1]);
                idSender = Integer.parseInt(palavraMsg[2]);
                timeMsg = Integer.parseInt(palavraMsg[3]);
                recursoMsg = Integer.parseInt(palavraMsg[4]);

                //Atualiza o valor do clock
                clock.ajustaClock(timeMsg);
                //Caso a mensagem seja um ack.
                if(tipo == Message.ACK){
                   //recebeu um ack: incrementa o relogio
                    clock.incrementaClock();
                    qtdACK[recursoMsg]++;
                    //System.out.println("P"+idNode+" recebeu "+qtdACK[recursoMsg]+" ACKs do Recurso "+recursoMsg+". Tempo:"+timeMsg);
                    System.out.println("P"+idNode+" recebeu ok do P"+idSender+". Ja tenho "+qtdACK[recursoMsg]+" ACKs do Recurso "+recursoMsg+". Tempo:"+timeMsg);
                    //verifica se o recurso já recebeu 3 acks;
                    if(qtdACK[recursoMsg] == 3){
                        clock.incrementaClock();
                        System.out.println("P"+idNode+" recebeu todos os acks do recurso "+recursoMsg+"(time: "+timeMsg+").");
                        System.out.println("Recurso "+recursoMsg+" liberado para aplicacao");
                        usaRecurso(recursoMsg);
                        qtdACK[recursoMsg] = 0;
                    }
                }else if(tipo == Message.MSG){

                    if(idSender != idNode){

                        if(!estaUsandoRecurso(recursoMsg) && !queroUsarRecurso(recursoMsg)){
                            clock.incrementaClock();
                            //Manda um ACK para liberar o recurso para o remetente
                            msg = new Message(idMsg, idNode, clock.getValor(), recursoMsg, Message.ACK);
                            sendTo(idSender+9000, msg);
                            //sendMsgMult(idMsg, idNode, clock.getValor(), recursoMsg, true); 
                            System.out.println("Ok. P"+idNode+" diz para P"+idSender+": use o recurso "+recursoMsg);                       
                        }
                        else if(!estaUsandoRecurso(recursoMsg) && queroUsarRecurso(recursoMsg)){
                            if(tempoRequisicaoRecurso(recursoMsg) > timeMsg){
                                clock.incrementaClock();
                                //Manda um ACK para liberar o recurso para o remetente
                                msg = new Message(idMsg, idNode, clock.getValor(), recursoMsg, Message.ACK);
                                sendTo(idSender+9000, msg);
                                //sendMsgMult(idMsg, idNode, clock.getValor(), recursoMsg, true); 
                                System.out.println("Ok. P"+idNode+" diz para P"+idSender+": use o recurso "+recursoMsg);                       
                           }else{
                                clock.incrementaClock();
                                msg = new Message(idMsg, idSender, clock.getValor(), recursoMsg, Message.NACK);
                                //Adiciona a requisicao
                                //Manda um ACK para liberar o recurso para o remetente
                                requisicao.add(msg);
                                sendTo(idSender+9000, msg);
                           }
                        } 
                        else if(estaUsandoRecurso(recursoMsg)){
                            clock.incrementaClock();
                            msg = new Message(idMsg, idSender, clock.getValor(), recursoMsg, Message.NACK);
                            //Adiciona a requisicao
                            //Manda um ACK para liberar o recurso para o remetente
                            requisicao.add(msg);
                            sendTo(idSender+9000, msg);
                        }
                    } else { //Se eu mesmo mandei a msg, eu me permito
                        clock.incrementaClock();
                        //Manda um ACK para liberar o recurso para o remetente
                        msg = new Message(idMsg, idSender, clock.getValor(), recursoMsg, Message.ACK);
                        sendTo(idSender+9000, msg);
                        //sendMsgMult(idMsg, idNode, clock.getValor(), recursoMsg, true); 
                        System.out.println("Ok. P"+idNode+" diz para P"+idSender+": use o recurso "+recursoMsg);                       
                    }


                    /*System.out.println("P"+idNode+" recebeu a MSG"+idMsg+". Tempo:"+timeMsg);
                    //Monta a msg para colocar na fila
                    msg = new Message(idMsg, idSender, timeMsg, recursoMsg, false);
                    clock.incrementaClock();
                    if(filaMsg.add(msg)){
                        clock.incrementaClock();
                        //envia ACK para todos
                        sendMsgMult(idMsg, idNode, clock.getValor(), recursoMsg, true);

                    } else System.out.println("Erro ao inserir a msg "+msg.getId()+" na fila.");
                    */
                }
            }
        }).start();
    }

    public static void usaRecurso(final int rec){
        recursoEmUso.add(rec);
        for(Message msg : queroUsar){
            if(msg.getRecurso() == rec){
                queroUsar.remove(msg);
                break;
            }
        }

        try{
            Thread.sleep(2000);
        }catch(Exception e){}

        recursoEmUso.remove((Integer)rec);
        System.out.println("P"+idNode+" diz: Parei de usar o recurso "+rec);

        //Mandar um ACK da utilizacao do recurso 
        for (Iterator<Message> i = requisicao.iterator(); i.hasNext();) {
            Message msg = i.next();
            if(msg.getRecurso() == rec){
                msg.setTipo(Message.ACK);
                int aux = msg.getSenderId();
                msg.setSenderId(idNode);
                sendTo(aux+9000, msg);
                i.remove();
            }
        }

    }
    public static boolean estaUsandoRecurso(final int rec){
        return recursoEmUso.contains(rec);
    }
   public static boolean queroUsarRecurso(final int rec){
        for(Message msg : queroUsar){
            if(msg.getRecurso() == rec)
                return true;
        }
        return false;
   }
   public static int tempoRequisicaoRecurso(final int rec){
        for(Message msg : queroUsar){
            if(msg.getRecurso() == rec)
                return msg.getClock();
        }
        return 1000;
   }

}
