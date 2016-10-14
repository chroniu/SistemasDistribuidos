package project02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;


class ClientHasBooks{
	final ClientInterface owner;
	final ArrayList<Long []> books;
	
	public ClientHasBooks( ClientInterface owner){
		this.owner = owner;
		this.books = new ArrayList<Long []>();
	}
	
	
	@Override
	public String toString() {
		return "ClientHasBooks [owner=" + owner + ", books=" + books + ", howManyBooks()=" + howManyBooks() + "]";
	}


	public boolean hasBook(int id){
		return books.stream().anyMatch(x -> x[0] == id);
	}
	
	public int howManyBooks(){
		return this.books.size();
	}
	
	/**
	 * The client rent the book with the id
	 * @param id
	 */
	public void addBook(long id){
		Long [] b = new Long[2]; 
		b[0] = id;
		b[1] = System.currentTimeMillis();
		books.add(b);
	}
	
	/**
	 * Removes the book from the client and return the time that the book was rented;
	 * @param id
	 * @return
	 */
	public long removeBook(int id){
		Long [] elem = this.books.stream().filter(x -> x[0] == id).findFirst().get();
		books.remove(elem);
		return elem[1];
	}
}


public class Library {
	
	
	private final Map<Book, ClientHasBooks> bookList;
	private final ArrayList<ClientHasBooks> clientsBooksList;
	
	
	public Library(){
		this.bookList = new HashMap<Book, ClientHasBooks>();
		this.clientsBooksList = new ArrayList<ClientHasBooks>();
		loadBooksLibrary();
		
	}
	
	public ArrayList<Book> getBookList(){
		return new ArrayList<Book>(this.bookList.keySet());
	}
	
	/**
	 * Load books from File
	 */
	private void loadBooksLibrary(){
		this.bookList.clear();
		int id = 1;
		try {
			for (String line : Files.readAllLines(Paths.get("books.txt"))) {
			//	String args[] = line.split("+");
				this.bookList.put(new Book( id++, line), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addBook(Book book){
		this.bookList.put(book, null);
	}
	
	public boolean isBookAvaliable(int bookId){
		try{
			return this.bookList.entrySet().stream().filter(x -> x.getKey().id == bookId).findFirst().get().getValue() == null;
		}catch(Exception e){
			System.out.println("Book is not registered in the library: "+bookId);
			return false;
		}
	}
	
	/**
	 * Classe assume que o livro nﾃ｣o estﾃ｡ reservado e o cliente nﾃ｣o estﾃ｡ penalizado
	 * @param client
	 * @param bookId
	 * @return
	 */
	public ServerMessage rentBook(ClientInterface client, long bookId){
			try{
				Entry<Book, ClientHasBooks> elem = this.bookList.entrySet().stream().filter(x -> x.getKey().id == bookId).findFirst().get();
				if(elem.getValue() == null){
					Optional<ClientHasBooks> e = this.clientsBooksList.stream().filter(x -> x.owner.equals(client)).findFirst();
					if( e.isPresent() ){
						if(e.get().howManyBooks() >= Config.MAX_BOOKS){
							return ServerMessage.MAX_BOOK_REACHED;
						}
						e.get().addBook(bookId);
						elem.setValue(e.get());
					}else{
						ClientHasBooks clientHasBooks = new ClientHasBooks(client);
						clientHasBooks.addBook(bookId);
						this.clientsBooksList.add(clientHasBooks);
						elem.setValue(clientHasBooks);
					}
					//TODO checar GIVE_BACK_BOOK 
					//TODO Implementar método de renovação
					return ServerMessage.OPERATION_SUCESSFULL;
				}else{
					//livro jﾃ｡ estﾃ｡ rented
					return ServerMessage.ALREADY_BOOKED;
				}
			}catch (Exception e) {
				return ServerMessage.BOOK_DONT_EXIST;
				// TODO: handle exception
			}
	}
	
	public long giveBackBooks(long bookId){
		return 0;//TODO implementar
	}
}
