package project02;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
 
/**
Classe que inicializa e lança o servidor
*/
public class Main {
	public static void main(String args[]) throws RemoteException, MalformedURLException{
		System.out.println("RMI Server started");
//		JFXPanel f = new JFXPanel();	
		try { //special exception handler for registry creation
            LocateRegistry.createRegistry(1099); 
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }
           
        //Instantiate RmiServer

        Server obj = new Server();

        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/Server", obj);
        System.out.println("PeerServer bound in registry");
        while(true);
	}
}
