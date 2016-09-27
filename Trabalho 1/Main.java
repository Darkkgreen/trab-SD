package com.company;

import java.io.IOException;

import static java.lang.Thread.sleep;

/*

Nome							RA
André Domingues Vieira 			511420
Mateus de Abreu Antunes			612618

*/



public class Main {

    public static void main(String[] args) {
        try {
			
			/*Aqui crio os 3 clientes que vao trocar menssagens*/
            Node n = new Node();
            Node n2 = new Node();
            Node n3 = new Node();
			
			//inicia as threads
			
            n.start();
            n2.start();
            n3.start();


            n3.send();
            n.send();
            n2.send();
            n.send();
            n3.send();
            n3.send();
            n.send();
            n.send();
            n2.send();
            sleep(4000);

            n.stop();
            n2.stop();
            n3.stop();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
