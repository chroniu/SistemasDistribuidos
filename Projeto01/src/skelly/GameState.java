package skelly;

import java.util.ArrayList;

import Messages.GameStateDecEncoder;
import util.Configurations;
import util.Util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;


/**
 * Classe auxiliar com os estados de cada jogador
 * @author lucas
 */
class GamePlayerState{
	final String identification;
	int erros;
	int acertos;
	int bonus;
	int vezesPerdidas;
	/**
	 * Construtor
	 * @param identification   String com a identificação
	 */
	public GamePlayerState(String identification){
		this.identification = identification;
		this.erros = 0;
		this.acertos = 0;
		this.bonus = 0;
		this.vezesPerdidas=0;
	}
}
/**
 * Classe que representa um estado de jogo.
 * Possui métodos para cuidar do estado do jogo
 * @author lucas
 */

public class GameState {
	
	private final ArrayList<GamePlayerState> playerIdentifications;	
	private final String palavaCorreta;	
	final ArrayList<String> letrasErradas;//armazena as letras incorretas informadas
	private final ArrayList<String> letrasCorretas;//armazena as letras corretas faltantes	
	private GamePlayerState currentPlayerIdentification;
	
	/**
	 * Construtor
	 */
	public GameState(String palavraCorreta) {
//		public GameState() {
//		this.palavaCorreta = wordGenerator();
		this.palavaCorreta=palavraCorreta;
		this.letrasErradas = new ArrayList<String>();
		this.letrasCorretas = Util.getLetterFromWord(this.palavaCorreta.toLowerCase());
		this.playerIdentifications = new ArrayList<GamePlayerState>();
		this.currentPlayerIdentification = null;
		
	}
	
	/**
	 * Método que lê um arquivo .txt e escolhe randomicamente uma palavra
	 */
	public String wordGenerator(){
		ArrayList<String> wordList = new ArrayList<String>();
		Random randomWord = new Random();
		BufferedReader in;
		String line;
		try {
		in = new BufferedReader(new FileReader(Configurations.URL));
		line = in.readLine();

		while (line != null) {
		            wordList.add(line);
		            line = in.readLine();
		        }
		in.close();

		} catch (IOException e) {
		   e.printStackTrace();
		  }

		int index = randomWord.nextInt(wordList.size());
		return wordList.get(index);
	}
	
	/**
	 * Método que adiciona um usuário no jogo
	 * @param identification   String com a identificação
	 */
	public void addUserToGame(String identification){
		if(userIsAdded(identification)) return;
		
		Util.log("GameState: USER "+identification+" ADDED", Configurations.OUT_LOG);
		
		GamePlayerState playerState = new GamePlayerState(identification);
		this.playerIdentifications.add(playerState);
	}
	
	/**
	 * Método que verifica se um usuário foi adicionad ao jogo
	 * @param ident   String com a identificação
	 */
	private boolean userIsAdded(String ident){
		for (GamePlayerState gs : playerIdentifications) {
			if(gs.identification.equals(ident))
				return true;
		}
		
		return false;
	}

	/**
	 * Método que remove um usuário no jogo
	 * @param identification   String com a identificação
	 */
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
	
	/**
	 * Método que retorna a identificação do proximo jogador
	 * @return String
	 */
	public String getNextPlayer(){
		GamePlayerState playerState = this.playerIdentifications.get(0);
		this.playerIdentifications.remove(playerState);
		this.playerIdentifications.add(playerState);
	
		Util.log("GameState: NEXT PLAYER "+playerState.identification+" SELECTED", Configurations.OUT_LOG);
		this.currentPlayerIdentification = playerState;
		return playerState.identification;
	}
	
	/**
	 * Método que verifica se o jogo acabou
	 * @return boolean
	 */
	public boolean gameEnded(){
		return (this.letrasCorretas.isEmpty() || this.playerIdentifications.isEmpty());
	}
	
	/**
	 * Método que retorna a identificação do jogador da vez
	 * @return String
	 */
	public String currentPlayerIdentification(){
		return (this.currentPlayerIdentification == null? null : this.currentPlayerIdentification.identification);
	}
	public void perdeuVez(){
		this.currentPlayerIdentification.vezesPerdidas++;
		verificarJogadorAtual();
	}
	
	/**
	 * Método que atualiza o estado do jogo
	 * @param guess             String com a tentativa
	 * @param playerIdentity    Stirng com identificação
	 * @return boolean
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
				
				verificarJogadorAtual();
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
			verificarJogadorAtual();
			return false;
		}
	}
	 
	/**
	 * Método que verifica os erros do jogador atual
	 * @return String
	*/
	private void verificarJogadorAtual() {
		if(this.currentPlayerIdentification.erros >= Configurations.MAX_ERROS){
			removeUserFromGame(this.currentPlayerIdentification.identification);
		}
		if(this.currentPlayerIdentification.vezesPerdidas >= Configurations.MAX_VEZES_PERDIDAS){
			removeUserFromGame(this.currentPlayerIdentification.identification);
		}
		
	}

	private void verificarVezesPerdidasJogadorAtual() {
		if(this.currentPlayerIdentification.vezesPerdidas >= Configurations.MAX_VEZES_PERDIDAS){
			removeUserFromGame(this.currentPlayerIdentification.identification);
		}
		
	}
	

	/**
	 * Método que retorna a palavra atualizada para enviar para os jogadores
	 * @return String
	 */
	public String wordState(){
		char [] buf = this.palavaCorreta.toCharArray();
		
		for (int i = 0; i < buf.length; i++) {
			if(buf[i] == ' '){
				continue;
			}else if(!this.letrasCorretas.contains((buf[i]+"").toLowerCase())){
				continue;
			}else{
				buf[i] = '*';
			}
		}
		
		return new String(buf);
	}
	
	/**
	 * Método que retorna a quantidade de jogadores
	 * @return int
	 */
	public int getNumPlayers(){
		return this.playerIdentifications.size();
	}

	/**
	 * Método que retorna os dados do GameState decodificado 
	 * @return GameStateDEcEnconder
	 */
	public GameStateDecEncoder getDecoder() {
		 String [] pontuacoes = new String[this.playerIdentifications.size()];
		 int i=0;
		for (GamePlayerState gamePlayerState : this.playerIdentifications) {
			pontuacoes[i++] = gamePlayerState.identification+": "+gamePlayerState.acertos;
		}
		
//		GameStateDecEncoder decoder = new GameStateDecEncoder(this.currentPlayerIdentification.erros, wordState(), letrasErradas.toString(), pontuacoes, this.currentPlayerIdentification.vezesPerdidas);
		GameStateDecEncoder decoder = new GameStateDecEncoder(this.currentPlayerIdentification.erros, wordState(), letrasErradas.toString(), pontuacoes);
		return decoder;
	}

	/**
	 * Método que rverifica se um usuário está no jogo
	 * @param identification   String com a identificação
	 * @return boolean
	 */
	public boolean userIsInGame(String identification) {
		return userIsAdded(identification);
	}

	/**
	 * Método que retorna a identificação do jogador ganhador
	 * @return String
	 */
	public String winnerPlayerIdentification(){
		int points = 0;
		String identification= "";
		for (GamePlayerState gs : this.playerIdentifications){
			if(gs.acertos>points){
				identification = gs.identification;
				points = gs.acertos;
			}
		}
		return identification;
	}
	

}
