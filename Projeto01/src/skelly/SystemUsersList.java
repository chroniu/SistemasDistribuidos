package skelly;
 
import java.security.PublicKey;
import java.util.ArrayList;
import util.Configurations;
import util.Util;
import Messages.KnowMessageData;
import Messages.MessageType;
import java.lang.System;

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
 * 
 * TODO -> COlocar um "tempo" 
 * Caso um usuário não tenha se comunicado em um limite de tempo, então tira-lo da lista
 * -> chamar alguma funlão para avisar o role
 */
public class SystemUsersList implements Runnable {

	private static ArrayList<Node> nodeList;
	private static Message myKnowMessage;
	private static Role role;

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

	// atualiza a lista de users
	public static void processIdentityMessage(Message msg) {
		Util.log("Processing KNow Mesage from: "+msg.sender );
		if (!msg.type.equals(KnowMessageData.Identity)) {
			return;
		}
		Node node = getUserNode(msg.sender);
		Util.log("Node: "+node );

		if(node == null){
			KnowMessageData knowMessage = new KnowMessageData(msg.data);
			if(knowMessage.valid){
				node = new Node(msg.sender, knowMessage.typeSys, knowMessage.publicKey);
				node.timeFromLastMsg = System.currentTimeMillis();
				nodeList.add(node);
				role.newUserDiscovered(node.identification, node.typeSys);
			}else{
				Util.log("Mensagem "+KnowMessageData.Identity+" Inválida");
			}
		}else{
			node.timeFromLastMsg = System.currentTimeMillis();
		}
	}
	
	private static Node getUserNode(String identification){
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).identification.equals(identification)) {
				return nodeList.get(i);
			}
		}
		return null;
	}
	
	public static PublicKey getUserPublicKey(String identification){
		final Node node = getUserNode(identification);
		return (node == null? null: node.publicKey);
	}

	public static String getServerIdentification(){
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).typeSys.equals(MessageType.MSG_SERVER)) {
				return nodeList.get(i).identification;
			}
		}
		return null;
	}
	
	public static ArrayList<String> getTypeUserList(){
		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < nodeList.size(); i++) {
			if (nodeList.get(i).typeSys.equals(MessageType.MSG_USER)) {
				list.add(nodeList.get(i).identification); 
			}
		}	
		return list;
	}
	
	// envia de tempos em tempos uma mensagem de identificação pra rede
	public void run() {
		while(true){
			MultiCastServer.getInstance().sendMessage(myKnowMessage);
			//TODO Verificar a cada tempo T, se um usuário parou de existir
			try {
				Thread.sleep(Configurations.KNOW_MSG_SLEEPER);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
