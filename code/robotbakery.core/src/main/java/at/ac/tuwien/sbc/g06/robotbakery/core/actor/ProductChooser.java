package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Collectors;

import at.ac.tuwien.sbc.g06.robotbakery.core.RecipeRegistry;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * 
 * (visibility is set to protected for testing purposes)
 * 
 *
 */
public class ProductChooser {

	private List<Product> baseDoughCandidates;
	private SortedMap<String, Integer> counterStock;
	private SortedMap<IngredientType, Integer> ingredientStock;

	public ProductChooser(IKneadRobotService kneadRobotService) {
		baseDoughCandidates = kneadRobotService.getBaseDoughsFromStorage();
		counterStock = kneadRobotService.getCounterStock();
		ingredientStock = kneadRobotService.getIngredientStock();

	}

	public Product getFinishableBaseDough() {
		return baseDoughCandidates.stream().filter((p) -> enoughIngredientsToFinish(p.getRecipe(), ingredientStock))
				.findFirst().orElse(null);

	}

	public Product getNextProductForCounter() {
		if (counterStock.values().stream().findFirst().equals(SBCConstants.COUNTER_MAX_CAPACITY))
			return null;
		int median = (int) counterStock.values().toArray()[3];
		List<Recipe> recipes = getRecipesSortedByDiff(counterStock, median);
		Recipe candiate = recipes.stream().filter((r) -> enoughIngredientsToFinish(r, ingredientStock)).findFirst()
				.orElse(null);
		if (candiate != null)
			return new Product(candiate);
		return null;

	}

	public Product getNextProductForStorage() {
		Recipe candiate = ingredientStock.keySet().stream().map(i -> getSuitableRecipe(i, ingredientStock.get(i)))
				.filter(r -> r != null).findFirst().orElse(null);
		if (candiate != null)
			return new Product(candiate);
		return null;
	}

	private Recipe getSuitableRecipe(IngredientType type, Integer amount) {
		return RecipeRegistry.getInstance().getAllRecipes().stream().filter(r -> r.getAmount(type) <= amount)
				.sorted((i, j) -> j.getAmount(type).compareTo(i.getAmount(type)))
				.filter(r -> enoughIngredientsToFinish(r, ingredientStock)).findFirst().orElse(null);
	}

	public Product getNextBaseDoughForStorage() {
		Recipe candiate = ingredientStock.keySet().stream()
				.filter(i -> i == IngredientType.FLOUR || i == IngredientType.WATER)
				.map(i -> getSuitableRecipe(i, ingredientStock.get(i))).filter(r -> r != null).findFirst().orElse(null);
		if (candiate != null)
			return new Product(candiate);
		return null;
	}

	private boolean enoughIngredientsToFinish(Recipe recipe, SortedMap<IngredientType, Integer> stock) {
		return recipe.getAdditionalIngredients().keySet().stream().allMatch((i) -> stock.get(i) >= recipe.getAmount(i));
	}

	private List<Recipe> getRecipesSortedByDiff(Map<String, Integer> stock, int median) {
		return stock.keySet().stream()
				.sorted((i, j) -> diff(stock.get(i), median).compareTo(diff(stock.get(i), median)))
				.map(productName -> RecipeRegistry.getInstance().getRecipeByName(productName))
				.collect(Collectors.toList());

	}

	private Integer diff(int value, int median) {
		if (value <= median)
			return median - value;
		return value - median;
	}

}
