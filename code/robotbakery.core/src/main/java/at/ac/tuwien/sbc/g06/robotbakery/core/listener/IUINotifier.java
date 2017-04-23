package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

/**
 * 
 * Interface which can be used by the bakery UI to get notified about changes
 * happening in the bakery (back-end logic)
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface IUINotifier extends Serializable {

	/**
	 * Is invoked when an order has been added to the counter
	 * 
	 * @param order
	 *            newly added order
	 */
	 void onOrderAdded(Order order);

	/**
	 * Is invoked when the state of an order in the counter has been changed
	 * 
	 * @param order
	 */
	 void onOrderStateChanged(Order order);

	

}
