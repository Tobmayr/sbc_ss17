package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

public class Ingredient implements Serializable {
	public static final String FLOUR = "Mehl";
	public static final String WATER = "Wasser";
	public static final String EGGS = "Eier";
	public static final String BACKING_MIX_SPICY = "Fertibackmischung \"pikant\"";
	public static final String BAKING_MIX_SWEET = "Fertibackmischung \"süß\"";


	private String type;
	private int amount;

	public Ingredient(String type, int amount) {
		super();
		this.type = type;
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Ingredient [type=" + type + ", amount=" + amount + "]";
	}

}
