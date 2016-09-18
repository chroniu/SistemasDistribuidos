package skelly;
import util.Configurations;
import util.Util;

import java.io.IOException;
import java.security.KeyPair;

import Messages.MessageType;
/**
 * Classe principal que controla o sistema.
 * Inicializa 3 Threads
 * SustemUserList -> Responsável pelo reconhecimento e troca de chaves
 * @author lucas 
 */

class System{
 final String identification;
 final String ip;
 final int port;
 final String typeSys;
 static KeyPair keyPair;
 final Role role;
 final MultiCastServer server;
 

 /**
  * Construtor
  * @param identification    String com a identificação
  * @param ip                String com o ip
  * @param port              int com o numéro da porta que será ouvida
  * @param typesys           String com o tipo de sistema
  * @throws IOException
  */
 public System(String identification, String ip, int port, String typeSys) throws IOException {
	Util.log("Initialising "+identification+  " Type: "+typeSys, Configurations.OUT_LOG);
	
	this.identification = identification;
	this.ip = ip;
	this.port = port;
	this.typeSys = typeSys;
	keyPair = Util.generateKeys();

	 Util.log("Keys Generated", Configurations.OUT_LOG);
	 Util.log("Public  Key:" +keyPair.getPublic(), Configurations.OUT_LOG);
	 Util.log("Private Key:" +keyPair.getPrivate(), Configurations.OUT_LOG);
	 
	 
	 //responsável pelo papel de GameServer ou Player
	 this.role =  (Role) (typeSys.equals(MessageType.MSG_USER) ? new Player(this.identification, this.keyPair.getPrivate())
	            : new GameServer(this.identification, this.keyPair.getPrivate())); 
	 
	 //cria o servidor multicast
	 this.server =  new MultiCastServer(ip, port, identification, role);

	 // inicializa o servidor MultiCast
	 (new Thread(this.server)).start();
	 Util.log("Multicast Server initialized", Configurations.OUT_LOG);
	 
	 //(new Thread(this.server)).start();
 	//inicializa a Thread responsável por criar e manter a lista de usuarios do sistema
	 SystemUsersList sul = new SystemUsersList(this.identification, this.typeSys, this.keyPair.getPublic(), this.role);
	 (new Thread(sul)).start();

	 Util.log("UserList Initializing", Configurations.OUT_LOG);

	 Util.log(typeSys+ " staring...", Configurations.OUT_LOG);
	 role.addRoleListener(this.server);
	 role.addRoleListener(sul); 
	 role.startExecution();
	 (new Thread(this.role)).start();
 }
}
