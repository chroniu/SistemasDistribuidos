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
		System.out.println(obj.rentBook(2, Client.getIntance()));
	}
}
