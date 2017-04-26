package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;

@SuppressWarnings("serial")
public class FlourPack extends Ingredient{

	private int currentAmount;

	private FlourPack(int currentAmount) {
		super(IngredientType.FLOUR);
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

	@Override
	public String toString() {
		return "FlourPack [currentAmount=" + currentAmount + "]";
	}
	
	
	
}
