package skelly;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;

import Messages.MessageType;

import util.Util;

/**
 * Classe atua como um servidor Multicast. Possui funções de envio e recebimento
 * de mensagens.
 * 
 * @addres -> Endereço do Multicast
 * @port -> Porta
 * @receiveCallBack -> Método chamado quando uma mensagem é recebida
 */
class MultiCastServer implements Runnable {
	private static MultiCastServer instance;
	
	final String address;
	final int port;
	final String identification;
	final ListenerMessage receiveCallBack;
	final MulticastSocket socket;
	final InetAddress multicastAddressGroup;
	final ServerSocket serverSocket;

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
	
	public static MultiCastServer getInstance(){
		return instance;
	}

	public void sendMessage(Message msg) {
		try {
			Util.log("Sending Message from: " + msg.sender + " to: "
					+ msg.receiver+ " ttype: "+msg.type+" dataSize: "+msg.data.length);
			byte data[] = msg.toByteArray();
			Util.log("Message Total Lenght:"+data.length);
			DatagramPacket dataPack = new DatagramPacket(data, 0, data.length,
					multicastAddressGroup, port);
			socket.send(dataPack);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// recebe pacotes e despacha
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
				//	Util.log("Recebendo Datagrama: "+datagramData.length);
				//	Util.log("Received Message From " + sender);

					Message msg = new Message(sender, receiver, type, (Util.range(
							data.getData(), 16 * 3, data.getLength())));
	//				Util.log("MSG data: "+msg.data.length);
					
					this.receiveCallBack.receivedMsg(msg);

				} else {
					Util.log("Message to: " + receiver + " from: " + sender
							+ " ignored");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}