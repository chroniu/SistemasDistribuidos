package Messages;

import java.util.Arrays;

/**
 * Classe para o GameServer mandar o estado do jogo e mensagens para os Players
 * @author Lucas
 */

public class GameStateDecEncoder {
	public final int erros_jogador;
	public final String palavraAtual;
	public final String letrasErradas;
	public final String []pontuacao;
	public final boolean valid;
//	public String novoServer;
//	public final int vezesPerdidas;

	/**
	 * Construtor
	 * @param erros_jogador   int erros do jogador
	 * @param palavraAtual    String com a palavra atual
	 * @param letrasErradas   String com as letras erradas
	 * @param pontuacao       String[] com a pontuação de cada jogador
	 */
	public GameStateDecEncoder(int erros_jogador, String palavraAtual,
			String letrasErradas, String[] pontuacao) {
//		public GameStateDecEncoder(int erros_jogador, String palavraAtual,
//				String letrasErradas, String[] pontuacao, int vezesPerdidas) {
		super();
		this.erros_jogador = erros_jogador;
		this.palavraAtual = palavraAtual;
		this.letrasErradas = letrasErradas;
		this.pontuacao = pontuacao;
		this.valid = (this.palavraAtual!=null) && (this.pontuacao!=null);
//		this.novoServer = null; 
	}
	
	/**
	 * Construtor
	 * @param data      byte[] data
	 */
	public GameStateDecEncoder(byte[] data){
		String erros_jogador = null, palavraAtual = null, letrasErradas = null;
		String pontuacao=null;
		String novoServer = null;
		String [] strs = new String(data).split("@");
		for(int i=0;i<strs.length;i++){
			if(strs[i].startsWith("Erros")){
				erros_jogador = strs[++i];
			}else if(strs[i].startsWith("Letras Erradas" )){
				letrasErradas = strs[++i];	
			}else if(strs[i].startsWith("Pontuacao" )){
				pontuacao = strs[++i]; 
			}else if(strs[i].startsWith("Palavra_Atual" )){
				palavraAtual = strs[++i];
			}
//			}else if(strs[i].startsWith("NovoServer")){
//				novoServer = strs[++i];
//			}
		}
		if(erros_jogador==null){
			System.out.print("x");
		}
		this.erros_jogador = Integer.parseInt(erros_jogador);
		this.palavraAtual = palavraAtual;
		this.letrasErradas = letrasErradas;
		this.pontuacao = pontuacao.split("x");
//		this.novoServer = novoServer;
		this.valid = (this.palavraAtual!=null) && (this.pontuacao!=null);

	}
 	
	/**
	 * Método que coloca os atributos num vetor de bytes
	 * @return byte[] 
	 */
	public byte[] toByteArray(){
		String stb = "";
		stb = "Erros@"+this.erros_jogador+"@";
		stb += "Letras Erradas@"+(this.letrasErradas==null?"":this.letrasErradas)+"@";
		stb += "Pontuacao@";
		for(int i=0;i<pontuacao.length;i++){
			stb+=pontuacao[i]+"x";
		}
		stb +="@Palavra_Atual@"+palavraAtual;
//		stb +="NovoServer@"+(this.novoServer == null?"NULL":this.novoServer);
		return stb.getBytes();
	}

	@Override
	public String toString() {
		return "Seus Erros: " + erros_jogador
//				+ "Vezes Perdidas: " + vezesPerdidas
				+ "\nletrasErradas:" + letrasErradas + "\npontuacao:"
				+ Arrays.toString(pontuacao) + "\nPalavra Atual:" + palavraAtual
				+ "";
	}
	
}
