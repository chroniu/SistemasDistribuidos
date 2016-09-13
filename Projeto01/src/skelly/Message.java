package skelly;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.*;
import javax.swing.JOptionPane;

import util.Configurations;
import util.Util;

import java.awt.MultipleGradientPaint.CycleMethod;
import java.lang.System;;

/*
 * Classe que representa uma mensagem
 * Possui campos para indicar quem enviou, pra quem é, qual o tipo e os dados da mensagem
 * Possui métodos para criptografar e descriptografar mensagens.
 */
class Message {
	final String sender;
	final String receiver;
	final String type;
	byte[] data;
	final Cipher chiper;

	public Message(String sender, String receiver, String type, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException
			 {
		this.sender = sender;
		this.receiver = receiver;
		this.data = data;
		this.type = type;
		this.chiper = Cipher.getInstance(Configurations.CryptoAlgorithm);
		
		
	}
	
	private final int TAMANHO_MAX_BLOCO_TO_ENCRIPTAR = 1000;//117;
	private final int TAMANHO_BLOCO_ENCRIPTADO = 1000; //128
	
	/*
	 * Encripta a mensagem localizada em @data
	 */
	public void encryptMessage(PrivateKey key) {
		Util.log("Encriptando mensagem. DataSize: "+data.length, Configurations.OUT_LOG);
Util.log("Descriptando mensagem: data.size "+data.length, Configurations.OUT_LOG);
		if (data == null) {
			Util.log("MSG Data not definited", Configurations.OUT_LOG);
			return;
		}

		try {
			this.chiper.init(Cipher.ENCRYPT_MODE, key);
			if(this.data.length>TAMANHO_MAX_BLOCO_TO_ENCRIPTAR){
				byte [] dataCopy = new byte[TAMANHO_BLOCO_ENCRIPTADO*2];
				final byte [] firstPart = this.chiper.doFinal(Util.range(this.data,0,TAMANHO_MAX_BLOCO_TO_ENCRIPTAR));
				Util.log("Size first: "+firstPart.length+" original: "+TAMANHO_MAX_BLOCO_TO_ENCRIPTAR, Configurations.OUT_INTERFACE);
				final byte [] secondPart =  this.chiper.doFinal(Util.range(data,TAMANHO_MAX_BLOCO_TO_ENCRIPTAR,data.length));
				System.arraycopy(firstPart, 0, dataCopy, 0, firstPart.length);
				System.arraycopy(secondPart, 0, dataCopy, firstPart.length, secondPart.length);
				this.data = dataCopy;
			}else{
				this.data = this.chiper.doFinal(data);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Decripta a mensagem localizada em @data
	 */
	public void decryptMessage(PublicKey key) {
		Util.log("Descriptando mensagem: data.size "+data.length, Configurations.OUT_LOG);
	
		if (data == null) {
			Util.log("MSG Data not definited", Configurations.OUT_LOG);
			return;
		}

		try {
			this.chiper.init(Cipher.DECRYPT_MODE, key);
			if(this.data.length>TAMANHO_BLOCO_ENCRIPTADO){
				byte [] dataCopy = new byte[TAMANHO_MAX_BLOCO_TO_ENCRIPTAR*2];
				
				final byte [] firstPart = this.chiper.doFinal(Util.range(this.data,0,TAMANHO_BLOCO_ENCRIPTADO));
				Util.log("Size first: "+firstPart.length+" original: "+TAMANHO_BLOCO_ENCRIPTADO, Configurations.OUT_INTERFACE);
				final byte [] secondPart =  this.chiper.doFinal(Util.range(this.data,TAMANHO_BLOCO_ENCRIPTADO, this.data.length));
				System.arraycopy(firstPart, 0, this.data, 0, firstPart.length);
				System.arraycopy(secondPart, 0, this.data, firstPart.length, secondPart.length);
				this.data = dataCopy;
			}else{
				this.data = this.chiper.doFinal(data);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
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