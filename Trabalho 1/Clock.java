//Implementação
//Ângela Rodrigues Ferreira - 552070
//Gustavo Almeida Rodrigues - 489999

public class Clock{
	private int valor; //Valor do tempo
	//private int acrescimo; //Cada node acrescenta uma qtd diferente, por exemplo, o node1 acrescenta 1, o Node2 acres. 2...  

	public Clock(int valor_inicial){
		this.valor = valor_inicial;
		//this.acrescimo = acrescimo;
	}	

	public void incrementaClock(){
		valor++;
	}
	/*	
		Quando um node recebe uma mensagem, ele verifica se o 
		clock dele eh menor do que o que a mensagem foi enviada,
		e então atualiza o seu valor. Se não for, apenas soma 1.
	*/
	public void ajustaClock(int tempoMsg){
		if(tempoMsg > this.valor)
			this.valor = tempoMsg;
		this.valor++;
	}

	public int getValor(){ return this.valor; }
	//public int getAcrescimo(){ return this.acrescimo; }
}