package skelly;

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
		SystemUsersList.processIdentityMessage(msg);
	}

	public void startExecution() {
		// TODO Auto-generated method stub
		
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User DIscoverd By Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed From Game Server");
		 Util.log("id: "+identification+"  type: "+typeSys);	}

}
