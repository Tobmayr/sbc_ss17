package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class RecipeRegistry {

	private static RecipeRegistry INSTANCE = null;

	private Map<String, Recipe> recipeMap;

	private RecipeRegistry() {
		recipeMap = new HashMap<>();
		createRecipes();
	}

	public static RecipeRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RecipeRegistry();
		}
		return INSTANCE;
	}

	private void createRecipes() {
		// Kaisersemmel Recipe
		Recipe recipe = new Recipe("Kaisersemmel");
		recipe.addIngredients(new Ingredient(Ingredient.FLOUR, 100), new Ingredient(Ingredient.WATER, 100),
				new Ingredient(Ingredient.BACKING_MIX_SPICY, 1));
		recipe.setPricePerUnit(0.35);
		recipeMap.put(recipe.getProductName(), recipe);

		// Bauernnrot Recipe
		recipe = new Recipe("Bauernbrot");
		recipe.addIngredients(new Ingredient(Ingredient.FLOUR, 550), new Ingredient(Ingredient.WATER, 500),
				new Ingredient(Ingredient.BACKING_MIX_SPICY, 5));
		recipe.setPricePerUnit(1.8);
		recipeMap.put(recipe.getProductName(), recipe);

		// Marmorkuchen Recipe
		recipe = new Recipe("Marmorkuchen");
		recipe.addIngredients(new Ingredient(Ingredient.FLOUR, 350), new Ingredient(Ingredient.WATER, 550),
				new Ingredient(Ingredient.EGGS, 5), new Ingredient(Ingredient.BAKING_MIX_SWEET, 2));
		recipe.setPricePerUnit(1.5);
		recipeMap.put(recipe.getProductName(), recipe);
		System.out.println();
	}

	public Recipe getRecipeByName(String name) {
		return recipeMap.get(name);
	}

	public Recipe getRecipeForProduct(Product product) {
		return recipeMap.get(product.getName());
	}

	public Collection<Recipe> getAllRecipes() {
		return Collections.unmodifiableCollection(recipeMap.values());
	}

	public Recipe getRecipeWithHighestAmount(String ingredientName) {
		Recipe target = null;
		for (Recipe recipe : recipeMap.values()) {
			if (target == null || recipe.getAmount(ingredientName) > target.getAmount(ingredientName)) {
				target = recipe;
			}

		}
		return target;
	}

	public Recipe getRecipeWithLowestAmount(String ingredientName) {
		Recipe target = null;
		for (Recipe recipe : recipeMap.values()) {
			if (target == null || recipe.getAmount(ingredientName) < target.getAmount(ingredientName)) {
				target = recipe;
			}

		}
		return target;
	}

}
