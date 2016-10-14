package project02;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Optional;

public class LibraryManager {
	private final Library library;
	private final ReservedBookControl reservedBookControl;
	public LibraryManager() {
		this.library = new Library();
		this.reservedBookControl = new ReservedBookControl();
	}
	
	public ArrayList<Book> getBookList() {
		return this.library.getBookList();
	}

	public synchronized ServerMessage rentBook(long bookid, ClientInterface client) {
		final ServerMessage reserved  = this.reservedBookControl.canClientBorrowBook(client, bookid);
		if(!reserved.equals(ServerMessage.OPERATION_SUCESSFULL))
			return reserved;
		
		return this.library.rentBook(client, bookid);
	}

	public synchronized ServerMessage rebookBook(long bookid, ClientInterface client) {
		final ServerMessage reserved  = this.reservedBookControl.canClientBorrowBook(client, bookid);
		if(!reserved.equals(ServerMessage.OPERATION_SUCESSFULL))
			return reserved;
		
		return this.library.rentBook(client, bookid);
	}

	public synchronized ServerMessage reserveBook(long bookid, ClientInterface client) {
		return this.reservedBookControl.tryToReserveBook(client, bookid);
	}

	public synchronized  long giveBackBook(long bookid, ClientInterface client) {
		long penalization = this.library.giveBackBooks(bookid, client);
		if (penalization > 0){
			this.reservedBookControl.penalizer(client, System.currentTimeMillis() );//penalization);
		}
		Optional<ClientInterface> c = this.reservedBookControl.giveBackBook(bookid,  client);
		 if(!c.isPresent()) return penalization;
		 try {
			c.get().notifyBookAvaliable(bookid, Config.TIME_BOOK_RESERVED);
		} catch (RemoteException e) {
			System.out.println("Client: "+ c.get()+" is not responding....");
		}
		 return penalization;
	}
}
