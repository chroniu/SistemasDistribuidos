package skelly;

import java.security.PrivateKey;
import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import util.Util;

public class GameServer implements Role {
	private GameState gameState;
	private final String identification;
	private final PrivateKey privateKey;

	private int state;

	private final int STATE_WAITING_FOR_PLAYERS = 1;
	private final int STATE_GAME_STARTED = 2;

	public GameServer(String identification, PrivateKey privateKey) {
		this.identification = identification;
		this.privateKey = privateKey;
	}

	public void run() {
		while (true) {

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (this.state) {
			case STATE_WAITING_FOR_PLAYERS:
				stateWaitingForPlayers();
				break;
			case STATE_GAME_STARTED:
				//stateGameStarted();
				break;
			default:
				break;
			}

		}

	}

	private void stateWaitingForPlayers() {
		if(gameState.getNumPlayers() >=2){
			state=STATE_GAME_STARTED;
		}
		
		if (SystemUsersList.getTypeUserList().size() >= 2) {
			try {
				for (String destination : SystemUsersList.getTypeUserList()) {
					Message msg = new Message(
							this.identification,
							destination,
							MessageType.MSG_DO_WANT_PLAY,
							SimpleMessageDataChecker
									.getDataForMessage(MessageType.MSG_DO_WANT_PLAY));
					msg.encryptMessage(privateKey);
					MultiCastServer.getInstance().sendMessage(msg);
					
				}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void receivedMsg(Message msg) {
		Util.log("Received Message from: " + msg.sender
				+ " not secured checked yet");
		Util.log("Type Message from: " + msg.type);

		if (msg.type.equals(MessageType.MSG_KNOW)) {
			SystemUsersList.processIdentityMessage(msg);
		} else if (msg.type.equals(MessageType.MSG_THROW)) {

		} else if (msg.type.equals(MessageType.MSG_THROW_FAIL)) {

		} else if (msg.type.equals(MessageType.MSG_I_WANT_TO_PLAY)) {
			Util.log("Checking Message Credentials");
			Util.log("Cheking User Existence: "
					+ SystemUsersList.getUserPublicKey(msg.sender));
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			if (SimpleMessageDataChecker.validMessage(msg.type, msg.data)) {
				Util.log("Message is Valid");
				gameState.addUserToGame(msg.sender);
				Util.log("User " + msg.sender + " Want to Play");
			} else {
				Util.log("Message is not valid - Wrong keys?");
			}
		}

	}

	public void startExecution() {
		this.gameState = new GameState("Seleciona Palavra");
		this.state = STATE_WAITING_FOR_PLAYERS;
	}

	public void newUserDiscovered(String identification, String typeSys) {
		Util.log("New User DIscoverd By Game Server");
		Util.log("id: " + identification + "  type: " + typeSys);

		try {
			Message msg = new Message(this.identification, identification,
					MessageType.MSG_DO_WANT_PLAY,
					SimpleMessageDataChecker
							.getDataForMessage(MessageType.MSG_DO_WANT_PLAY));
			msg.encryptMessage(privateKey);
			MultiCastServer.getInstance().sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void userRemoved(String identification, String typeSys) {
		Util.log("User Removed From Game Server");
		Util.log("id: " + identification + "  type: " + typeSys);
	}
}
