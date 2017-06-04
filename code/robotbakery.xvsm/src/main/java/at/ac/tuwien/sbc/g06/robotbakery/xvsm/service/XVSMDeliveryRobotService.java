package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.DeliveryRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

import java.net.URI;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class XVSMDeliveryRobotService extends GenericXVSMService implements IDeliveryRobotService {

    private XVSMRobotService robotService;
    private ContainerReference terminalContainer;
    private ContainerReference destination;

    public XVSMDeliveryRobotService(Capi capi) {
        super(new Capi(DefaultMzsCore.newInstance()));
        this.terminalContainer = getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);
        this.robotService = new XVSMRobotService(capi, DeliveryRobot.class.getSimpleName());

    }

    @Override
    public void startRobot() {
        robotService.startRobot();
    }

    @Override
    public void shutdownRobot() {
        robotService.shutdownRobot();
    }

    @Override
    public DeliveryOrder getDelivery() {
        Query query = new Query()
                .filter(Matchmakers.and(Property.forClass(DeliveryOrder.class, "*").exists(), Property.forName("*", "OrderState").equalTo(OrderState.OPEN)))
                .sortup(ComparableProperty.forName("*", "timestamp"));
        Integer available = test(terminalContainer, null,
                QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX));
        if(available>0) {
            return takeFirst(terminalContainer, null, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX));
        }

        return null;
    }

    @Override
    public boolean checkDestination(URI deliveryURI) {
        try {
            this.destination = capi.lookupContainer(XVSMConstants.DELIVERY_CONTAINER_NAME, deliveryURI, MzsConstants.RequestTimeout.DEFAULT, null);
            return true;
        } catch (MzsCoreException e) {
            return false;
        }
    }

    @Override
    public boolean deliverOrder(DeliveryOrder order) {
        boolean delivered = false;
        if(destination!=null) {
            delivered = write(order, destination, null);
        }
        if(delivered) {
            order.setState(Order.OrderState.PAID);
        } else {
            order.setState(OrderState.UNDELIVERABLE);
        }
        return delivered;
    }
}
