package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IServiceRobotService {

	Order getNextOrder(ITransaction tx);

	boolean addToCounter(List<Product> products, ITransaction tx);

    TreeMap<String, Integer> getCounterStock(ITransaction tx);

	List<Product> checkCounter(Order order, ITransaction tx);

	void updateOrder(Order order, ITransaction tx);

	List<Product> getProductFromStorage(Map<String, Integer> missingProducts, ITransaction tx);

	void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx);
}
