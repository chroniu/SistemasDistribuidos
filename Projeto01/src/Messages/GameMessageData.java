package Messages;

import util.Util;

/**
 * Classe para as mensagens dos tipos:
 * GameStarted;
 * GameEndend;
 * ThrowReply;
 * Turn.
 * @author Lucas
 */

public class GameMessageData {
	public final String serverMessage;
	public final GameStateDecEncoder state;
	

	/**
	 * Construtor 
	 * @param messageType  string com o tipo da mensagem
	 * @param state    GameStateDecEnconder com o estado do jogo
	 */
	public GameMessageData(String messageType, GameStateDecEncoder state){
		this.serverMessage = messageType;
		this.state = state;
	}
	
	/**
	 * Construtor 
	 * @param data  Byte[] data
	 */
	public GameMessageData(byte [] data){
		this.serverMessage = new String(Util.range(data, 0, 20)).trim();
		this.state = new GameStateDecEncoder(Util.range(data, 20));
	}
	
	/**
	 * Metodo que transforma o GameStateDecEncoder em um array de bytes 
	 * @return  byte[]
	 */
	public byte []toByteArray(){
		byte [] stateData = this.state.toByteArray();
		byte [] buff = new byte[20 + stateData.length];
		byte [] messagexType = this.serverMessage.getBytes();
		
		for(int i=0; i<20; i++){
			buff[i] = ( i <messagexType.length? messagexType[i]:0);
		}
		
		System.arraycopy(stateData, 0, buff, 20, stateData.length);

		
		return buff;
	}
	

}
