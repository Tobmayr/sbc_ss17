package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;

/**
 * Interface for Customer UI
 */
public interface ITabletUIService  {

	/**
	 * adds order to bakery
	 * @param order order with products
	 * @return true for success or false for exception
	 */
	boolean addOrderToCounter(Order order);

	/**
	 * get packed order from terminal
	 * @return packed order with products in it
	 */
	PackedOrder getPackedOrder(Order order);

	/**
	 * pay order at terminal
	 * @param order order to pay
	 * @return true for success or false for exception
	 */
	boolean payOrder(PackedOrder order);

	/**
	 * looks up the current state of the counter
	 * @return map with product names as key and the amount that is available in the counter as value
	 */
	Map<String, Integer> getInitialCounterProducts();

	/**
	 * looks up the current state of prepackages at the terminal
	 * @return list with prepackages
	 */
	List<Prepackage> getInitialPrepackages();

	/**
	 * get certain prepackage
	 * @param packageId uuid of prepackage
	 * @return prepackage with given uuid
	 */
	Prepackage getPrepackage(UUID packageId);

	/**
	 * writes status in prepackage
	 * @param prepackage prepackage that should be updated
	 * @return true if success, else false
	 */
	boolean updatePrepackage(Prepackage prepackage);
	





	
	

}
