package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
	public enum MessageType{ORDER_DECLINED};
	private final UUID receiverID;
	private final MessageType type;

	public Message(MessageType type, UUID receiverID) {
		super();
		this.type=type;
		this.receiverID=receiverID;

	
	}

	public UUID getReceiverID() {
		return receiverID;
	}

	public MessageType getType() {
		return type;
	}
	
	

	
}
