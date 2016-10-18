package project02;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
Interface do servidor
*/
public interface ServerInterface extends Remote{
	/**
	Retorna a lista de livros
	*/
	ArrayList<Book> getBookList() throws RemoteException;
	
	
	/**
	Solicita o emprestimo do livro @bookid, para o cliente @client	
	@param bookId -> id do livro
	@param client -> Referência do Cliente
	*/
	ServerMessage rentBook(long bookId, ClientInterface client) throws RemoteException;
	
	 
	/**
	Solicita a renovação do livro @bookid, para o cliente @client	
	@param bookId -> id do livro
	@param client -> Referência do Cliente
	*/
	ServerMessage rebookBook(long bookId, ClientInterface client) throws RemoteException;

	/**
	Solicita a reserva do livro @bookid, para o cliente @client		
	@param bookId -> id do livro
	@param client -> Referência do Cliente
	*/
	ServerMessage reserveBook(long bookId, ClientInterface client) throws RemoteException;
	
	
	/**
	Solicita a devolução do livro @bookid, pelo cliente @client	
	@param bookId -> id do livro
	@param client -> Referência do Cliente
	*/
	long giveBackBook(long bookId, ClientInterface client) throws RemoteException;

}
