package Messages;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import util.Configurations;
import util.Util;

/**
 * Estrutra do KnowMessage 1 [Byte] +
 * 
 * @author lucas
 * 
 */

public class KnowMessageData {

	public PublicKey publicKey;
	public final boolean valid;
	public final String typeSys;
	public static final String Identity = MessageType.MSG_KNOW;

	public KnowMessageData(byte[] data) {
		PublicKey key;
		boolean valid;

		byte type = data[0];
		byte[] publicKeyData = Util.range(data, 1);

		try {

			KeyFactory keyFactory = KeyFactory
					.getInstance(Configurations.CryptoAlgorithm);

			EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
			key = keyFactory.generatePublic(publicKeySpec);
			valid = true;
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
			key = null;
		}
		this.publicKey = key;
		this.valid = valid;
		this.typeSys = (type == 0 ? MessageType.MSG_SERVER
				: MessageType.MSG_USER);
	}

	public KnowMessageData(PublicKey publicKey, String typeSys) {
		this.publicKey = publicKey;
		this.typeSys = typeSys;
		this.valid = true;
	}

	public byte[] toByteArray() {
		if (!valid)
			return null;

		byte[] publicKeyBytes = publicKey.getEncoded();
		byte[] buff = new byte[publicKeyBytes.length + 1];

		System.arraycopy(publicKeyBytes, 0, buff, 1, publicKeyBytes.length);
		buff[0] = (byte) (this.typeSys.equals(MessageType.MSG_SERVER) ? 0 : 1);
		return buff;
	}
}
