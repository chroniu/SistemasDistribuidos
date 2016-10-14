package project02;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerInterface extends Remote{
	ArrayList<Book> getBookList() throws RemoteException;
	
	ServerMessage rentBook(long bookId, ClientInterface client) throws RemoteException;
	
	ServerMessage rebookBook(long bookId, ClientInterface client) throws RemoteException;
	
	ServerMessage reserveBook(long bookId, ClientInterface client) throws RemoteException;

	long giveBackBook(long bookId, ClientInterface client) throws RemoteException;

}
