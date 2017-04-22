package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;

public class RecipeRegistry {

	public static RecipeRegistry INSTANCE = new RecipeRegistry();

	private Map<String, Recipe> recipeMap;

	private RecipeRegistry() {
		recipeMap = new HashMap<>();
	}

	public boolean registerRecipe(final Recipe recipe, boolean replaceExisting) {
		if (recipe.getProductName() != null && !recipe.getProductName().isEmpty())
			if (replaceExisting || !recipeMap.containsKey(recipe.getProductName())) {
				recipeMap.put(recipe.getProductName(), recipe);
				return true;
			}

		return false;
	}

	public boolean removeRecipe(Recipe recipe) {
		return recipeMap.remove(recipe.getProductName()) != null;
	}

	public Recipe getRecipeByName(String name) {
		return recipeMap.get(name);
	}

	public Recipe getRecipeForProduct(Product product) {
		return recipeMap.get(product.getName());
	}

	public Collection<Recipe> getAllRecipes(){
		return Collections.unmodifiableCollection(recipeMap.values());
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
