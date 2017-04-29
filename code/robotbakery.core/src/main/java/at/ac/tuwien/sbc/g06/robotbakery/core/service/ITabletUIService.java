package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;

public interface ITabletUIService {

	boolean addOrderToCounter(Order order);

	PackedOrder getOrderPackage(Order order);

	boolean payOrder(Order order);


	
	

}
