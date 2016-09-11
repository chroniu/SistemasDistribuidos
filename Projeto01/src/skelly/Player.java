package skelly;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.NoSuchPaddingException;

import Messages.MessageType;
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

		if(msg.type.equals(MessageType.MSG_KNOW)){
			SystemUsersList.processIdentityMessage(msg);
		}else if (msg.type.equals(MessageType.MSG_TURN)){
			
		}else if (msg.type.equals(MessageType.MSG_THROW_REPLY)){
			
		}else if (msg.type.equals(MessageType.MSG_GAME_START)){

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
		 if(typeSys == MessageType.MSG_SERVER){
			 try {
				Message msg = new Message(this.identification, identification,
						 MessageType.MSG_WANT_TO_PLAY, 
						 new WantPlayData().toByteArray());
				msg.encryptMessage(this.privateKey);
				MultiCastServer.getInstance().sendMessage(msg);
			 } catch (Exception e){
				e.printStackTrace();
			}
		 
		 }
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed");
		 Util.log("id: "+identification+"  type: "+typeSys);	}

}
