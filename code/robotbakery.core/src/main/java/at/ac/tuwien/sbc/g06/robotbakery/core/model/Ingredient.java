package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;

@SuppressWarnings("serial")
public class Ingredient implements Serializable {

	private IngredientType type;

	public Ingredient(IngredientType type) {
		super();
		this.type = type;
	}

	public IngredientType getType() {
		return type;
	}

	public void setType(IngredientType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Ingredient [type=" + type + "]";
	}

}
