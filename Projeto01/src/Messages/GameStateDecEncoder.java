package Messages;

/**
 * Classe para o GameServer mandar o estado do jogo e mensagens para os Players
 * @author lucas
 *
 */
public class GameStateDecEncoder {
	public final int erros_jogador;
	public final String palavraAtual;
	public final String letrasErradas;
	public final String []pontuacao;
	public final boolean valid;
	
	
	public GameStateDecEncoder(int erros_jogador, String palavraAtual,
			String letrasErradas, String[] pontuacao) {
		super();
		this.erros_jogador = erros_jogador;
		this.palavraAtual = palavraAtual;
		this.letrasErradas = letrasErradas;
		this.pontuacao = pontuacao;
		this.valid = (this.palavraAtual!=null) && (this.pontuacao!=null);
	}
	
	public GameStateDecEncoder(byte[] data){
		String erros_jogador = null, palavraAtual = null, letrasErradas = null;
		
		String pontuacao=null;
		String [] strs = new String(data).split("|");
		for(int i=0;i<strs.length;i++){
			if(strs[i].startsWith("Erros:" )){
				erros_jogador = strs[++i];
			}else if(strs[i].startsWith("Letras Erradas" )){
				letrasErradas = strs[++i];	
			}else if(strs[i].startsWith("Pontuacao" )){
				pontuacao = strs[++i]; 
			}else if(strs[i].startsWith("Palavra_Atual" )){
				palavraAtual = strs[++i];
			}
		}
		
		this.erros_jogador = Integer.parseInt(erros_jogador);
		this.palavraAtual = palavraAtual;
		this.letrasErradas = letrasErradas;
		this.pontuacao = pontuacao.split("$");
	
		this.valid = (this.palavraAtual!=null) && (this.pontuacao!=null);

	}
 
	public byte[] toByteArray(){
		String stb = "";
		stb = "Erros|"+this.erros_jogador+"|";
		stb += "Letras Erradas|"+(this.letrasErradas==null?"":this.letrasErradas)+"|";
		stb += "Pontuacao|";
		for(int i=0;i<pontuacao.length;i++){
			stb+=pontuacao[i]+"$";
		}
		stb +="Palavra_Atual|"+palavraAtual;
		 
		return stb.getBytes();
	}
	
}
