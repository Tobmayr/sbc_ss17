package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * Flour Pack ingredient
 */
@SuppressWarnings("serial")
public class FlourPack extends Ingredient {

	private int currentAmount;

	public FlourPack() {
		super(IngredientType.FLOUR);
		this.currentAmount = SBCConstants.FLOUR_PACK_SIZE;
	}

	/**
	 * Takes the needed amount from the flour pack. If the flour pack hasn't
	 * enough flour left the rest amount is returned
	 * 
	 * @param amount
	 * @return
	 */
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
