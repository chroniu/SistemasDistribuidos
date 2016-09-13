package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

import javax.crypto.NoSuchPaddingException;

import Messages.GameMessageData;
import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import Messages.ThrowMessageData;
import util.Util;

public class GameServer implements Role {
	private GameState gameState;
	private final String identification;
	private final PrivateKey privateKey;

	private int state;

	private final int STATE_WAITING_FOR_PLAYERS = 1;
	private final int STATE_GAME_STARTED = 2;
	private final int STATE_NEXT_PLAYER = 3 ;
	private final int STATE_WAITING_JOGADA = 4;
	private final int STATE_GAME_ENDED = 5;
	private long sleepTime;
	
	public GameServer(String identification, PrivateKey privateKey) {
		this.identification = identification;
		this.privateKey = privateKey;
		this.sleepTime = 0;
	}

	public void run() {
		while (true) {
			try{
			
			switch (this.state) {
			case STATE_WAITING_FOR_PLAYERS:
				stateWaitingForPlayers();
				break;
			case STATE_GAME_STARTED:
				stateGameStarted();
				break;
			case STATE_NEXT_PLAYER:
				stateGameNextPlayer();
				break;
			case STATE_WAITING_JOGADA:
				stateWaitingJogada();
				break;
			case STATE_GAME_ENDED:
				stateGameEnded();
				break;
			default:
				break;
			}
			Thread.sleep(sleepTime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	
	

	private void stateGameEnded() {
		// TODO Auto-generated method stub
		
	}

	private void stateWaitingJogada() {
		this.sleepTime = 1000;  ///TODO colocar um timer pra se o jogador não mandar, perder a vez, mandando uma mensagen ThowReply
		
	}

	private void stateGameNextPlayer() throws NoSuchAlgorithmException, NoSuchPaddingException {
		String nextPlayerId = this.gameState.getNextPlayer();
		
		Message msg = new Message(this.identification, nextPlayerId, MessageType.MSG_TURN, 
				new GameMessageData(MessageType.MSG_TURN, this.gameState.getDecoder()).toByteArray());
		msg.encryptMessage(privateKey);
		MultiCastServer.getInstance().sendMessage(msg);
		
		this.sleepTime = 1000;
		state = STATE_WAITING_JOGADA;
	}

	private void stateGameStarted() throws NoSuchAlgorithmException, NoSuchPaddingException {
		 
		this.sleepTime = 1000;
		this.gameState.getNextPlayer();
		Message msg = new Message(
				this.identification,
				MessageType.DEST_ALL,
				MessageType.MSG_GAME_START,
				new GameMessageData(MessageType.MSG_GAME_START, this.gameState.getDecoder()).toByteArray());
		msg.encryptMessage(privateKey);
		MultiCastServer.getInstance().sendMessage(msg);
		state =  STATE_NEXT_PLAYER;
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
			if(state!=STATE_WAITING_JOGADA) return; 
			
			if (msg.sender.equals(this.gameState.currentPlayerIdentification())){
				Util.log("Checking Message Credentials");
		 		msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
				ThrowMessageData messageData = new ThrowMessageData(msg.data);
		 		if (messageData.valid) {
					Util.log("Message is Valid");
					boolean acertou = gameState.updateState(messageData.aposta, msg.sender);
					
					Message replyMsg;
					try {
						replyMsg = new Message(
								this.identification,
								msg.sender,
								MessageType.MSG_THROW_REPLY,
								new GameMessageData((acertou? "Acertou": "Errou"), this.gameState.getDecoder()).toByteArray());
						
						replyMsg.encryptMessage(privateKey);
						MultiCastServer.getInstance().sendMessage(replyMsg);
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					state = STATE_NEXT_PLAYER;
					checkEndOfGame();	
					Util.log("User " + msg.sender + " apostou"+messageData.aposta);
				} else {
					Util.log("Message is not valid - Wrong keys?");
				}
				
				
			}else{
				Util.log("Mensagem Throw de usuário incorreto: ");
			}
		} else if (msg.type.equals(MessageType.MSG_I_WANT_TO_PLAY)) {
			if(state != STATE_WAITING_FOR_PLAYERS) return;
			
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

	private void checkEndOfGame() {
		if(this.gameState.gameEnded()){
			state = STATE_GAME_ENDED;
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
