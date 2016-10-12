//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

import java.util.Comparator;

public class MessageComp implements Comparator<Message>{
	@Override
	public int compare(Message atual, Message nova){
		return atual.compTo(nova);
	}
}