package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

public class Message implements Serializable {
	public static final int ORDER_DECLINED = 1;
	private String message;
	private int type;

	public Message(String message, int type) {
		super();
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public int getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", type=" + type + "]";
	}
	
	

}
