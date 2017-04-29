package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

public interface ITableUIChangeListener extends IChangeListener {
	
	void onProductsAddedToCounter(Product product);

	void onProductRemovedFromCounter(Product product);
	
	void onOrderUpdated(Order order);

}
