package project02;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class Main {
	public static void main(String args[]) throws MalformedURLException, RemoteException, NotBoundException {
		ServerInterface obj = (ServerInterface) Naming.lookup("//localhost/Server");

		List<Book> strList = obj.getBookList();
		System.out.println(obj.rentBook(1, Client.getIntance()));
		System.out.println(obj.rentBook(2, Client.getIntance()));
		System.out.println(obj.rentBook(3, Client.getIntance()));
		System.out.println("Rent Max: " + obj.rentBook(4, Client.getIntance()));
		System.out.println("Give Back: " + obj.giveBackBook(1, Client.getIntance()));
		System.out.println("Rent 1: "+obj.rentBook(1, Client.getIntance()));
		
	}
}
