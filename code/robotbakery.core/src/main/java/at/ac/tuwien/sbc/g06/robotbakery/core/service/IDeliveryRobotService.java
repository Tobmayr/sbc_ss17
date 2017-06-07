package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;

/**
 * Created by Matthias Höllthaler on 13.05.2017.
 */
public interface IDeliveryRobotService extends IRobotService {

	/**
	 * Gets a packed order from the terminal
	 * @return packed order
	 */
	PackedOrder getPackedDeliveryOrder();

	/**
	 * checks if destination is existing and reachable
	 * @param order order with destination in it
	 * @return true for found, else false
	 */
	boolean checkDestination(PackedOrder order);

	/**
	 * delivers order to destination that is in the order
	 * @param order
	 * @return true for delivery, else false
	 */
	boolean deliverOrder(PackedOrder order);

	/**
	 * update order
	 * @param order order to update
	 * @return true for success, else false
	 */
	boolean updateOrder(Order order);
}
