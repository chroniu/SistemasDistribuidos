package skelly;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;

import org.omg.PortableInterceptor.SUCCESSFUL;

import Messages.MessageType;

import util.Configurations;
import util.Util;

/**
 * Classe atua como um servidor Multicast. Possui funções de envio e recebimento
 * de mensagens.
 * Automaticamente repassa as mensagens chegadas à um "ouvinte", que será um PLayer ou GameServer
 * @author Lucas
 */

class MultiCastServer implements Runnable, RoleListener {
	private static MultiCastServer instance;
	
	final String address;
	final int port;
	final String identification;
	private ListenerMessage receiveCallBack;
	final MulticastSocket socket;
	final InetAddress multicastAddressGroup;
	final ServerSocket serverSocket;

	/**
	 * Contrutor
	 * @param address            String com o endereço do multicast
	 * @param port               int com o númer da porta 
	 * @param identification     String com a identificação
	 * @param receiverCallBack   ListenerMessage 
	 * @throws IOException
	 */
	public MultiCastServer(String address, int port, String identification, ListenerMessage receiverCallBack) throws IOException {
		 
			this.address = address;
			this.port = port;
			this.identification = identification;
			this.socket = new MulticastSocket(port);
			this.multicastAddressGroup = InetAddress.getByName(address);
			this.socket.joinGroup(multicastAddressGroup);
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind(new InetSocketAddress(InetAddress
					.getLocalHost(), 0));
			this.receiveCallBack = receiverCallBack;
			
			this.instance = this;
	}
	
	/**
	 * Método que retorna uma instância da classe
	 * @return MulticastServer
	 */
	public static MultiCastServer getInstance(){
		return instance;
	}

	/**
	 * Método que envia uma mensagem
	 * @param msg   Message mensagem
	 */
	public void sendMessage(Message msg) {
		try {
			Util.log("Sending Message from: " + msg.sender + " to: "
					+ msg.receiver+ " ttype: "+msg.type+" dataSize: "+msg.data.length, Configurations.OUT_LOG);
			byte data[] = msg.toByteArray();
			Util.log("Message Total Lenght:"+data.length, Configurations.OUT_LOG);
			DatagramPacket dataPack = new DatagramPacket(data, 0, data.length,
					multicastAddressGroup, port);
			socket.send(dataPack);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método que recebe e despacha pacotes
	 */
	public void run() {
		while (true) {
			byte[] buffer = new byte[2048];
			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(data);

				final byte [] datagramData = data.getData();
				String sender = new String(Util.range(datagramData, 0, 16)).trim();
				String receiver = new String(Util.range(datagramData, 16, 32)).trim();
				String type = new String(Util.range(datagramData, 32, 32 + 16)).trim();
				if(sender.equals(this.identification)){
					//mensagem enviada pelo servidor. não precisa abrir
				}else if (receiver.equals(identification)|| receiver.equals(MessageType.DEST_ALL)) {

					Message msg = new Message(sender, receiver, type, (Util.range(
							data.getData(), 16 * 3, data.getLength())));
					
					this.receiveCallBack.receivedMsg(msg);

				} else {
					Util.log("Message to: " + receiver + " from: " + sender
							+ " ignored", Configurations.OUT_LOG);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void roleChanger(Role role) {
		receiveCallBack = role;
	}
}