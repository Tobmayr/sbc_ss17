package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class  JMSKneadRobotService implements IKneadRobotService{

	@Override
	public List<Product> getBaseDoughsFromStorage(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getCounterStock(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer integer, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaterPipe useWaterPipe(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
