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

public class P2{
    private static int idNode;
    private static int port;
    private static String ip = "localhost";
    private static Clock clock;
    private static TreeSet<Message> filaMsg;
    private static int qtdACK[] = new int[100];

    public static void main(String[] args) throws IOException{
        int i, idMsg;
        idNode = 2;
        port = 6790;
        filaMsg = new TreeSet<Message>(new MessageComp());
        clock = new Clock(idNode);

        //Inicializa o vetor de acks
            for(i=0; i<100;i++) qtdACK[i] = 0;

        //Coloca o processo para receber mensagens
        receiveMsg();
        try{
            Thread.sleep(4000);
        }catch(Exception e){}

        idMsg = 0;
        //Envia mensagem para os outros processos
        sendMsgMult(idNode, idNode, clock.getValor(), false);
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

    public static void sendMsgMult(int idMsg, int idSender, int clock, boolean ack){
        int portaDestino1 = port;
        int portaDestino2 = 6789;
        int portaDestino3 = 6791;
        Message msg = new Message(idMsg, idSender, clock, ack);
        sendTo(portaDestino1, msg);
        sendTo(portaDestino2, msg);
        sendTo(portaDestino3, msg);
        if(!ack)
           System.out.println("Mensagem "+idMsg+" enviada por multicasting pelo P"+idSender);
        else
           System.out.println("Ack "+idMsg+" enviado por multicasting pelo P"+idSender);
    }

    public static void sendTo(final int destino, final Message msg){
        (new Thread(){
            @Override
            public void run(){
                String conteudoMsg;
                Socket socketEnvio;
                BufferedWriter bfEnvio;

                if(msg.isAck())
                    conteudoMsg = "ACK-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock();
                else
                    conteudoMsg = "MSG-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock();
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
                int idMsg, idSender, timeMsg;
                Boolean ack = false;

                palavraMsg = conteudoMsg.split("-");
                //Guarda as informações recebidas da msg em strings separadas
                if(palavraMsg[0].equals("ACK"))
                    ack = true;
                idMsg = Integer.parseInt(palavraMsg[1]);
                idSender = Integer.parseInt(palavraMsg[2]);
                timeMsg = Integer.parseInt(palavraMsg[3]);
                //Atualiza o valor do clock
                clock.ajustaClock(timeMsg);
                //Caso a mensagem seja um ack.
                if(ack){
                    //recebeu um ack: incrementa o relogio
                    clock.incrementaClock();
                    qtdACK[idMsg]++;
                    System.out.println("P"+idNode+" recebeu "+qtdACK[idMsg]+" ACKs da Msg"+idMsg+". Tempo:"+timeMsg);
                    //verifica se o primeiro da fila já recebeu 3 acks;
                    if(!filaMsg.isEmpty()){
                        Message first = filaMsg.first();
                        if(qtdACK[first.getId()] == 3){
                            clock.incrementaClock();
                            System.out.println("P"+idNode+" recebeu todos os acks da MSG "+first.getId()+"(time: "+first.getClock()+").");
                            System.out.println("Mensagem "+first.getId()+" liberada para aplicacao");
                            filaMsg.remove(filaMsg.first());
                        }
                    }
                }else{

                    System.out.println("P"+idNode+" recebeu a MSG"+idMsg+". Tempo:"+timeMsg);
                    //Monta a msg para colocar na fila
                    msg = new Message(idMsg, idSender, timeMsg, false);
                    clock.incrementaClock();
                    if(filaMsg.add(msg)){
                        clock.incrementaClock();
                        //envia ACK para todos
                        sendMsgMult(idMsg, idNode, clock.getValor(), true);

                    } else System.out.println("Erro ao inserir a msg "+msg.getId()+" na fila.");
                }
            }
        }).start();
    }
}
