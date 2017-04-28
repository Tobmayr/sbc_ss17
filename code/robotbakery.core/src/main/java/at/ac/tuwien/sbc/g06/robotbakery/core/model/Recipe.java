package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Recipe implements Serializable {
	public enum IngredientType {
		FLOUR, WATER, EGGS, BAKING_MIX_SPICY, BAKING_MIX_SWEET;
	}

	private final Map<IngredientType, Integer> baseIngredients = new HashMap<>();
	private final Map<IngredientType, Integer> additionalIngredients = new HashMap<>();
	private final String productName;
	private final double pricePerUnit;

	public Recipe(String productName, List<IngredientType> ingredientTypes, List<Integer> amounts,
			double pricePerUnit) {
		super();
		this.productName = productName;
		if (ingredientTypes.size() == amounts.size()) {
			IntStream.range(0, amounts.size()).forEach(i -> addIngredients(ingredientTypes.get(i), amounts.get(i)));
		}

		this.pricePerUnit = pricePerUnit;
	}

	private void addIngredients(IngredientType type, int amount) {
		switch (type) {
		case BAKING_MIX_SPICY:
		case BAKING_MIX_SWEET:
		case EGGS:
			additionalIngredients.put(type, amount);
			break;
		case FLOUR:
		case WATER:
			baseIngredients.put(type, amount);
			break;
		default:
			break;

		}
	}

	public String getProductName() {
		return productName;
	}

	public Integer getAmount(IngredientType type) {
		Integer result = null;
		result = baseIngredients.get(type);
		if (result == null)
			return additionalIngredients.get(type);
		return result;

	}

	
	public Map<IngredientType, Integer> getBaseIngredients() {
		return baseIngredients;
	}

	public Map<IngredientType, Integer> getAdditionalIngredients() {
		return additionalIngredients;
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	@Override
	public String toString() {
		return "Recipe [baseIngredients=" + baseIngredients + ", additionalIngredients=" + additionalIngredients
				+ ", productName=" + productName + ", pricePerUnit=" + pricePerUnit + "]";
	}

}
