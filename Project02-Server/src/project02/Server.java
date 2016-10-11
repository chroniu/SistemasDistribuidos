package project02;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements ServerInterface{
	private final Library library;
	
	protected Server() throws RemoteException {
		super(0);
		this.library = new Library();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2767611403655957831L;

	@Override
	public ArrayList<Book> getBookList() {
		return this.getBookList();
	}

	@Override
	public ServerMessage rentBook(int id, ClientInterface client) {
		return null;
	}

	@Override
	public ServerMessage rebookBook(int id, ClientInterface client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerMessage reserveBook(int id, ClientInterface client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int giveBackBook(int id, ClientInterface client) {
		// TODO Auto-generated method stub
		return 0;
	}

}
