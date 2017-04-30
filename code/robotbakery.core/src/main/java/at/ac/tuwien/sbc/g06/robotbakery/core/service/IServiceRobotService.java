package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.SortedMap;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IServiceRobotService {

	Order getNextOrder(ITransaction tx);

	void addToCounter(List<Product> products, ITransaction tx);

    SortedMap<String, Integer> getCounterStock(ITransaction tx);

	List<Product> checkCounter(Order order, ITransaction tx);

	void updateOrder(Order order, ITransaction tx);

	List<Product> getProductFromStorage(SortedMap<String, Integer> missingProducts, ITransaction tx);

	void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx);
}
