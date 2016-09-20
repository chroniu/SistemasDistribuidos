package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.lang.System;
import javax.crypto.NoSuchPaddingException;

import Messages.ChangeServerMessageData;
import Messages.GameMessageData;
import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import Messages.ThrowMessageData;
import util.Configurations;
import java.util.Scanner;
import util.Util;

/**
 * Classe que implementa o papel de gerador de palavras
 * @author lucas
 */

public class GameServer implements Role {
	private GameState gameState;
	private final String identification;
	private final PrivateKey privateKey;
	private final ArrayList<RoleListener> myListeners;
	private int state;

	private final int STATE_WAITING_FOR_PLAYERS = 0;
	private final int STATE_GAME_STARTED = 2;
	private final int STATE_NEXT_PLAYER = 3 ;
	private final int STATE_WAITING_JOGADA = 4;
	private final int STATE_GAME_ENDED = 5;
	private final int STATE_ROLE_CHANGED = 6;
	
	private boolean first_try = true;
	private long sleepTime;
	private long timerVerify;
	
	
	/**
	 * Construtor
	 * @param identification    String com a identificação
	 * @param privateKey        PrivateKey chave privade
	 */
	public GameServer(String identification, PrivateKey privateKey) {
		this.identification = identification;
		this.privateKey = privateKey;
		this.sleepTime = 0;
		this.timerVerify = -1;
		this.myListeners = new ArrayList<RoleListener>();
	}

	/**
	 * Método que indica o estado que o servidor vai estar
	 */
	public void run() {
		Util.log("Inicializando Thread GameServer", Configurations.OUT_INTERFACE);
		
		boolean continuar = true;
		while (continuar) {
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
				//startExecution();
				break;
			case STATE_ROLE_CHANGED:
				sendChangeRoleMessage();
				this.changeRole();
				continuar = false;
			default:
				break;
			}
			Util.log("STATE: "+this.state, Configurations.OUT_INTERFACE);
			Thread.sleep(sleepTime);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Util.log("Finalizando Thread GameServer", Configurations.OUT_INTERFACE);

	}
	
