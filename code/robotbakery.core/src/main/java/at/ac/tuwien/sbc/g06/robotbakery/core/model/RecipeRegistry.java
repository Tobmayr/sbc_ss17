package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;

public class RecipeRegistry {

	private Map<String, Recipe> recipeMap;

	private RecipeRegistry() {
		recipeMap = new HashMap<>();
	}

	public boolean addRecipe(Recipe recipe) {
		if (recipe.getProductName() != null && !recipe.getProductName().isEmpty())
			if (!recipeMap.containsKey(recipe.getProductName())) {
				recipeMap.put(recipe.getProductName(), recipe);
				return true;
			}

		return false;
	}

	public boolean removeRecipe(Recipe recipe) {
		return recipeMap.remove(recipe) != null;
	}

	public Recipe getRecipeForProduct(Product product) {
		return recipeMap.get(product.getName());
	}

	public Recipe getRecipeWithHighestAmount(Ingredient ingredient) {
		Recipe target = null;
		for (Recipe recipe : recipeMap.values()) {
			if (target == null || recipe.getAmount(ingredient) > target.getAmount(ingredient)) {
				target = recipe;
			}

		}
		return target;
	}

	public Recipe getRecipeWithLowestAmount(Ingredient ingredient) {
		Recipe target = null;
		for (Recipe recipe : recipeMap.values()) {
			if (target == null || recipe.getAmount(ingredient) < target.getAmount(ingredient)) {
				target = recipe;
			}

		}
		return target;
	}

}
