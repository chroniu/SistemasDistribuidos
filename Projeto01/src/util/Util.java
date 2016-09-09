package util;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
 


/**
  * Created by lucas on 07/09/16.
  */
public class Util{

  public static byte[] range (byte[] src, int init, int end){
    byte[] buff = new byte[end - init];
     
    for (int i = init; i < end; i++){
      buff[i-init] = src[i];
    }
    return buff;
  }

  public static byte[] range(byte[] src, int init) {
    return range(src, init, src.length);
  }


  public static void log(String msg){
	  System.out.println("SERVER: " + msg);
	  
  }

  public static  KeyPair generateKeys() {
	try{
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(Configurations.CryptoAlgorithm);
	    keygen.initialize(1024);
	    return keygen.generateKeyPair();
	}catch(Exception e){
		e.printStackTrace();
	}
	return null;
  }
}
