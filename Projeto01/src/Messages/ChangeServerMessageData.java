package Messages;


public class ChangeServerMessageData {
	public final String newServer;
	public final boolean valid;
	
	/**
	 * Construtor
	 * @param newServer  String com a aposta
	 */
	public ChangeServerMessageData(String newServer){
		this.newServer = newServer;
		this.valid = true;
	}
	
	/**
	 * Construtor
	 * @param data  Byte[] data
	 */
	public ChangeServerMessageData(byte [] data){
		String str = new String(data);
		if(str.startsWith("Novo_Server:<")){
			this.newServer = str.substring("Novo_Server:<".length());
			this.valid = true;
		}else{
			this.newServer = "ERRO";
			this.valid = false;
		}
	}
	
	/**
	 * Metodo que transforma o ThrownMessageData em um array de bytes 
	 * @return  byte[]
	 */
	public byte[] toByteArray(){
		String str = "Novo_Server:<"+this.newServer;
		return str.getBytes();
	}
	

}

