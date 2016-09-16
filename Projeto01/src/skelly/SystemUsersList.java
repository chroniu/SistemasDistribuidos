package skelly;
 
import java.security.PublicKey;
import java.util.ArrayList;
import util.Configurations;
import util.Util;
import Messages.KnowMessageData;
import Messages.MessageType;
import java.lang.System;

/**
 * Classe auxiliar
 * @author Lucas
 */

class Node {
	final String identification;
	final String typeSys;
	final PublicKey publicKey;
	long timeFromLastMsg;

	public Node(String identification, String typeSys, PublicKey publicKey) {
		super();
		this.identification = identification;
		this.typeSys = typeSys;
		this.publicKey = publicKey;
		this.timeFromLastMsg = 0;
	}

}

/**
 * Responsável por gerenciar a lista de usuários do sistema Armazena chaves e
 * identificações e tempo desde a última resposta Created by lucas on 07/09/16.
 * Caso um usuário não tenha se comunicado em um limite de tempo, então tira-lo da lista
 * -> chamar alguma função para avisar o role 
 * @author Lucas
 */
public class SystemUsersList implements Runnable, RoleListener {

	private static ArrayList<Node> nodeList;
	private static Message myKnowMessage;
	private static Role role;

	/**
	 * Construtor
	 * @param identification  String com a identificação 
	 * @param typeSys         String com o tipo do sistema
	 * @param myPublicKey     PublicKey chave publica
	 * @param role            Role role
	 */
	public SystemUsersList(String identification, String typeSys, PublicKey myPublicKey, Role role) {
		this.nodeList = new ArrayList<Node>();
		KnowMessageData knm = new KnowMessageData(myPublicKey, typeSys);
		try {
			myKnowMessage = new  Message(identification, MessageType.DEST_ALL, 
					MessageType.MSG_KNOW, knm.toByteArray());
		}catch(Exception e){
			e.printStackTrace();
		}
		this.role = role;
	}

	/**
	 * Método que atualiza a lista de usuários
	 * @param msg     Message mensagem
	 */ 
	public static void processIdentityMessage(Message msg) {
		Util.log("Processing KNow Mesage from: "+msg.sender , Configurations.OUT_LOG);
		if (!msg.type.equals(KnowMessageData.Identity)) {
			return;
		}
		Node node = getUserNode(msg.sender);
		Util.log("Node: "+node , Configurations.OUT_LOG);

		if(node == null){
			KnowMessageData knowMessage = new KnowMessageData(msg.data);
			if(knowMessage.valid){
				node = new Node(msg.sender, knowMessage.typeSys, knowMessage.publicKey);
				node.timeFromLastMsg = System.currentTimeMillis();
				nodeList.add(node);
				role.newUserDiscovered(node.identification, node.typeSys);
			}else{
				Util.log("Mensagem "+KnowMessageData.Identity+" Inválida", Configurations.OUT_LOG);
			}
		}else{
			node.timeFromLastMsg = System.currentTimeMillis();
		}
	}
	
	/**
	 * Método que retorna o nó de um usuários específico
	 * @param identification    String com a identificação
	 * @return Node
	 */ 
	private static Node getUserNode(String identification){
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).identification.equals(identification)) {
				return nodeList.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Método que retorna a chave pública de um usuários específico
	 * @param identification    String com a identificação
	 * @return PublicKey
	 */ 
	public static PublicKey getUserPublicKey(String identification){
		final Node node = getUserNode(identification);
		return (node == null? null: node.publicKey);
	}

	/**
	 * Método que retorna a identificação do servidor
	 * @return String
	 */ 
	public static String getServerIdentification(){
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).typeSys.equals(MessageType.MSG_SERVER)) {
				return nodeList.get(i).identification;
			}
		}
		return null;
	}
	
	/**
	 * Método que retorna a lista com todos os usuários
	 * @return ArrayList<String>
	 */ 
	public static ArrayList<String> getTypeUserList(){
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).typeSys.equals(MessageType.MSG_USER)) {
				list.add(nodeList.get(i).identification); 
			}
		}	
		return list;
	}
	
	/**
	 * Método que envia de tempos em tempos uma mensagem de identificação pra rede
	 */ 
	public void run() {
		while(true){
			MultiCastServer.getInstance().sendMessage(myKnowMessage);
			//TODO Verificar a cada tempo T, se um usuário parou de existir
			try {
				Thread.sleep(Configurations.KNOW_MSG_SLEEPER);
				checkUserTimeOut();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * Método que verifica se o usuário passou do tempo limite do envio da verificação de que continua na rede
	 */
	private void checkUserTimeOut() {
		ArrayList<Node> toRemove = new ArrayList<Node>(); 
		long timeNow = System.currentTimeMillis();
		for (Node node: nodeList) {
			if( (timeNow - node.timeFromLastMsg)>Configurations.MAX_TIME_TO_REMOVE_FROM ){
				toRemove.add(node);
				role.userRemoved(node.identification, node.typeSys);
			}
		}
		nodeList.remove(toRemove);
		
	}

	public void roleChanger(Role newRole) {
		 role = newRole;
	}
 

}
