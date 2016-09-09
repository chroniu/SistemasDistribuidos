package skelly;

public interface Role extends Runnable, ListenerMessage{ 
	 
	  /*
	  responsável por inicializar a máquina de estados do objeto
	  deve chamar o .execute para inicializar a Thread da Máquina de estados
	   */
	  public void startExecution();
	  

	  public void newUserDiscovered(String identification, String typeSys);
	  
	  public void userRemoved(String identification, String typeSys);

}
