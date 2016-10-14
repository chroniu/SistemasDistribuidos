package project02;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Client extends UnicastRemoteObject implements ClientInterface {
	protected Client() throws RemoteException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServerInterface server;
	private static Client instance;
	
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
	
 
	public ArrayList<Book> requestBookList(){
		try {
			return this.server.getBookList();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ServerMessage requestBorrowBook(long id){
		try {
			return this.server.rentBook(id, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return ServerMessage.ERROR;
	}
	
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
	
	
	public boolean connectToServer() {
		try {
			this.server = (ServerInterface) Naming.lookup("//localhost/Server");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

//	private ClientApplication clientApplication;

	@Override
	public void notifyBookAvaliable(long bookid, long time) throws RemoteException {
		JOptionPane.showMessageDialog(null, "O livro " + bookid+ "está disponível"); 
	}


//	public void setApplicationDialog(ClientApplication clientApplication) {
//		this.clientApplication = clientApplication;
//	}

}
