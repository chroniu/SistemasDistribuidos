package Messages;

public class MessageType {
	public static final String DEST_ALL = "ALL";
	public static final String MSG_KNOW = "KNOW";
	public static final String MSG_SERVER = "SERVER";
	public static final String MSG_USER = "USER";
	
	//quando o usuário receber essa mensagem, indica que é a vez dele de mandar
	public static final String MSG_TURN = "TURN";
	
	public static final String MSG_I_WANT_TO_PLAY_CHK_STRING = "WANT_TO_PLAY";
	
	
	// indica a jogada do jogador
	public static final String MSG_THROW = "THROW";
	
	// mensagem do servidor para o jogador, com um reply para a jogada do msm
	// se acertou errou 
	public static final String MSG_THROW_REPLY = "THROW_REPLY";
	
	
	//indica que o jogador desistiu/passou a vez
	public static final String MSG_THROW_FAIL = "THROW_FAIL";

	
 	public static final String MSG_GAME_ENDED = "GAME_ENDED";
	
	public static final String MSG_GAME_START = "GAME_START";
	
	public static final String MSG_DO_WANT_PLAY = "MSG_DO_WANT_PLAY";
 
	public static final String MSG_I_WANT_TO_PLAY =" MSG_I_WANT_TO_PLAY";
	
	
}
