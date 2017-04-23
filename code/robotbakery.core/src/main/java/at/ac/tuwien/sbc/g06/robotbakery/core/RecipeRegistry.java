package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;
import javassist.tools.reflect.CannotCreateException;

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
		recipe.addIngredient(Ingredient.FLOUR, 100);
		recipe.addIngredient(Ingredient.WATER, 100);
		recipe.addIngredient(Ingredient.BACKING_MIX_SPICY, 1);
		recipe.setPricePerUnit(0.35);
		recipeMap.put(recipe.getProductName(), recipe);

		// Bauernnrot Recipe
		recipe = new Recipe("Bauernbrot");
		recipe.addIngredient(Ingredient.FLOUR, 550);
		recipe.addIngredient(Ingredient.WATER, 500);
		recipe.addIngredient(Ingredient.BACKING_MIX_SPICY, 5);
		recipe.setPricePerUnit(1.8);
		recipeMap.put(recipe.getProductName(), recipe);

		// Marmorkuchen Recipe
		recipe = new Recipe("Marmorkuchen");
		recipe.addIngredient(Ingredient.FLOUR, 350);
		recipe.addIngredient(Ingredient.WATER, 550);
		recipe.addIngredient(Ingredient.EGGS, 5);
		recipe.addIngredient(Ingredient.BACKING_MIX_SWEET, 2);
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
