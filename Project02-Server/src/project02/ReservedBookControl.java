package project02;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ReservedBookControl {
	/**
	Classe interna, usada apenas como uma struct para o Map de livros reservados.
	*/
	class A{
		public final ClientInterface c;
		public final Optional<Long> initTime;
		public A(ClientInterface c, Optional initTime) {
			super();
			this.c = c;
			this.initTime = initTime;
		}
	}
	//bookId, who     
	private final Map<Long, A> booksReserved;
	private final Map<ClientInterface, Long> clientDeception;
	
	
	public ReservedBookControl(){
		this.booksReserved   = new HashMap<Long, A>();
		this.clientDeception = new HashMap<ClientInterface, Long>(); 
	}
	
	/**
	 * penaliza o @client
	 */
	public void penalizer(ClientInterface client, long penalization){
	//	long v = this.clientDeception.get(client) == null? 0 : System.currentTimeMillis() - this.clientDeception.get(client).longValue();  
				//
		this.clientDeception.put(client, penalization);
	}
	
	/**
	Checa se o tempo atual menos @time já ultrapassou o tempo @bigTime 
	*/
	private boolean isTimePassed(final long time, final long bigTime){
		return System.currentTimeMillis() - time < bigTime;
	}
	
	/**
	Confere as condições de penalização do cliente @cli e status do livro @bookid para saber se a reserva 
	pode ser concedida.
	
	*/
	ServerMessage tryToReserveBook(ClientInterface cli, long bookId){
		if(clientDeception.containsKey(cli)){
			if(isTimePassed(clientDeception.get(cli),Config.TIME_PENALIZATION)){
				return ServerMessage.PENALISATION_ON;
			} 
			this.clientDeception.remove(cli);
		}
		
		if(this.booksReserved.containsKey(bookId)){
			
			if(!this.booksReserved.get(bookId).initTime.isPresent() && !isTimePassed(this.booksReserved.get(bookId).initTime.get(), Config.TIME_RESERVADO)){
				return ServerMessage.ALREADY_RESERVED;
			}
			this.booksReserved.remove(bookId);
			this.booksReserved.put(bookId, new A(cli, Optional.empty()));
		}
				
		this.booksReserved.put(bookId, new A(cli, Optional.empty()));
		System.out.println("Book "+ bookId + " reserved to "+ cli); 
		return ServerMessage.OPERATION_SUCESSFULL;
	}
	
	/**
	Checa se existem usuários aguardando a devolução do livro @bookid, e caso existam, retorna o usuário
	*/
	Optional<ClientInterface> giveBackBook(long bookId, ClientInterface client){
		if(!this.booksReserved.containsKey(bookId))
			return Optional.empty();
		
		if(!this.booksReserved.get(bookId).initTime.isPresent()){
			this.booksReserved.put(bookId, new A(this.booksReserved.get(bookId).c, Optional.of(System.currentTimeMillis())));
			return Optional.of(this.booksReserved.get(bookId).c);
		}	
		return Optional.empty();			
	}
	
	/**
	Confere as condições de penalização do cliente @cli e status do livro @bookid para saber se o empréstimo 
	pode ser concedido.
	*/
	ServerMessage canClientBorrowBook(ClientInterface cli, long bookId){
		if(this.clientDeception.containsKey(cli)){
			System.out.println("Client has penalization: time "+ clientDeception.get(cli) );
			
			if(isTimePassed(clientDeception.get(cli),Config.TIME_PENALIZATION)){
				return ServerMessage.PENALISATION_ON;
			} 
			this.clientDeception.remove(cli);
		}
		
		if(this.booksReserved.containsKey(bookId)){
			if(this.booksReserved.get(bookId).c.equals(cli))
				return ServerMessage.OPERATION_SUCESSFULL;
			else return ServerMessage.ALREADY_RESERVED;
		}
		return ServerMessage.OPERATION_SUCESSFULL;
	}		
}
