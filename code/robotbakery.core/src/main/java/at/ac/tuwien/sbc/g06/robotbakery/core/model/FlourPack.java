package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

public class FlourPack implements Serializable{

	private int currentAmount;

	private FlourPack(int currentAmount) {
		this.currentAmount = currentAmount;
	}

	public int takeFlour(int amount) {
		if (currentAmount >= amount) {
			currentAmount = currentAmount - amount;
			return 0;
		} else {
			int restAmount = amount - currentAmount;
			currentAmount = 0;
			return restAmount;
		}
	}

	public int getCurrentAmount() {
		return currentAmount;
	}
}
