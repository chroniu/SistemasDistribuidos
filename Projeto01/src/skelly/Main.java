package skelly;

import util.Configurations;
import util.Util;

import java.io.IOException;
import java.lang.System;

/**
 * Classe main, somente le as configurações de entrada e repassa pra classe System
 * @author lucas
 */

public class Main {

	public static void main(String[] args) {

		if (args.length > 3) {
			String identification = args[0];
			String ip = args[1];
			String port = args[2];
			String typeSys = args[3];

			Util.log("System Parameters", Configurations.OUT_LOG);
			Util.log("identification: " + identification, Configurations.OUT_LOG);
			Util.log("ip: " + ip, Configurations.OUT_LOG);
			Util.log("port: " + port, Configurations.OUT_LOG);
			Util.log("typeSys: " + typeSys, Configurations.OUT_LOG);
			
			try {
				new skelly.System(identification, ip, Integer.parseInt(port), typeSys);
			} catch (NumberFormatException e) {
			 
				e.printStackTrace();
			} catch (IOException e) {
	 
				e.printStackTrace();
			}
			

		} else {
			System.out.println("Welcome to Guess the Word");
			System.out.println("Params. \nIdentification Ip_Multicast Port TYPE");
			System.out.println("TYPE : USER or SERVER");
		}
	}
}
