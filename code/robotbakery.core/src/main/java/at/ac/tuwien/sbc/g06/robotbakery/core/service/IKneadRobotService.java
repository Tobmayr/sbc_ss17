package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;

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
	 * there production timestamp.
	 * 
	 * @return
	 */
	List<Product> getBaseDoughsFromStorage();

	/**
	 * Returns a map containing the current storage stock information about each
	 * ingredient type.
	 * 
	 * @return
	 */
	Map<IngredientType, Integer> getIngredientStock();

	/**
	 * Returns a map containing the current counter stock for each product type.
	 * 
	 *
	 * @return
	 */
	Map<String, Integer> getCounterStock();

}
