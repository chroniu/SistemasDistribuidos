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
		System.out.println("Requested Books List");
		return this.libraryManager.getBookList();
	}

	@Override
	public ServerMessage rentBook(long bookid, ClientInterface client)  throws RemoteException{
		System.out.println("Requested Rent Book "+bookid+" Client "+client);
		return this.libraryManager.rentBook(bookid, client);
	}

	@Override
	public ServerMessage rebookBook(long bookid, ClientInterface client)  throws RemoteException{
		System.out.println("Requested Rebook Book "+bookid+" Client "+client);

		return this.libraryManager.rebookBook(bookid, client);

	}

	@Override
	public ServerMessage reserveBook(long bookid, ClientInterface client) throws RemoteException{
		System.out.println("Requested Reserve Book "+bookid+" Client "+client);

		return this.libraryManager.reserveBook(bookid, client);

	}

	@Override
	public long giveBackBook(long bookid, ClientInterface client) throws RemoteException{
		System.out.println("Requested Give Back Book"+bookid+" Client "+client);

		return 0;
	}

}
