package Messages;

public class SimpleMessageDataChecker {
	
	public static boolean validMessage(String type, byte []data){
		if(type.equals(MessageType.MSG_DO_WANT_PLAY)){
			return genericCheckMessage(MessageContent.DO_WANT_TO_PLAY_CHK_STRING, data);
		}else if(type.equals(MessageType.MSG_I_WANT_TO_PLAY)){
			return genericCheckMessage(MessageContent.I_WANT_TO_PLAY, data);
		}
		else return false;
	}
	
	private static boolean genericCheckMessage(final String checkString, byte [] data){
		return checkString.equals(new String(data));
	}
	
	public static byte[] getDataForMessage(String type){
		String msg="NULL";
		if(type.equals(MessageType.MSG_DO_WANT_PLAY)){
			msg = MessageContent.DO_WANT_TO_PLAY_CHK_STRING;
		}else if(type.equals(MessageType.MSG_I_WANT_TO_PLAY)){
			msg = MessageContent.I_WANT_TO_PLAY;
		}
		byte [] data = msg.getBytes();
		if(data.length < 100){
			byte [] buff  = new byte[117];
			for(int i = 0;i<data.length;i++){
				buff[i] = data[i];
			}
			data = buff;
		}
		return data;
	}

}
