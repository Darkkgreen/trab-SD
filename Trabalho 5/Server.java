import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class Server implements Hello{
	public Server(){}
	public String sayHello(){
		return "hello, world!";
	}

	public static void main(String args[]){
		try{
			Server obj = new Server();
			Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.blind("Hello", stub);
			System.err.println("Server ready");
		} catch(Exception e){
			Syste.err.println("Server exception: "+ e.toString());
			e.printStackTrace();
		}
	}
}
