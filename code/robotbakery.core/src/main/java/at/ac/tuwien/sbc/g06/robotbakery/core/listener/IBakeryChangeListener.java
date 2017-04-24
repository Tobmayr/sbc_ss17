package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;

/**
 * 
 * Listener-Interface which can be used to react to changes in the bakery
 * (i.e. changing objects of the counter,terminal,storage oder bakeroom)
 * Implemented by the GUI to enable automated change-based refreshing.
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface IBakeryChangeListener {

	/**
	 * Is invoked when an order has been added to the counter or an existing order has been modified;
	 * 
	 * @param order
	 *            newly added or updated order
	 */
	 void onOrderAddedOrUpdated(Order order);
	 
	 void onProductAdded(Product product);
	 
	 void onIngredientAdded(Ingredient ingredient);
	 
	 
	 

	

}
