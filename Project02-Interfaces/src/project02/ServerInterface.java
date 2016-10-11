package project02;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote{
	ArrayList<Book> getBookList() throws RemoteException;
	
	ServerMessage rentBook(int id, ClientInterface client) throws RemoteException;
	
	ServerMessage rebookBook(int id, ClientInterface client) throws RemoteException;
	
	ServerMessage reserveBook(int id, ClientInterface client) throws RemoteException;

	int giveBackBook(int id, ClientInterface client) throws RemoteException;
}
