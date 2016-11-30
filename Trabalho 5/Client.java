import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import java.io.*;

public class Client{
	private CLient(){}

	public boolean enviaArquivo(Archive stub, String nome){
		FileInputStream fis = null;
		File arquivo = new File("./"+ nome);
		boolean retorno = false;

		byte[] byteArquivo = new byte[(int) arquivo.length()];

		try{
			fis = new FileInputStream(arquivo);
			fis.read(byteArquivo);
			fis.close();

			retorno = this.stub.uploadArchive(byteArquivo, nome);
		}catch{
			System.out.println("Problema com o envio do arquivo");
		}

		return retorno;
	}

	public static void main(String[] args) { 
		String host = (args.length < 1) ? null : args[0];
		String arquivo = (args.length < 2) ? null : args[1];

		try { 
			Registry registry = LocateRegistry.getRegistry(host); 
			Archive stub = (Archive) registry.lookup("Archive");

			boolean estado = enviaArquivo(stub, arquivo);

			if(estado == true)
				System.out.prinlnt("Arquivo " + arquivo + " enviado com sucesso");
			else
				System.out.println("Problema ao enviar arquivo");
		} catch (Exception e) { 
			System.err.println("Client exception: " + e.toString()); 
			e.printStackTrace(); 
		}
 	}
} 