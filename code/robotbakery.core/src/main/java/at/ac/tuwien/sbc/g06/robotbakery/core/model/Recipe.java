package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class Recipe extends Entity {
	private static Logger logger = Logger.getLogger(Recipe.class);

	public enum Ingredient {
		FLOUR, WATER, EGGS, BACKING_MIX_SPICY, BACKING_MIX_SWEET;
	}

	private Map<Ingredient, Integer> ingredientAmountMap;

	private String productName;
	private double pricePerUnit;

	public Recipe(String productName) {
		super();
		this.productName = productName;
		ingredientAmountMap = new HashMap<>();
	}

	public String getProductName() {
		return productName;
	}

	public void addIngredient(Ingredient ingredient, int amount) {
		ingredientAmountMap.put(ingredient, amount);
	}

	public int getAmount(Ingredient ingredient) {
		return ingredientAmountMap.get(ingredient);
	}

	public double getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(double pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

}
