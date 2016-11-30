import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Server implements Archive{
	public Server(){}
	
	public boolean uploadArchive(){
		
	}

	public static void main(String args[]){
		try{
			Server obj = new Server(); 
			Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);    
			Registry registry = LocateRegistry.getRegistry(); 
			System.setProperty("java.rmi.server.hostname","127.0.0.1");
			registry.bind("Hello", stub); 
			System.err.println("Server ready"); 
		} catch(Exception e){
			System.err.println("Server exception: "+ e.toString());
			e.printStackTrace();
		}
	}
}
