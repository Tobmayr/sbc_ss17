package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.SortedMap;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public interface IKneadRobotService {

	/**
	 * Returns all (products considered as) base doughs in storage ordered by
	 * there production timestamp. (asc)
	 * 
	 * @return
	 */
	List<Product> getBaseDoughsFromStorage();

	/**
	 * Returns a sorted map containing the current storage stock information about each ingredient
	 * type (desc)
	 * 
	 * @return
	 */
	SortedMap<IngredientType, Integer> getIngredientStock();

	/**
	 * Returns a sorted map containing the current counter stock for each product type. (asc)
	 *
	 * @return
	 */
	SortedMap<String, Integer> getCounterStock();

}
