package Messages;

public class MessageType {
	public static final String DEST_ALL = "ALL";
	public static final String MSG_KNOW = "KNOW";
	public static final String MSG_SERVER = "SERVER";
	public static final String MSG_USER = "USER";
	
	//quando o usuário receber essa mensagem, indica que é a vez dele de mandar
	public static final String MSG_TURN = "TURN";
	
	// indica a jogada do jogador
	public static final String MSG_THROW = "THROW";
	
	// mensagem do servidor para o jogador, com um reply para a jogada do msm
	// se acertou errou 
	public static final String MSG_THROW_REPLY = "THROW_REPLY";
	
	
	//indica que o jogador desistiu/passou a vez
	public static final String MSG_THROW_FAIL = "THROW_FAIL";

	
 	public static final String MSG_GAME_ENDED = "GAME_ENDED";
	
	public static final String MSG_GAME_START = "GAME_START";
	
	
	
}
