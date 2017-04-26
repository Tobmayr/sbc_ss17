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

	private Map<IngredientType, Integer> ingredientMap;
	private final String productName;
	private final double pricePerUnit;

	public Recipe(String productName, List<IngredientType> ingredientTypes, List<Integer> amounts,
			double pricePerUnit) {
		super();
		this.productName = productName;
		ingredientMap = new HashMap<>();
		if (ingredientTypes.size() == amounts.size()) {
			IntStream.range(0, amounts.size()).forEach(i -> ingredientMap.put(ingredientTypes.get(i), amounts.get(i)));
		}

		this.pricePerUnit = pricePerUnit;
	}

	public String getProductName() {
		return productName;
	}

	public int getAmount(IngredientType type) {
		return ingredientMap.get(type);
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	@Override
	public String toString() {
		return "Recipe [ingredientAmountMap=" + ingredientMap + ", productName=" + productName + ", pricePerUnit="
				+ pricePerUnit + "]";
	}

}
