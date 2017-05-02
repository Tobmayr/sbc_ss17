package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;

/**
 * 
 * Listener-Interface which can be used to react to changes in the bakery (i.e.
 * changing objects of the counter,terminal,storage oder bakeroom) Implemented
 * by the GUI to enable automated change-based refreshing.
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface IBakeryUIChangeListener extends IChangeListener{

	/**
	 * Is invoked when an order has been added to the counter or an existing
	 * order has been modified;
	 * 
	 * @param order
	 *            newly added or updated order
	 */
	void onOrderAddedOrUpdated(Order order);

	void onProductAddedToStorage(Product product);

	void onProductRemovedFromStorage(Product product);

	void onProductAddedToCounter(Product product);

	void onProductRemovedFromCounter(Product product);
	
	void onProductAddedToBakeroom(Product product);
	
	void onProductRemovedFromBakeroom(Product product);
	
	void onProductAddedToTerminal(Product product);
	
	void onProductRemovedFromTerminal(Product product);

	void onIngredientAddedToStorage(Ingredient ingredient);

	void onIngredientRemovedFromStorage(Ingredient ingredient);
	
	void onRobotStart(Class<? extends Robot> robot);

	void onRobotShutdown(Class<? extends Robot> robot);
	

}
