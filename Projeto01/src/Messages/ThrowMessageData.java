package Messages;

/**
 * Classe que representa os dados de uma mensagem de aposta do Player
 * @author lucas
 *
 */
public class ThrowMessageData {
	public final String aposta;
	public final boolean valid;
	
	public ThrowMessageData(String aposta){
		this.aposta = aposta;
		this.valid = true;
	}
	
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
	
	public byte[] toByteArray(){
		String str = "Aposte:<"+this.aposta;
		return str.getBytes();
	}
	

}
