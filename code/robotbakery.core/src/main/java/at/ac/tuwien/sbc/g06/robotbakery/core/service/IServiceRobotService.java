package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IServiceRobotService extends IRobotService{

	Order getNextOrder(ITransaction tx);

	boolean addToCounter(List<Product> products, ITransaction tx);

    Map<String, Integer> getCounterStock();

	boolean updateOrder(Order order, ITransaction tx);

	boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx);

	List<Product> getProductsFromStorage(String productType, int amount, ITransaction tx);

	List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx);


}
