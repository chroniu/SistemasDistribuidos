package project02;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
Implementa a interface do Cliente
É uma classe Singleton
*/
public class Client extends UnicastRemoteObject implements ClientInterface {
	protected Client() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	Referência ao servidor
	*/
	private ServerInterface server;
	/**
	Singleton do cliente
	
	*/
	private static Client instance;
	
	/**
	Retorna uma referência a instância do cliente
	*/
	public static Client getIntance(){
		if (instance == null){
			try {
				instance = new Client();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}
	
 	/**
	Requisita a de livros ao servidor
	*/
	public ArrayList<Book> requestBookList(){
		try {
			return this.server.getBookList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	Requisita o empréstimo do livro @id ao servidor
	*/
	public ServerMessage requestBorrowBook(long id){
		try {
			return this.server.rentBook(id, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return ServerMessage.ERROR;
	}
	
	
	/**
	Requisita a reserva do livro @bookid ao servidor
	*/
	public ServerMessage requestReserveBook(long id){
		try {
			return this.server.reserveBook(id, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return ServerMessage.ERROR;
	}
	
	
	public long requestgiveBackBook(long id){
		try {
			return this.server.giveBackBook(id, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	Tenta conectar no servidor localizado em //localhost/Server
	*/
	public boolean connectToServer() {
		try {
			this.server = (ServerInterface) Naming.lookup("//localhost/Server");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	Implementa o método de callback chamado pelo servidor.

	*/
	@Override
	public void notifyBookAvaliable(long bookid, long time) throws RemoteException {
		JOptionPane.showMessageDialog(null, "O livro " + bookid+ "está disponível"); 

	}


}
