package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

public interface ITabletUIService {

	boolean addOrderToCounter(Order order);

	List<Product> readProductsInCounter();

	PackedOrder getAndPayOrderPackage();
	
	

}
