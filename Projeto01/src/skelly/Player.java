package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Scanner;

import Messages.GameMessageData;
import Messages.MessageType;
import Messages.SimpleMessageDataChecker;
import Messages.ThrowMessageData;
import util.Configurations;
import util.Util;
import java.lang.System;
import javax.crypto.NoSuchPaddingException;

/**
 * Implementa o papel de um Jogador
 * @author lucas
 */

public class Player implements Role{
	private final String identification;
	private final PrivateKey privateKey;
	
	private final int STATE_WAITING_GAME_START = 1;
	private final int STATE_WAITING_TURN = 2;
	private final int STATE_JOGADA = 3;
	private final int STATE_WAITING_REPLY_FROM_SERVER = 4;
	private final int STATE_GAME_ENDED = 5;
	private final int STATE_JOGADA_WORD = 6;
	private final int STATE_CHANGE_ROLE = 7;
	
	private final ArrayList<RoleListener> myListeners;
	private int state;
	private long sleepTime;
	
	/**
	 * Construtor
	 * @param identification    String com a identificação
	 * @param privateKey        PrivateKey chave privade
	 */
	public Player(String identification, PrivateKey privateKey){
		this.identification = identification;
		this.privateKey = privateKey;
		this.myListeners = new ArrayList<RoleListener>();
	}
	
