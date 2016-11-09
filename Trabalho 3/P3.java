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

public class P3{
    private static int idNode;
    private static int port;
    private static String ip = "localhost";
    private static Clock clock;
    private static TreeSet<Message> filaMsg;
    private static int qtdACK[] = new int[100];
    //Lista para guardar os PIDs dos processos existentes
    //No caso, usamos P1, P2, P3, P4, P5.
    private static int idsProcessos[] = new int[6];
    private static int coordenadorAtual;
    
    public static void main(String[] args) throws IOException{
        int i, idMsg;
        idNode = 3;
        port = 9000+idNode;
        coordenadorAtual = 5; //Inicia com o 5 sendo o coordenador
        filaMsg = new TreeSet<Message>(new MessageComp());
        clock = new Clock(idNode);

        //Inicializa o vetor de acks
        for(i=0; i<10;i++) qtdACK[i] = 0;
        //Vetor q guarda os ids dos Processos
        for(i=0; i<5;i++) idsProcessos[i] = i+1;


        //Coloca o processo para receber mensagens
        receiveMsg();
        try{
            Thread.sleep(2000);
        }catch(Exception e){}

        //Envia mensagem para os outros processos pedindo eleicao
        solicitaEleicao(idNode, clock.getValor(), coordenadorAtual);

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
                        //treatMsg(conteudoMsg);
                    }
                }catch(IOException e){e.printStackTrace();}

            }
        }).start();
    }

    public static void sendMsgMult(int idMsg, int idSender, int clock, int coord, int tipo ){
        int portaDestino1 = 9001;
        int portaDestino2 = 9002;
        int portaDestino3 = 9003;
        int portaDestino4 = 9004;
        int portaDestino5 = 9005;
        
        Message msg = new Message(idMsg, idSender, clock, coord, tipo);
        sendTo(portaDestino1, msg);
        sendTo(portaDestino2, msg);
        sendTo(portaDestino3, msg);
        sendTo(portaDestino4, msg);
        sendTo(portaDestino5, msg);
        
        if(tipo == Message.NOVO_COORD){
            System.out.println("P"+idSender+" declara que o novo coordenador eh: "+coord); 
        }
    }

    public static void sendTo(final int destino, final Message msg){
        (new Thread(){
            @Override
            public void run(){
                String conteudoMsg = null;
                Socket socketEnvio = null;
                BufferedWriter bfEnvio = null;

                if(msg.getTipo() == Message.ELEICAO)
                    conteudoMsg = "ELEICAO-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getCoordenador();
                else if(msg.getTipo() == Message.OK)
                    conteudoMsg = "OK-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getCoordenador();
                else if(msg.getTipo() == Message.NOVO_COORD)
                    conteudoMsg = "NOVO_COORD-"+msg.getId()+"-"+msg.getSenderId()+"-"+msg.getClock()+"-"+msg.getCoordenador();
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
                int idMsg, idSender, timeMsg, coordMsg, tipo;
                //Boolean ack = false;
                tipo = -1;
                palavraMsg = conteudoMsg.split("-");
                //Guarda as informações recebidas da msg em strings separadas
                if(palavraMsg[0].equals("ELEICAO"))
                    tipo = Message.ELEICAO;
                if(palavraMsg[0].equals("NOVO_COORD"))
                    tipo = Message.NOVO_COORD;
                if(palavraMsg[0].equals("OK"))
                    tipo = Message.OK;

                idMsg = Integer.parseInt(palavraMsg[1]);
                idSender = Integer.parseInt(palavraMsg[2]);
                timeMsg = Integer.parseInt(palavraMsg[3]);
                coordMsg = Integer.parseInt(palavraMsg[4]);

                //Atualiza o valor do clock
                clock.ajustaClock(timeMsg);
                //Caso a mensagem seja um ack.
                if(tipo == Message.ELEICAO){
                    clock.ajustaClock(timeMsg);
                    //Quando processo recebe msg de eleição de membros com número mais baixo
                    //Envia OK para remetente para indicar que está vivo e convoca eleição
                    if(idNode > idSender){
                      enviaOK(idMsg, clock.getValor(), coordMsg, idSender);  
                    }
                }else if(tipo == Message.OK){
                   //recebeu um ack: incrementa o relogio
                    clock.incrementaClock();
                    qtdACK[idMsg]++;
                    System.out.println("Recebi um OK de P"+idSender+". Encerro minha atividade.");

                }else if(tipo == Message.NOVO_COORD){
                    coordenadorAtual = coordMsg;
                }
            }
        }).start();
    }
    //Solicita eleicao para os processos com ids maiores que o dele.
    public static void solicitaEleicao(int idMsg, int clock, int coord){
        /*  envia uma mensagem ELEIÇÃO para todos os processos com números 
            mais altos. Se nenhum responder, 
            P vence a eleição e se torna coordenador
        */
        int i;
        int novoCoord;
        Message m = new Message(idMsg, idNode, clock, coord, Message.ELEICAO);
        if(idNode != 5) 
            System.out.print("P"+idNode+" convoca uma eleicao para: P"); 
        for(i = 0; i<5; i++){
            if(idsProcessos[i] > idNode){
                sendTo(i+9000, m);
                System.out.print(i+" "); 
            }
        }
        if(!recebeuResposta(idMsg)){
            novoCoord = idNode;
            avisaNovoCoord(idMsg,idNode, clock, novoCoord);
        }
    }
    //Espera receber os algum OK por 5 segundos
    public static boolean recebeuResposta(int idMsg){
        System.out.println("Aguardando respostas por 5 seg.");
        try{
            Thread.sleep(5000);
        }catch(Exception e){}

        int i, cont = 0;

        if(qtdACK[idMsg] == 0){
            clock.incrementaClock();
            System.out.println("P"+idNode+" nao recebeu respostas. Solicitara uma nova eleicao");
            qtdACK[idMsg] = 0;
            return false;
        }
        else return true;
    }

    public static void avisaNovoCoord(int idMsg, int idSender, int clock, int idCoordenador){
        sendMsgMult(idMsg, idSender, clock, idCoordenador, Message.NOVO_COORD);
    }
    //Envia Ok para quem solicitou a eleicao com PID menor.
    public static void enviaOK(int idMsg, int clock, int coord, int remetente){
        Message m = new Message(idMsg, idNode, clock, coord, Message.OK);
        sendTo(remetente+9000, m);   
    }


}
