package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class NotificationMessage implements Serializable {
	public static final int NO_MORE_PRODUCTS_IN_STORAGE = 100;
	public static final int NO_MORE_PRODUCTS_IN_COUNTER = 110;
	public static final int NO_MORE_PRODUCTS_IN_BAKEROOM = 120;
	private int messageTyp;

	public NotificationMessage(int messageTyp) {
		super();
		this.messageTyp = messageTyp;
	}

	public int getMessageTyp() {
		return messageTyp;
	}

	public void setMessageTyp(int messageTyp) {
		this.messageTyp = messageTyp;
	}

}
