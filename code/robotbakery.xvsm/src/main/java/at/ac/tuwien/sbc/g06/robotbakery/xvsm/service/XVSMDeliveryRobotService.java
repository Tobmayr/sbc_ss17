package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.DeliveryRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class XVSMDeliveryRobotService extends AbstractXVSMService implements IDeliveryRobotService {

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
    public boolean checkDestination(String destination) {
        try {
            this.destination = capi.lookupContainer(destination, XVSMConstants.BASE_SPACE_URI, MzsConstants.RequestTimeout.DEFAULT, null);
            return true;
        } catch (MzsCoreException e) {
            return false;
        }
    }

    @Override
    public boolean deliverOrder(DeliveryOrder order) {
        boolean delivered = write(order, destination, null);
        if(delivered) {
            order.setState(Order.OrderState.PAID);
        }
        return delivered;
    }
}
