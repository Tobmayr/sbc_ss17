package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

public interface ITabletUIService  {

	boolean addOrderToCounter(Order order);

	PackedOrder getOrderPackage();

	boolean payOrder(PackedOrder order);

	void initialize(UUID customerID, UUID id);

	Map<String, Integer> getInitialCounterProducts();




	
	

}
