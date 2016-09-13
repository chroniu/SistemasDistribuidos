package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
 *
 */
public class Player implements Role{
	private final String identification;
	private final PrivateKey privateKey;
	
	private final int STATE_WAITING_GAME_START = 1;
	private final int STATE_WAITING_TURN = 2;
	private final int STATE_JOGADA = 3;
	private final int STATE_WAITING_REPLY_FROM_SERVER = 4;
	private final int STATE_GAME_ENDED = 5;
	private int state;
	private long sleepTime;
	
	
	public Player(String identification, PrivateKey privateKey){
		this.identification = identification;
		this.privateKey = privateKey;
	}
	
	public void run() {
		while(true){
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
				case STATE_WAITING_REPLY_FROM_SERVER:
					
					this.sleepTime = 500;
					break;
				case STATE_GAME_ENDED:
					
					this.sleepTime = 5000;
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

	private void readJogada() throws NoSuchAlgorithmException, NoSuchPaddingException {
		 Scanner scanner = new Scanner(System.in);
		 Util.log("Your Guess>> ", Configurations.OUT_INTERFACE);
		 String guess = scanner.nextLine();
		 
		 Message msg = new Message(this.identification, SystemUsersList.getServerIdentification() , MessageType.MSG_THROW, 
				 					new ThrowMessageData(guess).toByteArray());
		 msg.encryptMessage(privateKey);
		 MultiCastServer.getInstance().sendMessage(msg);
		 state = STATE_WAITING_REPLY_FROM_SERVER;
		 
	}

	public void receivedMsg(Message msg) {
		if(!msg.type.equals(MessageType.MSG_KNOW)){
		 Util.log("Received Message from: "+msg.sender+" not secured checked yet", Configurations.OUT_LOG);
		 Util.log("Type Message from: "+msg.type, Configurations.OUT_LOG);
		}
		if(SystemUsersList.getUserPublicKey(msg.sender)==null &&(!msg.type.equals(MessageType.MSG_KNOW) )){
			Util.log("User Not Yet Know. Ignoring Message", Configurations.OUT_LOG);
			return ;
		}
		
		if(msg.type.equals(MessageType.MSG_KNOW)){
			SystemUsersList.processIdentityMessage(msg);
		}else if (msg.type.equals(MessageType.MSG_GAME_START)){
			if(state != STATE_WAITING_GAME_START)  return;
			//TODO verificar sender
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
			//TODO verificar sender
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
			
				this.state = STATE_JOGADA;
				Util.log(gmd.state.toString()+"\n", Configurations.OUT_INTERFACE);
		
			
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_LOG);
			}
			
		}else if (msg.type.equals(MessageType.MSG_THROW_REPLY)){
			if(state != STATE_WAITING_REPLY_FROM_SERVER)  return;
			//TODO verificar sender
			
			msg.decryptMessage(SystemUsersList.getUserPublicKey(msg.sender));
			GameMessageData gmd = new GameMessageData(msg.data);
			if(gmd.state.valid){
				this.state = STATE_WAITING_TURN;
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
			}else{
				Util.log("Message is not valid - Wrong keys?", Configurations.OUT_INTERFACE);
			}
			

		}else if (msg.type.equals(MessageType.MSG_GAME_ENDED)){
		
		}else{
			Util.log("Mensagem não reconhecida - TIPO>: "+msg.type, Configurations.OUT_INTERFACE);
		}
	}

	public void startExecution() { 
		state = STATE_WAITING_GAME_START;
		
	}

	public void newUserDiscovered(String identification, String typeSys) {
		 Util.log("New User Discoverd", Configurations.OUT_LOG);
		 Util.log("id: "+identification+"  type: "+typeSys, Configurations.OUT_LOG);
		
	}

	public void userRemoved(String identification, String typeSys) {
		 Util.log("User Removed", Configurations.OUT_LOG);
		 Util.log("id: "+identification+"  type: "+typeSys, Configurations.OUT_LOG);	}

}
