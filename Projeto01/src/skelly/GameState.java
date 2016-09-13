package skelly;

import java.util.ArrayList;

import Messages.GameStateDecEncoder;
import util.Configurations;
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
 * Classe que representa um estado de jogo.
 * Possui métodos para cuidar do estado do jogo
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
		this.letrasCorretas = Util.getLetterFromWord(this.palavaCorreta.toLowerCase());
		this.playerIdentifications = new ArrayList<GamePlayerState>();
		this.currentPlayerIdentification = null;
		
	}
	
	
	public void addUserToGame(String identification){
		if(userIsAdded(identification)) return;
		
		Util.log("GameState: USER "+identification+" ADDED", Configurations.OUT_LOG);
		
		GamePlayerState playerState = new GamePlayerState(identification);
		this.playerIdentifications.add(playerState);
	}
	
	private boolean userIsAdded(String ident){
		for (GamePlayerState gs : playerIdentifications) {
			if(gs.identification.equals(ident))
				return true;
		}
		
		return false;
	}
	public void removeUserFromGame(String identification){
		
		for (GamePlayerState state : playerIdentifications) {
			if(state.identification.equals(identification)){
				Util.log("GameState: USER "+identification+" REMOVED", Configurations.OUT_LOG);

				this.playerIdentifications.remove(state);
				return;
			}
		}
		Util.log("GameState: USER "+identification+" NOT REMOVED - NOT FOUND", Configurations.OUT_LOG);

	}
	
	public String getNextPlayer(){
		GamePlayerState playerState = this.playerIdentifications.get(0);
		this.playerIdentifications.remove(playerState);
		this.playerIdentifications.add(playerState);
	
		Util.log("GameState: NEXT PLAYER "+playerState.identification+" SELECTED", Configurations.OUT_LOG);
		this.currentPlayerIdentification = playerState;
		return playerState.identification;
	}
	
	public boolean gameEnded(){
		return (this.letrasCorretas.isEmpty() || this.playerIdentifications.isEmpty());
	}
	
	public String currentPlayerIdentification(){
		return (this.currentPlayerIdentification == null? null : this.currentPlayerIdentification.identification);
	}
	
	/*
	 * Atualiza o estado do jogo
	 * retorna true se a guess foi correta
	 */
	public boolean updateState(String guess, final String playerIdentity){
		guess = guess.toLowerCase();
		if(!this.currentPlayerIdentification.identification.equals(playerIdentity)){
			Util.log("GameState: "+playerIdentity+ " tryed to play, but it's "+ this.currentPlayerIdentification+" turn", Configurations.OUT_LOG);
			return false;
		}
		Util.log("Guess: "+guess, Configurations.OUT_INTERFACE);
		
		// guess de word
		if(guess.length()>1){
			Util.log("Guess Word: "+guess, Configurations.OUT_INTERFACE);
			if(this.palavaCorreta.toUpperCase().equals(guess.toUpperCase())){
				this.currentPlayerIdentification.acertos += this.letrasCorretas.size();
				this.letrasCorretas.clear();
				return true;

			}else{
				this.currentPlayerIdentification.erros += 1; 	
				this.letrasErradas.add(guess);
				
				verificarErrosJogadorAtual();
				return false;

			}
		}
		
		
		int acertos = 0;

		Util.log("Letras corretas: "+this.letrasCorretas, Configurations.OUT_INTERFACE);
		Util.log("LetrasCorretas Contains guess? "+this.letrasCorretas.contains(guess.toLowerCase()), Configurations.OUT_INTERFACE);
		while(this.letrasCorretas.contains(guess)){
			acertos++;
			this.letrasCorretas.remove(guess);
		}
		if(acertos > 0){
			this.currentPlayerIdentification.acertos += acertos;
			return true;
		}else{
			this.currentPlayerIdentification.erros  += 1;
			this.letrasErradas.add(guess.toLowerCase());
			verificarErrosJogadorAtual();
			return false;
		}
	}
	 
	
	private void verificarErrosJogadorAtual() {
		if(this.currentPlayerIdentification.erros >= Configurations.MAX_ERROS){
			removeUserFromGame(this.currentPlayerIdentification.identification);
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
			}else if(!this.letrasCorretas.contains((buf[i]+"").toLowerCase())){
				continue;
			}else{
				buf[i] = '_';
			}
		}
		
		return new String(buf);
	}
	
	public int getNumPlayers(){
		return this.playerIdentifications.size();
	}


	public GameStateDecEncoder getDecoder() {
		 String [] pontuacoes = new String[this.playerIdentifications.size()];
		 int i=0;
		for (GamePlayerState gamePlayerState : this.playerIdentifications) {
			pontuacoes[i++] = gamePlayerState.identification+": "+gamePlayerState.acertos;
		}
		
		GameStateDecEncoder decoder = new GameStateDecEncoder(this.currentPlayerIdentification.erros, wordState(), letrasErradas.toString(), pontuacoes);
		return decoder;
	}


	public boolean userIsInGame(String identification) {
		return userIsAdded(identification);
	}
	

}
