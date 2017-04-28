package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public abstract class Message implements Serializable {

	private final UUID receiverID;
	private final MessageType messageType;

	public Message(UUID receiverID, MessageType messageType) {
		super();
		this.messageType = messageType;
		this.receiverID = receiverID;
	}

	public UUID getReceiverID() {
		return receiverID;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public enum MessageType {
		ORDER_DECLINED, ORDER_READY;
	}

}
