package Messages;


/**
 * Classe que armazena todos os tipos de mensagens
 * @author Lucas
 */

public class MessageType {

	// indica que a mensagem será enviada para todos que estiverem escutando
	public static final String DEST_ALL = "ALL";

	// mensagem de aviso de que começou e/ou continua escutando
	public static final String MSG_KNOW = "KNOW";

	// indica o tipo do utilizador, ou servidor ou usuário
	public static final String MSG_USER = "USER";
	public static final String MSG_SERVER = "SERVER";
	
	// Mensagem do servidor para  jogador indicando que é a vez dele jogar
	public static final String MSG_TURN = "TURN";
	
	// indica a jogada do jogador
	public static final String MSG_THROW = "THROW";
	
	// mensagem do servidor para o jogador com um reply se acertou a letra
	public static final String MSG_THROW_REPLY_LETTER = "THROW_REPLY_LETT";

	// mensagem do servidor para o jogador com um reply se acertou a palavra
	public static final String MSG_THROW_REPLY_WORD = "THROW_REPLY_WORD";

	//indica que o jogador desistiu/passou a vez
	public static final String MSG_THROW_FAIL = "THROW_FAIL";

	//indica que o jogo acabou
 	public static final String MSG_GAME_ENDED = "GAME_ENDED";
	
	//indica que o jogo começou
	public static final String MSG_GAME_START = "GAME_START";
	
	// mensagem do servidor para o jogador perguntando se quer jogar
	public static final String MSG_DO_WANT_PLAY = "MSG_DO_WANT_PLAY";
 
	// mensagem do jogador para o servidor avisando que quer jogar
	public static final String MSG_I_WANT_TO_PLAY ="MSG_I_WANT_TO_P";
	
}
