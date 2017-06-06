package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;

/**
 * Created by Matthias Höllthaler on 13.05.2017.
 */
public interface IDeliveryRobotService extends IRobotService {

	PackedOrder getPackedDeliveryOrder();

	boolean checkDestination(PackedOrder order);

	boolean deliverOrder(PackedOrder order);

	boolean updateOrder(Order order);
}
