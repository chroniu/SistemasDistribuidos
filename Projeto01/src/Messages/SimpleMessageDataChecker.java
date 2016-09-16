package Messages;

/**
 * Classe stática que armazena todos os tipos de mensagens
 * @author Lucas
 */

public class SimpleMessageDataChecker {
	
	/**
	 * Método estático que verifica se uma mensagem é valida
	 * @param type      String com o tipo
	 * @param data      Byte [] data
	 * @return boolean
	 */
	public static boolean validMessage(String type, byte []data){
		if(type.equals(MessageType.MSG_DO_WANT_PLAY)){
			return genericCheckMessage(MessageContent.DO_WANT_TO_PLAY_CHK_STRING, data);
		}else if(type.equals(MessageType.MSG_I_WANT_TO_PLAY)){
			return genericCheckMessage(MessageContent.I_WANT_TO_PLAY, data);
		}
		else return false;
	}
	
	/**
	 * Método estático que verifica se o vetor de bytes está correto
	 * @param checkString     String com a frase/palavra que será comparada
	 * @param data      Byte [] data
	 * @return boolean
	 */
	private static boolean genericCheckMessage(final String checkString, byte [] data){
		return checkString.equals(new String(data));
	}
	
	/**
	 * Método estático que gera um vetor de bytes a partir do tipo de mensagem selecionado
	 * @param type      String com o tipo
	 * @return byte[]
	 */
	public static byte[] getDataForMessage(String type){
		String msg="NULL";
		if(type.equals(MessageType.MSG_DO_WANT_PLAY)){
			msg = MessageContent.DO_WANT_TO_PLAY_CHK_STRING;
		}else if(type.equals(MessageType.MSG_I_WANT_TO_PLAY)){
			msg = MessageContent.I_WANT_TO_PLAY;
		}
		byte [] data = msg.getBytes();
		 
		return data;
	}

}
