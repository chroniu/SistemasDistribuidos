package project02;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements ServerInterface{
	private final LibraryManager libraryManager;
	
	protected Server() throws RemoteException {
		super(0);
		this.libraryManager = new LibraryManager();
	}

	private static final long serialVersionUID = -2767611403655957831L;

	@Override
	public ArrayList<Book> getBookList()  throws RemoteException{
		return this.libraryManager.getBookList();
	}

	@Override
	public ServerMessage rentBook(long bookid, ClientInterface client)  throws RemoteException{
		return this.libraryManager.rentBook(bookid, client);
	}

	@Override
	public ServerMessage rebookBook(long bookid, ClientInterface client)  throws RemoteException{
		return this.libraryManager.rebookBook(bookid, client);

	}

	@Override
	public ServerMessage reserveBook(long bookid, ClientInterface client) throws RemoteException{
		return this.libraryManager.reserveBook(bookid, client);

	}

	@Override
	public long giveBackBook(long bookid, ClientInterface client) throws RemoteException{
		return this.libraryManager.giveBackBook(bookid, client);
	}

}
