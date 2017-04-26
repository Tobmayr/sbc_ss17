package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;

@SuppressWarnings("serial")
public class Ingredient implements Serializable {

	private final IngredientType type;

	public Ingredient(IngredientType type) {
		super();
		this.type = type;

	}

	public IngredientType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Ingredient [type=" + type + "]";
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Ingredient) {
			Ingredient that = (Ingredient) obj;
			return this.getType()==that.getType();
		}
		return super.equals(obj);
	}

}
