package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class ProductChooser {

	private List<Product> baseDoughCandidates;
	private Map<String, Integer> counterStock;
	private Map<IngredientType, Integer> ingredientStock;

	public ProductChooser(IKneadRobotService kneadRobotService) {
		baseDoughCandidates = kneadRobotService.getBaseDoughsFromStorage();
		counterStock = CollectionsUtil.sortMapByValues(kneadRobotService.getCounterStock(), true);
		ingredientStock = CollectionsUtil.sortMapByValues(kneadRobotService.getIngredientStock(), false);

	}

	public Product getFinishableBaseDough() {
		return baseDoughCandidates.stream().filter((p) -> enoughAddtionaIngredientsToFinish(p.getRecipe())).findFirst()
				.orElse(null);

	}

	public Product getNextProductForCounter() {
		if (counterStock.values().stream().findFirst().get().equals(SBCConstants.COUNTER_MAX_CAPACITY))
			return null;
		int median = (int) counterStock.values().toArray()[2];
		List<Recipe> recipes = getRecipesSortedByDiff(counterStock, median);
		Recipe candiate = recipes.stream().filter((r) -> enoughIngredientsToFinish(r)).findFirst().orElse(null);
		if (candiate != null)
			return new Product(candiate);
		return null;

	}

	public Product getNextProductForStorage() {
		Recipe candiate = ingredientStock.keySet().stream().filter(type->type!=IngredientType.FLOUR)
				.map(i -> getSuitableRecipe(i, ingredientStock.get(i), false)).filter(r -> r != null).findFirst()
				.orElse(null);
		if (candiate != null)
			return new Product(candiate);
		return null;
	}

	private Recipe getSuitableRecipe(IngredientType type, Integer amount, boolean onlyBaseIngredients) {
		Predicate<Recipe> pred = onlyBaseIngredients ? r -> enoughIngredientsToFinishBaseDough(r)
				: r -> enoughIngredientsToFinish(r);

		return RecipeRegistry.getInstance().getAllRecipes()
				.stream()
				.filter(r -> r.getAmount(type) <= amount)
				.sorted((i, j) -> j.getAmount(type).compareTo(i.getAmount(type))).filter(pred).findFirst().orElse(null);
	}

	public Product getNextBaseDoughForStorage() {
		Recipe candiate=getSuitableRecipe(IngredientType.FLOUR, ingredientStock.get(IngredientType.FLOUR), true);
		if (candiate != null)
			return new Product(candiate);
		return null;
	}

	private boolean enoughIngredientsToFinish(Recipe r) {
		if (enoughIngredientsToFinishBaseDough(r)) {
			return enoughAddtionaIngredientsToFinish(r);
		}
		return false;
	}

	private boolean enoughIngredientsToFinishBaseDough(Recipe recipe) {
		return recipe.getAmount(IngredientType.FLOUR) <= ingredientStock.get(IngredientType.FLOUR);
	}

	private boolean enoughAddtionaIngredientsToFinish(Recipe recipe) {
		return recipe.getIngredients().keySet().stream().filter(type->type!=IngredientType.FLOUR && type!=IngredientType.WATER)
				.allMatch((i) -> ingredientStock.get(i) >= recipe.getAmount(i));
	}

	private List<Recipe> getRecipesSortedByDiff(Map<String, Integer> stock, int median) {
		return stock.keySet().stream()
				.sorted((i, j) -> diff(stock.get(i), median).compareTo(diff(stock.get(i), median)))
				.map(productName -> RecipeRegistry.getInstance().getRecipeByName(productName))
				.collect(Collectors.toList());

	}

	private Integer diff(int value, int median) {
		return median - value;
	}

}
