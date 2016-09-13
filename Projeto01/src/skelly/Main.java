package skelly;

import util.Util;

import java.io.IOException;
import java.lang.System;

/**
 * Classe main, somente le as configurações de entrada e repassa pra classe System
 * @author lucas
 *
 */
public class Main {

	public static void main(String[] args) {

		if (args.length > 3) {
			String identification = args[0];
			String ip = args[1];
			String port = args[2];
			String typeSys = args[3];

			Util.log("System Parameters");
			Util.log("identification: " + identification);
			Util.log("ip: " + ip);
			Util.log("port: " + port);
			Util.log("typeSys: " + typeSys);
			
			try {
				new skelly.System(identification, ip, Integer.parseInt(port), typeSys);
			} catch (NumberFormatException e) {
			 
				e.printStackTrace();
			} catch (IOException e) {
	 
				e.printStackTrace();
			}
			

		} else {
			System.out.println("Welcome to Guess the Word");
			System.out
					.println("Params. \nIdentification Ip_Multicast Port TYPE");
			System.out.println("TYPE : USER or SERVER");
		}
	}
}
