package skelly;

/**
 * Interface para um papel no sistema.
 * Classe Player e GameServer implementam essa interface
 * @author lucas
 *
 */
public interface Role extends Runnable, ListenerMessage{ 
	 
	  /*
	  responsável por inicializar a máquina de estados do objeto
	  deve chamar o .execute para inicializar a Thread da Máquina de estados
	   */
	  public void startExecution();
	  

	  /**
	   * Chamado sempre quando um novo usuário descoberto
	   * @param identification
	   * @param typeSys
	   */
	  public void newUserDiscovered(String identification, String typeSys);
	  
	  /**
	   * chamado sempre quando um novo usuário for removido
	   * @param identification
	   * @param typeSys
	   */
	  public void userRemoved(String identification, String typeSys);

}
