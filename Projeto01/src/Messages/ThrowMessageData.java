package Messages;

/**
 * Classe que representa os dados de uma mensagem de aposta do Player
 * @author lucas
 */

public class ThrowMessageData {
	public final String aposta;
	public final boolean valid;
	
	/**
	 * Construtor
	 * @param aposta  String com a aposta
	 */
	public ThrowMessageData(String aposta){
		this.aposta = aposta;
		this.valid = true;
	}
	
	/**
	 * Construtor
	 * @param data  Byte[] data
	 */
	public ThrowMessageData(byte [] data){
		String str = new String(data);
		if(str.startsWith("Aposte:<")){
			this.aposta = str.substring("Aposte:<".length());
			this.valid = true;
		}else{
			this.aposta = "ERRO";
			this.valid = false;
		}
	}
	
	/**
	 * Metodo que transforma o ThrownMessageData em um array de bytes 
	 * @return  byte[]
	 */
	public byte[] toByteArray(){
		String str = "Aposte:<"+this.aposta;
		return str.getBytes();
	}
	

}
