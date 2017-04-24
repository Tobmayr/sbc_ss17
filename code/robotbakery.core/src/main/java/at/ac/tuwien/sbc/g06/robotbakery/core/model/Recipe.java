package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class Recipe implements Serializable{

	private Map<String, Integer> ingredientMap;
	private String productName;
	private double pricePerUnit;

	public Recipe(String productName) {
		super();
		this.productName = productName;
		ingredientMap = new HashMap<>();
	}

	public String getProductName() {
		return productName;
	}

	public void addIngredients(Ingredient... ingredients) {
		Arrays.asList(ingredients)
				.forEach(ingredient -> ingredientMap.put(ingredient.getType(), ingredient.getAmount()));
	}

	public int getAmount(String type) {
		return ingredientMap.get(type);
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	@Override
	public String toString() {
		return "Recipe [ingredientAmountMap=" + ingredientMap + ", productName=" + productName + ", pricePerUnit="
				+ pricePerUnit + "]";
	}

}
