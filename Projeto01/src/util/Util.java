package util;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
 


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


  public static void log(String msg, int type){
	  
	  //imprime tudo
	  if(Configurations.OUTPUT>=3){

		  System.out.println("SERVER: " + msg);
		    
	  }else{
		  if(type == Configurations.OUTPUT){
			  System.out.println("SERVER: " + msg);
		  }
	  }
  }

  public static  KeyPair generateKeys() {
	try{
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
	    keygen.initialize(2048);
	    return keygen.generateKeyPair();
	}catch(Exception e){
		e.printStackTrace();
	}
	return null;
  }
  
  public static ArrayList<String> getLetterFromWord(String word){
	  final ArrayList<String> letList = new  ArrayList<String>();
	  char [] array = word.toCharArray();
	  
	  for(int i = 0; i < array.length; i++){
		  if(array[i] != ' ')
			  letList.add(array[i]+"");
	  }
	  
	  return letList;
  }
 
}
