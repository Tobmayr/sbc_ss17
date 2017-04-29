package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface IServiceRobotService {

	Order getNextOrder(ITransaction tx);

	void addToCounter(List<Product> products, ITransaction tx);

	Product checkCounter();

	void updateOrder(Order order, ITransaction tx);

	void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx);
}
