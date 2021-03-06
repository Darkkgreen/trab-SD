import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.io.*;


public class Server implements Archive{
	public Server(){}
	
	public boolean uploadArchive(byte [] byteArchive, String nome){
		System.out.println("Transferindo arquivo para servidor: " + nome);

		try{
			FileOutputStream fos = new FileOutputStream("./teste/" + nome);
			fos.write(byteArchive);
			fos.close();
		}catch (IOException e){
			System.out.println("Erro ao transferir arquivo");
			return false;
		}

		System.out.println("Arquivo " + nome + " foi armazenado com sucesso");
		return true;
	}

	public static void main(String args[]){
		String host = (args.length < 1) ? null : args[0];

		try{
			Server obj = new Server(); 
			Archive stub = (Archive) UnicastRemoteObject.exportObject(obj, 0);    
			Registry registry = LocateRegistry.getRegistry(); 
			System.setProperty("java.rmi.server.hostname", host);
			if((Archive) registry.lookup("Archive") == null)
				registry.bind("Archive", stub); 
			System.err.println("Servidor pronto para recebimento"); 
		} catch(Exception e){
			System.err.println("Server exception: "+ e.toString());
			e.printStackTrace();
		}
	}
}
