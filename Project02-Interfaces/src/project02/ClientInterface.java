package project02;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
	Interface do cliente
*/
public interface ClientInterface extends Remote{
	/**
	Notifica que um livro está disponível
	@param bookid -> id do livro que tornou-se disponível
	@param time   -> tempo que o cliente possui de reserva exclusiva para o livro @bookid
	 */
	void notifyBookAvaliable(long bookid, long time) throws RemoteException;
}