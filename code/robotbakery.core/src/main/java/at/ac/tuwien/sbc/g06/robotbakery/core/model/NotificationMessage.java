package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

/**
 * NotificationMessages for robots
 */
@SuppressWarnings("serial")
public class NotificationMessage implements Serializable {
	public static final int NO_MORE_PRODUCTS_IN_STORAGE = 100;
	public static final int NO_MORE_PRODUCTS_IN_COUNTER = 110;
	public static final int NO_MORE_PRODUCTS_IN_BAKEROOM = 120;
	public static final int ORDER_PROCESSING_LOCKED = 130;
	public static final int ORDER_PROCESSING_FREE = 140;
	public static final int NO_MORE_ORDERS = 150;
	public static final int PREPACKAGE_LIMIT_REACHED = 160;
	public static final int NO_MORE_INGREDIENTS_IN_STORAGE = 170;
	public static final int COUNTER_STOCK_FULL = 180;
	public static final int NO_MORE_DELIVERY_ORDERS = 190;
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
