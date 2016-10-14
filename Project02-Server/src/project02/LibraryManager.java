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

	public ServerMessage rentBook(long bookid, ClientInterface client) {
		final ServerMessage reserved  = this.reservedBookControl.canClientBorrowBook(client, bookid);
		if(reserved != ServerMessage.OPERATION_SUCESSFULL)
			return reserved;
		
		return this.library.rentBook(client, bookid);
	}

	public ServerMessage rebookBook(long bookid, ClientInterface client) {
		final ServerMessage reserved  = this.reservedBookControl.canClientBorrowBook(client, bookid);
		if(reserved != ServerMessage.OPERATION_SUCESSFULL)
			return reserved;
		
		return this.library.rentBook(client, bookid);
	}

	public ServerMessage reserveBook(long bookid, ClientInterface client) {
		return this.reservedBookControl.tryToReserveBook(client, bookid);
	}

	public long giveBackBook(long bookid, ClientInterface client) {
		long penalization = this.library.giveBackBooks(bookid);
		Optional<ClientInterface> c = this.reservedBookControl.giveBackBook(bookid);
		 if(!c.isPresent()) return 0;
		 try {
			c.get().notifyBookAvaliable(bookid, Config.TIME_BOOK_RESERVED);
		} catch (RemoteException e) {
			System.out.println("Client: "+ c.get()+" is not responding....");
		}
		 return penalization;
	}
}
