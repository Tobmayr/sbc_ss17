package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

public interface IServiceRobotService {

	Order getNextOrder();
	
	void addToCounter(List<Product> products);

	Product checkCounter();

	void updateOrder(Order order);

	void putPackedOrderInTerminal(PackedOrder packedOrder);
}
