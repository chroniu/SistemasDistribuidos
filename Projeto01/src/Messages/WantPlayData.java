package Messages;

public class WantPlayData {
	public final boolean valid;
	private final String MSG_WANT_PLAY = "I WANT TO PLAY";
	public WantPlayData(byte [] data){
		String str = new String(data);
		if(str.equals(MSG_WANT_PLAY)){
			this.valid = true;
		}else this.valid = false;
	}
	
	public WantPlayData(){
		this.valid = true;
	}
	
	public byte[] toByteArray() {
		if (!valid)
			return null;

		return this.MSG_WANT_PLAY.getBytes();
	}
}
