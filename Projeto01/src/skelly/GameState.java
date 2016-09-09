package skelly;

import java.util.ArrayList;
import util.Util;

class GamePlayerState{
	final String identification;
	int erros;
	int acertos;
	int bonus;
	
	public GamePlayerState(String identification){
		this.identification = identification;
		this.erros = 0;
		this.acertos = 0;
		this.bonus = 0;
	}
}
/**
 * Classe que representa um estado de jogo
 *
 * @author lucas
 *
 */
public class GameState {
	
	private final ArrayList<GamePlayerState> playerIdentifications;
	
	private final String palavaCorreta;
	
	final ArrayList<String> letrasErradas;//armazena as letras incorretas informadas
	private final ArrayList<String> letrasCorretas;//armazena as letras corretas faltantes
	
	private GamePlayerState currentPlayerIdentification;
	
	public GameState(String palavaCorreta) {
		this.palavaCorreta = palavaCorreta;
		this.letrasErradas = new ArrayList<String>();
		this.letrasCorretas = Util.getLetterFromWord(this.palavaCorreta);
		this.playerIdentifications = new ArrayList<GamePlayerState>();
		this.currentPlayerIdentification = null;
	}
	
	
	public void addUserToGame(String identification){
		Util.log("GameState: USER "+identification+" ADDED");
		
		GamePlayerState playerState = new GamePlayerState(identification);
		this.playerIdentifications.add(playerState);
	}
	
	public void removeUserFromGame(String identification){
		
		for (GamePlayerState state : playerIdentifications) {
			if(state.identification.equals(identification)){
				Util.log("GameState: USER "+identification+" REMOVED");

				this.playerIdentifications.remove(state);
				return;
			}
		}
		Util.log("GameState: USER "+identification+" NOT REMOVED - NOT FOUND");

	}
	
	public String getNextPlayer(){
		GamePlayerState playerState = this.playerIdentifications.get(0);
		this.playerIdentifications.remove(playerState);
		this.playerIdentifications.add(playerState);
	
		Util.log("GameState: NEXT PLAYER "+playerState.identification+" SELECTED");
		this.currentPlayerIdentification = playerState;
		return playerState.identification;
	}
	
	public boolean gameEnded(){
		return ! this.letrasCorretas.isEmpty();
	}
	
	public String currentPlayerIdentification(){
		return (this.currentPlayerIdentification == null? null : this.currentPlayerIdentification.identification);
	}
	
	/*
	 * Atualiza o estado do jogo
	 * retorna true se a guess foi correta
	 */
	public boolean updateState(String guess, String playerIdentity){
		if(!currentPlayerIdentification.equals(playerIdentity)){
			Util.log("GameState: "+playerIdentity+ " tryed to play, but it's "+ this.currentPlayerIdentification+" turn");
			return false;
		}
		
		// guess de word
		if(guess.length()>1){
			if(this.palavaCorreta.equals(guess)){
				this.currentPlayerIdentification.acertos += this.letrasCorretas.size();
				this.letrasCorretas.remove(this.letrasCorretas);
				return true;

			}else{
				this.currentPlayerIdentification.erros += 1; 	
				this.letrasErradas.add(guess);
				return false;

			}
		}
		
		
		int acertos = 0;
		
		while(this.letrasCorretas.contains(guess)){
			acertos++;
			this.letrasCorretas.remove(guess);
		}
		if(acertos > 0){
			this.currentPlayerIdentification.acertos += acertos;
			return true;
		}else{
			this.currentPlayerIdentification.erros  += 1;
			this.letrasErradas.add(guess);
			return false;
		}
	}
	 
	
	/*
	 * retorna a palavra atualizada para enviar para os jogadores
	 */
	public String wordState(){
		char [] buf = this.palavaCorreta.toCharArray();
		
		for (int i = 0; i < buf.length; i++) {
			if(buf[i] == ' '){
				continue;
			}else if(this.letrasCorretas.contains(buf[i]+"")){
				continue;
			}else{
				buf[i] = '_';
			}
		}
		
		return new String(buf);
	}
	
	
	

}
