import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Archive extends Remote{
	boolean uploadArchive(byte [] byteArchive, String nome) throws RemoteException;
}