	/**
	 * Método que indica o estado que o jogador vai estar
	 */
	public void run() {
		boolean continuar = true;
		while(continuar){
			try {
				switch (state) {
				case STATE_WAITING_GAME_START:
					this.sleepTime = 500;
					break;
				case STATE_WAITING_TURN:
					this.sleepTime = 500;
					break;
				case STATE_JOGADA:
					readJogada();
					this.sleepTime = 500;
					break;
				case STATE_JOGADA_WORD:
					readJogadaWord();
					this.sleepTime = 500;
					break;
				case STATE_WAITING_REPLY_FROM_SERVER:
					this.sleepTime = 500;
					break;
				case STATE_GAME_ENDED:
					Thread.sleep(10000);
					//this.state = STATE_WAITING_GAME_START;
					startExecution();
					this.sleepTime = 1;
					break;
				case STATE_CHANGE_ROLE:
					continuar = false;
					break;
				default:
					break;
				}
				Util.log("STATE: "+this.state, Configurations.OUT_LOG);
				Thread.sleep(sleepTime);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Método que recebe uma letra do jogador e envia como tentativa
	 * @throws NoSuchAlgorithmException
	 * @thorws NoSuchPaddingException
	 */
	private void readJogada() throws NoSuchAlgorithmException, NoSuchPaddingException {
		 Scanner scanner = new Scanner(System.in);
		 Util.log("Entre uma letra >> ", Configurations.OUT_INTERFACE);
		 String guess = scanner.nextLine();
		 
		 while (guess.length()>1){
			 Util.log("Entre com apenas uma letra >> ", Configurations.OUT_INTERFACE);
			 guess = scanner.nextLine();
		 }			 
		 if(this.state == STATE_JOGADA){
		 Message msg = new Message(this.identification, SystemUsersList.getServerIdentification() , MessageType.MSG_THROW, 
				 					new ThrowMessageData(guess).toByteArray());
		 msg.encryptMessage(privateKey);
		 MultiCastServer.getInstance().sendMessage(msg);
		 state = STATE_WAITING_REPLY_FROM_SERVER;
		 }
	}

	/**
	 * Método que recebe uma palavra do jogador e envia como tentativa
	 * @throws NoSuchAlgorithmException
	 * @thorws NoSuchPaddingException
	 */
	private void readJogadaWord() throws NoSuchAlgorithmException, NoSuchPaddingException {
		 Scanner scanner = new Scanner(System.in);
		 Util.log("Entre uma palavra>> ", Configurations.OUT_INTERFACE);
		 String guess = scanner.nextLine();

		 while (guess.length() == 1){
			 Util.log("Entre com uma palavra >> ", Configurations.OUT_INTERFACE);
			 guess = scanner.nextLine();
		 }	
		 if(this.state == STATE_JOGADA_WORD){
		 Message msg = new Message(this.identification, SystemUsersList.getServerIdentification() , MessageType.MSG_THROW, 
				 					new ThrowMessageData(guess).toByteArray());
		 msg.encryptMessage(privateKey);
		 MultiCastServer.getInstance().sendMessage(msg);
		 state = STATE_WAITING_REPLY_FROM_SERVER;
		 }
	}
	
	/**
	 * Metodo que indica o que o jogador tem que fazer com base na mensagem recebida
	 * @param msg     Message mensagem
	 */
	public void receivedMsg(Message msg) {
		if(!msg.type.equals(MessageType.MSG_KNOW)){
			 Util.log("Received Message from: "+msg.sender+" not secured checked yet", Configurations.OUT_LOG);
			 Util.log("Type Message from: "+msg.type, Configurations.OUT_LOG);
		}

		if(SystemUsersList.getUserPublicKey(msg.sender)==null && (!msg.type.equals(MessageType.MSG_KNOW) )){
			Util.log("User Not Yet Know. Ignoring Message", Configurations.OUT_LOG);
			return ;
		}
		
		if(msg.type.equals(MessageType.MSG_KNOW)){
			SystemUsersList.processIdentityMessage(msg);
		
		}else if (msg.type.equals(MessageType.MSG_GAME_START)){
			if(state != STATE_WAITING_GAME_START)  return;

			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);

			if(gmd.state.valid){
				this.state = STATE_WAITING_TURN;
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);			
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}

		}else if (msg.type.equals(MessageType.MSG_TURN)){
			if(state != STATE_WAITING_TURN)  return;
			
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
			
				this.state = STATE_JOGADA;
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}
			
		}else if (msg.type.equals(MessageType.MSG_THROW_REPLY_LETTER)){			
			
//			if(!(state ==  STATE_WAITING_REPLY_FROM_SERVER || state== STATE_JOGADA ))  return;
			if(!(state ==  STATE_WAITING_REPLY_FROM_SERVER))  return;
		
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
				this.state = STATE_JOGADA_WORD;
				Util.log(gmd.serverMessage+"\n", Configurations.OUT_INTERFACE);
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}
			
		}else if (msg.type.equals(MessageType.MSG_DO_WANT_PLAY)){
			if(state != STATE_WAITING_GAME_START) return;
			//TODO verificar sender
				
			Util.log("Checking Message Credentials", Configurations.OUT_LOG);
			Util.log("Cheking User Existence: "+SystemUsersList.getUserPublicKey(msg.sender), Configurations.OUT_LOG);
			

			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			
			if(SimpleMessageDataChecker.validMessage(msg.type, msg.data)){
				Util.log("Message is Valid", Configurations.OUT_LOG);
		  	 try {
					Message msg_reply = new Message(this.identification, msg.sender,
							 MessageType.MSG_I_WANT_TO_PLAY, 
							 SimpleMessageDataChecker.getDataForMessage(MessageType.MSG_I_WANT_TO_PLAY));
					msg_reply.encryptMessage(this.privateKey);
					MultiCastServer.getInstance().sendMessage(msg_reply);
				 } catch (Exception e){
					e.printStackTrace();
				}
			} 			
			else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_INTERFACE);
			}
			

		}else if(msg.type.equals(MessageType.MSG_THROW_REPLY_WORD)){

			if(!(state ==  STATE_WAITING_REPLY_FROM_SERVER || state== STATE_JOGADA_WORD || state== STATE_JOGADA ))  return;
			
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
				this.state = STATE_WAITING_TURN;
				Util.log(gmd.serverMessage+"\n", Configurations.OUT_INTERFACE);
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);
		
			
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}

			
		}else if (msg.type.equals(MessageType.MSG_GAME_ENDED)){
			if(this.state == STATE_WAITING_GAME_START) return;
			this.state  = STATE_GAME_ENDED;
			
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
				this.state = STATE_GAME_ENDED;
				Util.log(gmd.serverMessage+"\n", Configurations.OUT_INTERFACE);
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}

		}else{
			Util.log("Mensagem não reconhecida - TIPO>: "+msg.type, Configurations.OUT_INTERFACE);
		}
	}

	/**
	 * Metodo que inicia o jogador
	 */
	public void startExecution() { 
		state = STATE_WAITING_GAME_START;	
	}

	/**
	 * Metodo que indica se um novo usuário foi descoberto na rede
	 * @param identification    String com a identificação
	 * @param typeSys           String com o tipo do sistema
	 */
	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User Discoverd", Configurations.OUT_LOG);
		 Util.log("id: "+identification+"  type: "+typeSys, Configurations.OUT_LOG);
	}


	/**
	 * Metodo que indica se um usuário foi removido da rede
	 * @param identification    String com a identificação
	 * @param typeSys           String com o tipo do sistema
	 */
	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed", Configurations.OUT_LOG);
		 Util.log("id: "+identification+"  type: "+typeSys, Configurations.OUT_LOG);	
	}

	public void addRoleListener(RoleListener listener) {
		this.myListeners.add(listener);
	}
	public void addRoleListener(ArrayList<RoleListener> listener) {
		this.myListeners.addAll(listener);
	}

	public void changeRole() {
		 Role role = new GameServer(this.identification, this.privateKey);
		 this.myListeners.clear();
		  for (RoleListener listener : this.myListeners) {
			 listener.roleChanger(role);
		  }
		 (new Thread(role)).start();
		 role.startExecution();
	}
}
