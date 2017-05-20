package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;

/**
 * Created by Matthias Höllthaler on 13.05.2017.
 */
public interface IDeliveryRobotService extends IRobotService {

    DeliveryOrder getDelivery();

    boolean checkDestination();

    boolean deliverOrder();
}
