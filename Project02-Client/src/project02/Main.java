package project02;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException{
		 ServerInterface obj = (ServerInterface)Naming.lookup("//localhost/Server");
	        System.out.println(obj.toString()); 
	}
}
