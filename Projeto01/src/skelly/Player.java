package skelly;

import java.security.PrivateKey;
import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import util.Util;

/**
 * Implementa o papel de um Jogador
 * @author lucas
 *
 */
public class Player implements Role{
	private final String identification;
	private final PrivateKey privateKey;
	
	private final int STATE_WAITING_GAME_START = 1;
	private final int STATE_WAITING_TURN = 2;
	private final int STATE_JOGADA = 3;
	private final int STATE_WAITING_REPLY_FROM_SERVER = 4;
	private final int STATE_GAME_ENDED = 5;
	private int state;
	
	
	public Player(String identification, PrivateKey privateKey){
		this.identification = identification;
		this.privateKey = privateKey;
	}
	
	public void run() {
		while(true){
			try {
				switch (state) {
				case STATE_WAITING_GAME_START:
					
					break;
				case STATE_WAITING_TURN:
					
					break;
				case STATE_JOGADA:
					
					break;
				case STATE_WAITING_REPLY_FROM_SERVER:
					
					break;
				case STATE_GAME_ENDED:
					
					break;

				default:
					break;
				}
				Thread.sleep(100);
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void receivedMsg(Message msg) {
		if(!msg.type.equals(MessageType.MSG_KNOW)){
		 Util.log("Received Message from: "+msg.sender+" not secured checked yet");
		 Util.log("Type Message from: "+msg.type);
		}
		if(SystemUsersList.getUserPublicKey(msg.sender)==null &&(!msg.type.equals(MessageType.MSG_KNOW) )){
			Util.log("User Not Yet Know. Ignoring Message");
			return ;
		}
		
		if(msg.type.equals(MessageType.MSG_KNOW)){
			SystemUsersList.processIdentityMessage(msg);
		}else if (msg.type.equals(MessageType.MSG_TURN)){
			if(state != STATE_WAITING_TURN)  return;
			
		}else if (msg.type.equals(MessageType.MSG_THROW_REPLY)){
			if(state != STATE_WAITING_REPLY_FROM_SERVER)  return;
			
		}else if (msg.type.equals(MessageType.MSG_DO_WANT_PLAY)){
			if(state != STATE_WAITING_GAME_START) return;
			
				
			Util.log("Checking Message Credentials");
			Util.log("Cheking User Existence: "+SystemUsersList.getUserPublicKey(msg.sender));
			

			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			
			if(SimpleMessageDataChecker.validMessage(msg.type, msg.data)){
				Util.log("Message is Valid");
		  	 try {
					Message msg_reply = new Message(this.identification, msg.sender,
							 MessageType.MSG_I_WANT_TO_PLAY, 
							 SimpleMessageDataChecker.getDataForMessage(MessageType.MSG_I_WANT_TO_PLAY));
					msg_reply.encryptMessage(this.privateKey);
					MultiCastServer.getInstance().sendMessage(msg_reply);
				 } catch (Exception e){
					e.printStackTrace();
				}
			}else{
				Util.log("Message is not valid - Wrong keys?");
			}
			

		}else if (msg.type.equals(MessageType.MSG_GAME_ENDED)){
		
		}else{
			//mensagem não reconhecida
		}
	}

	public void startExecution() { 
		state = STATE_WAITING_GAME_START;
		
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User Discoverd");
		 Util.log("id: "+identification+"  type: "+typeSys);
		
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed");
		 Util.log("id: "+identification+"  type: "+typeSys);	}

}