	/**
	 * Método que avisa que o o jogo acabou
	 */
	private void stateGameEnded() { 
		try{
			
			Message msg = new Message(this.identification, MessageType.DEST_ALL, MessageType.MSG_GAME_ENDED, 
					new GameMessageData("VENCEDOR:" +this.gameState.winnerPlayerIdentification(),
							this.gameState.getDecoder()).toByteArray());
			msg.encryptMessage(privateKey);
			MultiCastServer.getInstance().sendMessage(msg);
			Util.log("Sending Reply Message to: "+msg.receiver+ " Game_ENDED", Configurations.OUT_INTERFACE);
			Thread.sleep(Configurations.TIMER_GAME_ENDED);
			this.state = STATE_ROLE_CHANGED;
			this.sleepTime = 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		
	
	}

	/**
	 * Método que fica esperando a jogada do jogador 
	 */
	private void stateWaitingJogada() {	
		if((System.currentTimeMillis() - this.timerVerify ) > (this.first_try? Configurations.MAX_TIME_TO_PLAY_LETTER : Configurations.MAX_TIME_TO_PLAY_WORD ))	{	
			try{
				if (this.first_try){
					this.gameState.perdeuVez();
					
				}
				
				if(!this.gameState.userIsInGame(this.gameState.currentPlayerIdentification())){
					Message replyMsg;
					try {
						replyMsg = new Message(
								this.identification,
								this.gameState.currentPlayerIdentification(),
								MessageType.MSG_GAME_ENDED,
								new GameMessageData("Demorou: Perdeu", this.gameState.getDecoder()).toByteArray());
						replyMsg.encryptMessage(privateKey);
						MultiCastServer.getInstance().sendMessage(replyMsg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Message msg = new Message(this.identification, this.gameState.currentPlayerIdentification(), MessageType.MSG_THROW_REPLY_WORD, 
							new GameMessageData(this.first_try?"PERDEU A VEZ":"PASSOU A VEZ", this.gameState.getDecoder()).toByteArray());
					msg.encryptMessage(privateKey);
					MultiCastServer.getInstance().sendMessage(msg);
					Util.log("Sending Reply Message to: "+msg.receiver+ " Perdeu a vez", Configurations.OUT_INTERFACE);	
				}
				this.first_try=false;
			}catch(Exception e){
				e.printStackTrace();
			}
				this.state = STATE_NEXT_PLAYER;
				this.sleepTime = 1;
		}else this.sleepTime = 1000;  
	}

	/**
	 * Método que avisa o proximo jogador que é a vez dele jogar
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	private void stateGameNextPlayer() throws NoSuchAlgorithmException, NoSuchPaddingException {
		String nextPlayerId;
		Message msg;
		if(!this.first_try){
			nextPlayerId = this.gameState.getNextPlayer();
			this.first_try=true;
			msg = new Message(this.identification, nextPlayerId, MessageType.MSG_TURN, 
					new GameMessageData(MessageType.MSG_TURN, this.gameState.getDecoder()).toByteArray());
			msg.encryptMessage(privateKey);
			MultiCastServer.getInstance().sendMessage(msg);
			Util.log("Sending Turn Message to: "+msg.receiver, Configurations.OUT_INTERFACE);

		}
		else{
			this.first_try=false;
		}
		
		this.sleepTime = 1000;
		this.timerVerify = System.currentTimeMillis();
		state = STATE_WAITING_JOGADA;
		checkEndOfGame();
	}

	/**
	 * Método que avisa que o jogo começou
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
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

	/**
	 * Método que o servidor fica esperando atingir o número mínimo de jogadores para iniciar o jogo
	 */
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

	/**
	 * Método que indica o que o servidor tem que fazer com base na mensagem recebida
	 * @param msg    Message mensagem
	 */
	public void receivedMsg(Message msg) {
		Util.log("Received Message from: " + msg.sender
				+ " not secured checked yet", Configurations.OUT_INTERFACE);
		Util.log("Type Message from: " + msg.type, Configurations.OUT_INTERFACE);

		if (msg.type.equals(MessageType.MSG_KNOW)) {
			SystemUsersList.processIdentityMessage(msg);
		} else if (msg.type.equals(MessageType.MSG_THROW)) {
			if(state!=STATE_WAITING_JOGADA) return; 
			
			if (msg.sender.equals(this.gameState.currentPlayerIdentification())){
				Util.log("Checking Message Credentials", Configurations.OUT_INTERFACE);
		 		msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
				ThrowMessageData messageData = new ThrowMessageData(msg.data);
		 		if (messageData.valid) {
					Util.log("Message is Valid", Configurations.OUT_INTERFACE);
					boolean acertou = gameState.updateState(messageData.aposta, msg.sender);
					Util.log("Acertou? "+acertou, Configurations.OUT_INTERFACE);
					if(this.gameState.userIsInGame(this.gameState.currentPlayerIdentification())){
						
						Message replyMsg;
						try {
							replyMsg = new Message(
									this.identification,
									msg.sender,
									(this.first_try? MessageType.MSG_THROW_REPLY_LETTER  : MessageType.MSG_THROW_REPLY_WORD),
									new GameMessageData((acertou? "Acertou": "Errou"), this.gameState.getDecoder()).toByteArray());
									
							replyMsg.encryptMessage(privateKey);
							MultiCastServer.getInstance().sendMessage(replyMsg);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						Message replyMsg;
						try {
							replyMsg = new Message(
									this.identification,
									msg.sender,
									MessageType.MSG_GAME_ENDED,
									new GameMessageData("Errou demais: Perdeu", this.gameState.getDecoder()).toByteArray());
							
							replyMsg.encryptMessage(privateKey);
							MultiCastServer.getInstance().sendMessage(replyMsg);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					state = STATE_NEXT_PLAYER;
					checkEndOfGame();	
					Util.log("User " + msg.sender + " apostou"+messageData.aposta, Configurations.OUT_INTERFACE);
				} else {
					Util.log("Message is not valid - Wrong keys?", Configurations.OUT_INTERFACE);
				}
				
				
			}else{
				Util.log("Mensagem Throw de usuário incorreto: ", Configurations.OUT_INTERFACE);
			}
		} else if (msg.type.equals(MessageType.MSG_I_WANT_TO_PLAY)) {
			if(state != STATE_WAITING_FOR_PLAYERS) return;
			
			Util.log("Checking Message Credentials", Configurations.OUT_INTERFACE);
			Util.log("Cheking User Existence: "
					+ SystemUsersList.getUserPublicKey(msg.sender), Configurations.OUT_INTERFACE);
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			if (SimpleMessageDataChecker.validMessage(msg.type, msg.data)) {
				Util.log("Message is Valid", Configurations.OUT_INTERFACE);
				gameState.addUserToGame(msg.sender);
				Util.log("User " + msg.sender + " Want to Play", Configurations.OUT_INTERFACE);
			} else {
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_INTERFACE);
			}
		}

	}

	/**
	 * Método que verifica se o jogo acabou
	 */
	private void checkEndOfGame() {
		if(this.gameState.gameEnded()){
			state = STATE_GAME_ENDED;
		}
	}

	/**
	 * Método que inicia a partida
	 */
	public void startExecution() {
		String palavraCorreta ="";
		Scanner s = new Scanner(System.in);		
		Util.log("Entre com a palavra correta:", Configurations.OUT_INTERFACE);
		palavraCorreta=s.nextLine();
		this.gameState = new GameState(palavraCorreta);
//		this.gameState = new GameState();
		this.state = STATE_WAITING_FOR_PLAYERS;
	}


	/**
	 * Metodo que indica se um novo usuário foi descoberto na rede
	 * @param identification    String com a identificação
	 * @param typeSys           String com o tipo do sistema
	 */
	public void newUserDiscovered(String identification, String typeSys) {
		Util.log("New User DIscoverd By Game Server", Configurations.OUT_INTERFACE);
		Util.log("id: " + identification + "  type: " + typeSys, Configurations.OUT_INTERFACE);
	}

	/**
	 * Metodo que indica se um usuário foi removido da rede
	 * @param identification    String com a identificação
	 * @param typeSys           String com o tipo do sistema
	 */
	public void userRemoved(String identification, String typeSys) {
		Util.log("User Removed From Game Server", Configurations.OUT_INTERFACE);
		Util.log("id: " + identification + "  type: " + typeSys, Configurations.OUT_INTERFACE);
		this.gameState.removeUserFromGame(identification);
		if(this.gameState.gameEnded()){
			this.state = STATE_GAME_ENDED;
		}
	}

	public void addRoleListener(RoleListener listener) {
		this.myListeners.add(listener);
	}
	public void addRoleListener(ArrayList<RoleListener> listener) {
		this.myListeners.addAll(listener);
	}
	public void sendChangeRoleMessage(){
		try {
			ChangeServerMessageData csm = new ChangeServerMessageData(SystemUsersList.chooseNextServer());
 			Message replyMsg;
			replyMsg = new Message(
					this.identification,
					MessageType.DEST_ALL,
					MessageType.MSG_CHANGE_SERVER,
					csm.toByteArray());
			replyMsg.encryptMessage(privateKey);
			MultiCastServer.getInstance().sendMessage(replyMsg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeRole() {
		 Role role = new Player(this.identification, this.privateKey);
		 //this.myListeners.clear();
		  for (RoleListener listener : this.myListeners) {
			 listener.roleChanger(role);
		  }
		  role.addRoleListener(this.myListeners);
		  role.startExecution();
		  (new Thread(role)).start();
		 
			
	}
}
