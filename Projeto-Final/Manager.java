import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;


public class Manager{

	public Manager(){}

	public void send(Path caminho){
		System.out.println("Foi criado o arquivo: "+caminho);
	}
	public void remove(Path caminho){
		System.out.println("Foi deletado o arquivo: "+caminho);
	}

}
