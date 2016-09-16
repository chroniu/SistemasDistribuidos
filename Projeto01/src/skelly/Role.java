package skelly;

import java.util.ArrayList;

/**
 * Interface para um papel no sistema.
 * Classe Player e GameServer implementam essa interface
 * @author lucas
 */

public interface Role extends Runnable, ListenerMessage{ 
	 
	  /*
	  responsável por inicializar a máquina de estados do objeto
	  deve chamar o .execute para inicializar a Thread da Máquina de estados
	   */
	  public void startExecution(); 

	  /**
	   * Chamado sempre quando um novo usuário descoberto
	   * @param identification   String com a identificação
	   * @param typeSys          String com o tipo do sistema
	   */
	  public void newUserDiscovered(String identification, String typeSys);
	  
	  /**
	   * chamado sempre quando um novo usuário for removido
	   * @param identification   String com a identificação
	   * @param typeSys          String com o tipo do sistema
	   */
	  public void userRemoved(String identification, String typeSys);
	  
	  public void addRoleListener(RoleListener listener);
	  public void addRoleListener(ArrayList<RoleListener> listener);
	  
	  public void changeRole();

}
