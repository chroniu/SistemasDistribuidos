package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.NoSuchPaddingException;

import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import util.Util;

public class GameServer implements Role {
	private GameState gameState;
	private final String identification;
	private final PrivateKey privateKey;
	
	public GameServer(String identification, PrivateKey privateKey){
		this.identification = identification;
		this.privateKey = privateKey;
	}
	
	public void run() {
		while(true){
			try {
			
				Thread.sleep(500);
				 try {
					 ///TODO -> Não pode deixar assim, só pra teste
						Message msg = new Message(this.identification, MessageType.DEST_ALL, MessageType.MSG_DO_WANT_PLAY, SimpleMessageDataChecker.getDataForMessage(MessageType.MSG_DO_WANT_PLAY));
						msg.encryptMessage(privateKey);
						MultiCastServer.getInstance().sendMessage(msg);
					 }catch(Exception e){
						e.printStackTrace();
					}
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
		}else if (msg.type.equals(MessageType.MSG_THROW)){
			
		}else if (msg.type.equals(MessageType.MSG_THROW_FAIL)){
			
		}else if (msg.type.equals(MessageType.MSG_I_WANT_TO_PLAY)){
			Util.log("Checking Message Credentials");
			Util.log("Cheking User Existence: "+SystemUsersList.getUserPublicKey(msg.sender));
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			if(SimpleMessageDataChecker.validMessage(msg.type, msg.data)){
				Util.log("Message is Valid");
				
				Util.log("User "+ msg.sender+" Want to Play");
			}else{
				Util.log("Message is not valid - Wrong keys?");
			}
		}
		
		
	}

	public void startExecution() {
		this.gameState = new GameState("Seleciona Palavra");
	
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User DIscoverd By Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);
		 	
		 try {
			Message msg = new Message(this.identification, identification, MessageType.MSG_DO_WANT_PLAY, SimpleMessageDataChecker.getDataForMessage(MessageType.MSG_DO_WANT_PLAY));
			msg.encryptMessage(privateKey);
			MultiCastServer.getInstance().sendMessage(msg);
		 }catch(Exception e){
			e.printStackTrace();
		}
	
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed From Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);	
	}
}
