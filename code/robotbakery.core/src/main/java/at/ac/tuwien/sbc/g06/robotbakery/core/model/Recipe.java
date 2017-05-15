package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * recipe that is used to make a product
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Recipe implements Serializable {
	public enum IngredientType {
		FLOUR, WATER, EGGS, BAKING_MIX_SPICY, BAKING_MIX_SWEET;
	}

	private final Map<IngredientType, Integer> ingredients = new HashMap<>();
	private final String productName;
	private final double pricePerUnit;
	private final int bakeTime;

	public Recipe(String productName, List<IngredientType> ingredientTypes, List<Integer> amounts,
			double pricePerUnit, int bakeTime) {
		super();
		this.productName = productName;
		if (ingredientTypes.size() == amounts.size()) {
			IntStream.range(0, amounts.size()).forEach(i -> ingredients.put(ingredientTypes.get(i), amounts.get(i)));
		}

		this.pricePerUnit = pricePerUnit;
		this.bakeTime=bakeTime;
	}

	public String getProductName() {
		return productName;
	}

	public Integer getAmount(IngredientType type) {
		Integer result = ingredients.get(type);
		if (result == null)
			result = 0;
		return result;

	}

	public Map<IngredientType, Integer> getIngredients() {
		return ingredients;
	}
	
	

	public int getBakeTime() {
		return bakeTime;
	}

	/**
	 * get the ingredients for the recipe that are used to finish a base dough
	 * @return set of ingredienttypes and amount
	 */
	public Set<Entry<IngredientType, Integer>> getAdditionalIngredients() {
		return ingredients.entrySet().stream()
				.filter(e -> e.getKey() != IngredientType.FLOUR && e.getKey() != IngredientType.WATER)
				.collect(Collectors.toSet());
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	@Override
	public String toString() {
		return "Recipe [ingredients=" + ingredients + ", productName=" + productName + ", pricePerUnit=" + pricePerUnit
				+ "]";
	}

}
