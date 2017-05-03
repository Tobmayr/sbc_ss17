package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;

@SuppressWarnings("serial")
public class Ingredient implements Serializable {

	private final IngredientType type;
	private final UUID id;

	public Ingredient(IngredientType type) {
		super();
		id = UUID.randomUUID();

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
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Ingredient) {
			Ingredient that = (Ingredient) obj;
			return this.getId().toString() == that.getId().toString();
		}
		return super.equals(obj);
	}

	public UUID getId() {
		return id;
	}

}
