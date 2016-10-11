package project02;

import java.util.HashMap;
import java.util.Map;

public class ReservedBookControl {
	private final Map<Long, ClientInterface> booksReserved;
	
	public ReservedBookControl(){
		this.booksReserved = new HashMap<Long, ClientInterface>();
	}
	
	boolean tryToReserveBook(ClientInterface cli, long bookId){
		return false
		
	}
	
	
}
