package project02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
Classe auxiliar para armazenar os livros que cada cliente possui atualmente
*/
class ClientHasBooks{
	/**
	O cliente
	*/
	final ClientInterface owner; 
	/*
	Os livros que o @owner possui emprestados e o timestamp de quando emprestou  
	[0] -> bookId
	[1] -> timestamp
	*/
	final ArrayList<Long []> books;
	
	
	public ClientHasBooks( ClientInterface owner){
		this.owner = owner;
		this.books = new ArrayList<Long []>(3);
	}
	
	public boolean hasBook(int id){
		return books.stream().anyMatch(x -> x[0] == id);
	}
	
	public int howManyBooks(){
		return this.books.size();
	}
	
	/**
	 * The client rented the book with the id
	 * @param id
	 */
	public void addBook(long id){
		Long [] b = new Long[2]; 
		b[0] = id;
		b[1] = System.currentTimeMillis();
		this.books.add(b);
	}
	
	
	public boolean isGiveBackTimeOver(){
		for (Long [] book : this.books){
			if (isTimePassed(book[1], Config.TIME_EMPRESTADO))
				return true;
		}
		return false;
	}
	
	
	private boolean isTimePassed(final long time, final long bigTime){
		return System.currentTimeMillis() - time > bigTime;
	}
	/**
	 * Removes the book from the client and return the time that the book was rented;
	 * @param id
	 * @return
	 */
	public long removeBook(long id){
		Long [] elem = this.books.stream().filter(x -> x[0] == id).findFirst().get();
		books.remove(elem);
		return elem[1];
	}
}


/**
Classe que armazena os livros, os livros emprestados por cada cliente  e possui métodos para a manipulação dos dados
*/
public class Library {
	
	
	private final Map<Book, ClientHasBooks> bookList;
	private final ArrayList<ClientHasBooks> clientsBooksList;
	
	
	public Library(){
		this.bookList = new HashMap<Book, ClientHasBooks>();
		this.clientsBooksList = new ArrayList<ClientHasBooks>();
		loadBooksLibrary();
		
	}
	/*
	Retorna a lista de livros
	*/
	public ArrayList<Book> getBookList(){
		return  (new ArrayList<Book>(this.bookList.keySet()));
//		Book [] books = (Book[]) //(this.bookList.entrySet().stream().filter(x -> true).toArray());
//		return (ArrayList<Book>) Arrays.asList(books);
	}
	
	/**
	 * Load books from File "books.txt"
	 */
	private void loadBooksLibrary(){
		this.bookList.clear();
		int id=1;
		try {
			for (String line : Files.readAllLines(Paths.get("books.txt"))) {
//				String args[] = line.split("+");
//				this.bookList.put(new Book(Integer.parseInt(args[0]), args[1]), null);
				this.bookList.put(new Book( id++, line), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	Adiciona o livro @book à biblioteca
	@param book o livro a ser adicionado
	*/
	public void addBook(Book book){
		this.bookList.put(book, null);
	}
	
	/**
	Verifica se o livro @bookId pode ser emprestado
	@param bookId -> id do livro para pesquisa
	*/
	public boolean isBookAvaliable(int bookId){
		try{
			return this.bookList.entrySet().stream().filter(x -> x.getKey().id == bookId).findFirst().get().getValue() == null;
		}catch(Exception e){
			System.out.println("Book is not registered in the library: "+bookId);
			return false;
		}
	}
	
	
	
	/**
	 * Classe assume que o livro @bookId não está reservado e que o cliente @client não possui penalizações
	 * @param client
	 * @param bookId
	 * return retorna o status da operação
	 */
	public ServerMessage rentBook(ClientInterface client, long bookId){
			try{
				Entry<Book, ClientHasBooks> elem = this.bookList.entrySet().stream().filter(x -> x.getKey().id == bookId).findFirst().get();
				if(elem.getValue() == null){
					Optional<ClientHasBooks> e = this.clientsBooksList.stream().filter(x -> x.owner.equals(client)).findFirst();
					    if( e.isPresent() ){
					    if(e.get().howManyBooks() > Config.MAX_BOOKS){
							return ServerMessage.MAX_BOOK_REACHED;
						}
						if(e.get().isGiveBackTimeOver()){
							return ServerMessage.GIVE_BACK_BOOKS;
						}
						e.get().addBook(bookId);
						elem.setValue(e.get());
					}else{
						ClientHasBooks clientHasBooks = new ClientHasBooks(client);
						clientHasBooks.addBook(bookId);
						this.clientsBooksList.add(clientHasBooks);
						elem.setValue(clientHasBooks);
					}
					//TODO Implementar metodo de renovacao

					   return ServerMessage.OPERATION_SUCESSFULL;
				}else if(elem.getValue().owner.equals(client)){
					//AQUI ELE RENOVA
					Optional<ClientHasBooks> e = this.clientsBooksList.stream().filter(x -> x.owner.equals(client)).findFirst();					
					if(e.get().isGiveBackTimeOver()){
						return ServerMessage.GIVE_BACK_BOOKS;
					}
					//elem.getValue()
					e.get().removeBook(bookId);
					//ClientHasBooks clientHasBooks = new ClientHasBooks(client);
					//clientHasBooks.addBook(bookId);
					//this.clientsBooksList.add(clientHasBooks);
					//elem.setValue(clientHasBooks);
					e.get().addBook(bookId);
					return ServerMessage.RENEWED_SUCESSFULLY;
				}				
				else{
					//livro já está rented
					return ServerMessage.ALREADY_BOOKED;
				}
			}catch (Exception e) {
				e.printStackTrace();
				return ServerMessage.BOOK_DONT_EXIST;
	
			}
	}
	
	/**
	Realiza a devolução do livro @bookId à biblioteca
	return o timestamp do empréstimo do livro 
	*/
	public long giveBackBooks(long bookId, ClientInterface client){
		long penalty=0;
		Entry<Book, ClientHasBooks> elem = this.bookList.entrySet().stream().filter(x -> x.getKey().id == bookId).findFirst().get();
		if(elem.getValue() == null){
			System.out.println("Nao esta emprestado");
			return 0;
			//Nao esta emprestado
		}
		else{
			if(!elem.getValue().owner.equals(client)){
				return 0;
			}
			
			long e=0;
			System.out.println("Tentando receber...");
			for(int i = 0; i < elem.getValue().books.size(); i++){
			       if(  elem.getValue().books.get(i)[0] == bookId){
			               e = elem.getValue().books.get(i)[1];
			   			System.out.println("Livro Achado...");
						
			               if(System.currentTimeMillis() - e > Config.TIME_EMPRESTADO){
	       		
			            	   penalty=((System.currentTimeMillis()-e)/Config.TIME_EMPRESTADO)*Config.TIME_PENALIZATION;
				       			System.out.println("Penalidade: "+penalty);			
				       			
			               }
			               else{
			            	   System.out.println("Sem penalidade");
			               }
			       }
			}
			Optional<ClientHasBooks> aux = this.clientsBooksList.stream().filter(x -> x.books.equals(elem.getValue().books)).findFirst();
			//elem.getValue()
			aux.get().removeBook(bookId);
			elem.setValue(null);
		}
		return penalty;
	
	}
}