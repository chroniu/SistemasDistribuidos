package project02;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{
	void notifyBookAvaliable(Book book, long time) throws RemoteException;
}