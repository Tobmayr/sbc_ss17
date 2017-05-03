package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public interface IKneadRobotService extends IRobotService{

	/**
	 * Returns all (products considered as) base doughs in storage ordered by
	 * there production timestamp.
	 * 
	 * @return
	 */
	List<Product> checkBaseDoughsInStorage();

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

	/**
	 * Get the specified amount of ingredients for a certain type
	 * @param type Get ingredients of this type
	 * @param integer amount of ingredients to get
	 * @param tx Transaction
	 * @return return list of ingredients or null for exception
	 */
	List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer integer, ITransaction tx);

	/**
	 * Reserves water pipe for use
	 * @param time water pipe is used for specified time in milliseconds
	 * @param tx Transaction
	 * @return true for success and false for exception
	 */
	boolean useWaterPipe(long time, ITransaction tx);

	/**
	 * puts base dough in storage
	 * @param nextProduct product to insert
	 * @param tx Transaction
	 * @return true for successful insert or false for exception
	 */
	boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx);

	/**
	 *
	 * @param id id of product
	 * @param tx Transaction
	 * @return product or null for exception
	 */
	Product getProductFromStorage(UUID id, ITransaction tx);

	/**
	 * get flour from storage, prefer open packs
	 * @param tx Transaction
	 * @return FlourPack
	 */
	FlourPack getPackFromStorage(ITransaction tx);

	/**
	 * puts flour pack to storage
	 * @param pack pack to put in storage
	 * @param tx Transaction
	 * @return true for success and false for exception
	 */
	boolean putPackInStorage(FlourPack pack, ITransaction tx);

	/**
	 * Put finished dough in bakeroom
	 * @param nextProduct finished dough product
	 * @param tx Transaction
	 * @return true for success or false for exception
	 */
	boolean putDoughInBakeroom(Product nextProduct, ITransaction tx);

	



}
