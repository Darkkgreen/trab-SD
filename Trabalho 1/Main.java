import java.io.IOException;

import static java.lang.Thread.sleep;

public class Main {

    public static void main(String[] args) {
        Random gerador;
        int valor, i;

        try {
            Node a = new Node();
            Node b = new Node();
            Node c = new Node();
			
            a.start();
            b.start();
            c.start();
 
            for(i = 0; i < 10; i++){
                numero = gerador.nextInt(3);
                switch(numero){
                    case 0:
                        a.send();
                        break;
                    case 1:
                        b.send();
                        break;
                    case 2:
                        c.send();
                }
            }
            
            sleep(4000);

            a.stop();
            b.stop();
            c.stop();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
