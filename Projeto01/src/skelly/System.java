package skelly;
import util.Util;

import java.io.IOException;
import java.security.KeyPair;

import Messages.MessageType;
/**
 * Principal que controla o sistema.
 * Inicializa 3 Threads
 * SustemUserList -> Responsável pelo reconhecimento e troca de chaves
 * Created by lucas on 07/09/16.
 */
class System{
 final String identification;
 final String ip;
 final int port;
 final String typeSys;
 static KeyPair keyPair;
 final Role role;
 final MultiCastServer server;
 
 public System(String identification, String ip, int port, String typeSys) throws IOException {
	Util.log("Initialising "+identification+  " Type: "+typeSys);
	
	this.identification = identification;
	this.ip = ip;
	this.port = port;
	this.typeSys = typeSys;
	keyPair = Util.generateKeys();

	 Util.log("Keys Generated");
	 Util.log("Public  Key:" +keyPair.getPublic());
	 Util.log("Private Key:" +keyPair.getPrivate());
	 
	 
	 //responsável pelo papel de GameServer ou Player
	 this.role =  (Role) (typeSys.equals(MessageType.MSG_USER) ? new Player(this.identification, this.keyPair.getPrivate())
	            : new GameServer(this.identification, this.keyPair.getPrivate())); 
	 
	 //cria o servidor multicast
	 this.server =  new MultiCastServer(ip, port, identification, role);

	 // inicializa o servidor MultiCast
	 (new Thread(this.server)).start();
	 Util.log("Multicast Server initialized");
	 
	 //(new Thread(this.server)).start();
 	//inicializa a Thread responsável por criar e manter a lista de usuarios do sistema
	 (new Thread(new SystemUsersList(this.identification, this.typeSys, this.keyPair.getPublic(), this.role))).start();

	 Util.log("UserList Initializing");

	 Util.log(typeSys+ " staring...");
	 role.startExecution();
	 (new Thread(this.role)).start();
 }
}
