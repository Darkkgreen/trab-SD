import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.io.*;

public class Client{
	private Client(){}
	private Archive stub;

	public void criaStub(String host){
		try {
			Registry registry = LocateRegistry.getRegistry(host); 
			this.stub = (Archive) registry.lookup("Archive");
		}catch (Exception e){
			System.out.println("Problema criando stub");
		}
	}

	public boolean enviaArquivo(String nome){
		FileInputStream fis = null;
		File arquivo = new File("./"+ nome);
		boolean retorno = false;

		byte [] byteArquivo = new byte[(int) arquivo.length()];

		try{
			fis = new FileInputStream(arquivo);
			fis.read(byteArquivo);
			fis.close();

			retorno = this.stub.uploadArchive(byteArquivo, nome);
		}catch (Exception e){
			System.out.println("Problema com o envio do arquivo");
		}

		System.out.println("Arquivo enviado com sucesso!");
		return retorno;
	}

	public static void main(String[] args) { 
		String host = (args.length < 1) ? null : args[0];
		String arquivo = (args.length < 2) ? null : args[1];
		Client cliente = new Client();

		cliente.criaStub(host);
		boolean estado = cliente.enviaArquivo(arquivo);

		if(estado == true)
			System.out.println("Arquivo " + arquivo + " enviado com sucesso");
		else
			System.out.println("Problema ao enviar arquivo");
 	}
} 