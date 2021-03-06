package at.ac.tuwien.sbc.g06.robotbakery.core.util;

import static at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType.BAKING_MIX_SPICY;
import static at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType.BAKING_MIX_SWEET;
import static at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType.EGGS;
import static at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType.FLOUR;
import static at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType.WATER;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
		Recipe recipe = new Recipe(SBCConstants.PRODUCT1_NAME, Arrays.asList(FLOUR, WATER, BAKING_MIX_SPICY),
				Arrays.asList(100, 100, 1), 0.4d, 3);
		recipeMap.put(recipe.getProductName(), recipe);

		// Bauernnrot Recipe
		recipe = new Recipe(SBCConstants.PRODUCT2_NAME, Arrays.asList(FLOUR, WATER, BAKING_MIX_SPICY),
				Arrays.asList(550, 500, 5), 2.5d, 5);
		recipeMap.put(recipe.getProductName(), recipe);

		// Marmorkuchen Recipe
		recipe = new Recipe(SBCConstants.PRODUCT3_NAME, Arrays.asList(FLOUR, WATER, EGGS, BAKING_MIX_SWEET),
				Arrays.asList(350, 550, 5, 2), 2.0d, 8);
		recipeMap.put(recipe.getProductName(), recipe);

		// Fladenbrot Recipe
		recipe = new Recipe(SBCConstants.PRODUCT4_NAME, Arrays.asList(FLOUR, WATER, BAKING_MIX_SPICY),
				Arrays.asList(250, 300, 1), 1.0d, 2);
		recipeMap.put(recipe.getProductName(), recipe);

		// Croissant Recipe
		recipe = new Recipe(SBCConstants.PRODUCT5_NAME, Arrays.asList(FLOUR, WATER, EGGS, BAKING_MIX_SWEET),
				Arrays.asList(150, 230, 1, 2), 1.8d, 4);
		recipeMap.put(recipe.getProductName(), recipe);

	}

	public Recipe getRecipeByName(String name) {
		return recipeMap.get(name);
	}

	public Recipe getRecipeForProduct(Product product) {
		return recipeMap.get(product.getProductName());
	}

	public Collection<Recipe> getAllRecipes() {
		return Collections.unmodifiableCollection(recipeMap.values());
	}

}
