package skelly;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.NoSuchPaddingException;

import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import Messages.WantPlayData;
import util.Util;

public class Player implements Role{
	private final String identification;
	private final PrivateKey privateKey;
	
	public Player(String identification, PrivateKey privateKey){
		this.identification = identification;
		this.privateKey = privateKey;
	}
	
	public void run() {
		while(true){
			try {
			
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
			
		}else if (msg.type.equals(MessageType.MSG_THROW_REPLY)){
			
		}else if (msg.type.equals(MessageType.MSG_DO_WANT_PLAY)){
			Util.log("Checking Message Credentials");
			Util.log("Cheking User Existence: "+SystemUsersList.getUserPublicKey(msg.sender));
			
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			
			if(SimpleMessageDataChecker.validMessage(msg.type, msg.data)){
				Util.log("Message is Valid");
		  	 try {
					Message msg_reply = new Message(this.identification, identification,
							 MessageType.MSG_I_WANT_TO_PLAY_CHK_STRING, 
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
		// TODO Auto-generated method stub
		
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User Discoverd");
		 Util.log("id: "+identification+"  type: "+typeSys);
		
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed");
		 Util.log("id: "+identification+"  type: "+typeSys);	}

}
