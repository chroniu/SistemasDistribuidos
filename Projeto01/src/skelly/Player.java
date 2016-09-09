package skelly;

import Messages.MessageType;
import util.Util;

public class Player implements Role{
	
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
		if(msg.type == MessageType.MSG_KNOW){
			SystemUsersList.processIdentityMessage(msg);
		}else if (msg.type == MessageType.MSG_TURN){
			
		}else if (msg.type == MessageType.MSG_THROW_REPLY){
			
		}else if (msg.type == MessageType.MSG_GAME_START){

		}else if (msg.type == MessageType.MSG_GAME_ENDED){
		
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
