package skelly;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.*;
import util.Configurations;
import util.Util;
import java.lang.System;;

class Message {
	final String sender;
	final String receiver;
	final String type;
	byte[] data;
	final Cipher chiper;

	public Message(String sender, String receiver, String type, byte[] data)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.sender = sender;
		this.receiver = receiver;
		this.data = data;
		this.chiper = Cipher.getInstance(Configurations.CryptoAlgorithm);
		this.type = type;
	}

	/*
	 * Encripta a mensagem localizada em @data
	 */
	public void encryptMessage(PublicKey key) {
		if (data == null) {
			Util.log("MSG Data not defined");
			return;
		}
		try {
			this.chiper.init(Cipher.ENCRYPT_MODE, key);
			data = this.chiper.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Decripta a mensagem localizada em @data
	 */
	public void decryptMessage(PrivateKey key) {
		if (data == null) {
			Util.log("MSG Data not definited");
			return;
		}
		try {
			this.chiper.init(Cipher.DECRYPT_MODE, key);
			data = this.chiper.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public byte[] toByteArray(){
		byte[] buf = new byte[16 * 3 + this.data.length];
		final byte[] senderx = sender.getBytes();
		final byte[] receiverx = receiver.getBytes();
		final byte[] typex = type.getBytes();
			
		for(int i=0; i<16; i++){
			buf[i] = ( i <senderx.length? senderx[i]:0);
			buf[i+16] = ( i <receiverx.length? receiverx[i]:0);
			buf[i+32] = ( i <typex.length? typex[i]:0);
			
		}
		System.arraycopy(this.data, 0, buf, 16 * 3, this.data.length);
		return buf;
  	}
}