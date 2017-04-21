package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.Map;

public class Recipe {

	public enum Ingredient {
		FLOUR, WATER, EGGS, BACKING_MIX_SPICY, BACKING_MIX_SWEET;
	}

	private Map<Ingredient, Integer> ingredientAmountMap;

	private String productName;

	public Recipe(String productName) {
		super();
		this.productName = productName;

	}

	public String getProductName() {
		return productName;
	}

	public void addIngredient(Ingredient ingredient, int amount) {
		ingredientAmountMap.put(ingredient, amount);
	}

	public int getAmount(Ingredient ingredient) {
		return ingredientAmountMap.get(ingredient);
	}
}
