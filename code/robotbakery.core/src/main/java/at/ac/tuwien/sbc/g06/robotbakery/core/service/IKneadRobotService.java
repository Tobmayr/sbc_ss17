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

	List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer integer, ITransaction tx);

	boolean useWaterPipe(long time, ITransaction tx);

	boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx);

	Product getProductFromStorage(UUID id, ITransaction tx);

	FlourPack getPackFromStorage(ITransaction tx);

	boolean putPackInStorage(FlourPack pack, ITransaction tx);

	boolean putDoughInBakeroom(Product nextProduct, ITransaction tx);

	



}
