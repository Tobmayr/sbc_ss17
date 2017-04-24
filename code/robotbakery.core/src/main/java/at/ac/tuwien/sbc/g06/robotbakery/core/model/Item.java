package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

public class Item implements Serializable {
	private String productName;
	private int amount;
	private double cost;

	public Item(String productName, int amount, double cost) {
		super();
		this.productName = productName;
		this.amount = amount;
		this.cost = cost;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

}
