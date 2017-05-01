package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;

public interface ITabletUIService {

	boolean addOrderToCounter(Order order);

	PackedOrder getOrderPackage();

	boolean payOrder(Order order);

	void initialize(UUID customerID, UUID id);




	
	

}
