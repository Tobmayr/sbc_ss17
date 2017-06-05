package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;

import java.net.URI;

/**
 * Created by Matthias Höllthaler on 13.05.2017.
 */
public interface IDeliveryRobotService extends IRobotService {

    DeliveryOrder getDeliveryOrder();

    boolean checkDestination(DeliveryOrder order);

    boolean deliverOrder(DeliveryOrder order);
}
