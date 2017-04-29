package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.List;
import java.util.SortedMap;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public class XVSMKneadRobotService implements IKneadRobotService {

	@Override
	public List<Product> getBaseDoughsFromStorage(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedMap<IngredientType, Integer> getIngredientStock(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SortedMap<String, Integer> getCounterStock(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

}
