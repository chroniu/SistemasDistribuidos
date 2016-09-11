package skelly;

import Messages.MessageType;
import util.Util;

public class GameServer implements Role {
	private GameState gameState;
	
	
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
		}else if (msg.type.equals(MessageType.MSG_THROW)){
			
		}else if (msg.type.equals(MessageType.MSG_THROW_FAIL)){
			
		}else{
			//mensagem não reconhecida
		}
		
		
	}

	public void startExecution() {
		this.gameState = new GameState("Seleciona Palavra");
	
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User DIscoverd By Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed From Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);	
	}
}
