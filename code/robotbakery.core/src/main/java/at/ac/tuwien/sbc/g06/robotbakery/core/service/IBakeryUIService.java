package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.io.Serializable;
import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

/**
 * interface for bakery UI
 */
public interface IBakeryUIService {

	/**
	 * adds ingredients to storage
	 * 
	 * @param ingredients
	 *            list of ingredients
	 * @return true for successful or false for exception
	 */
	boolean addIngredientsToStorage(List<Ingredient> ingredients);

	void addItemsToStorage(List<Serializable> items);

	void addProductsToCounter(List<Product> products);

	void addProductsToBakeroom(List<Product> forBakeroom);
	
	
}
