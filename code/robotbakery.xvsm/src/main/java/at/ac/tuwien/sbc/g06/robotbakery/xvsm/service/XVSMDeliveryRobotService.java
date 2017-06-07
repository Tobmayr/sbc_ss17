package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.HashMap;
import java.util.Map;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.DeliveryRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class XVSMDeliveryRobotService extends GenericXVSMService implements IDeliveryRobotService {

	private XVSMRobotService robotService;
	private ContainerReference terminalContainer;
	private ContainerReference destinationContainer;
	private ContainerReference counterContainer;

	public XVSMDeliveryRobotService() {
		super(new Capi(DefaultMzsCore.newInstance()));
		this.terminalContainer = getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);
		this.counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
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
	public PackedOrder getPackedDeliveryOrder() {
		Query query = new Query().filter(Property.forName("*", "delivery").equalTo(true))
				.sortup(ComparableProperty.forName("*", "timestamp"));
		return takeFirst(terminalContainer, null, QueryCoordinator.newSelector(query),
				TypeCoordinator.newSelector(PackedOrder.class));

	}

	@Override
	public boolean checkDestination(PackedOrder deliveryOrder) {
		try {
			this.destinationContainer = capi.lookupContainer(XVSMConstants.DELIVERY_CONTAINER_NAME,
					deliveryOrder.getDeliveryAddress(), MzsConstants.RequestTimeout.DEFAULT, null);
			return true;
		} catch (MzsCoreException e) {
			return false;
		}
	}

	@Override
	public boolean deliverOrder(PackedOrder order) {
		boolean delivered = false;
		if (destinationContainer != null) {
			delivered = write(order, destinationContainer, null);
		}
		if (delivered) {
			order.setState(Order.OrderState.DELIVERED);
		} else {
			order.setState(OrderState.UNDELIVERALBE);

		}
		write(order, counterContainer, null);
		return delivered;
	}

	@Override
	public boolean updateOrder(Order delivery) {
		return write(delivery, counterContainer, null);
	}

	@Override
	public Map<String, Boolean> getInitialState() {
		Query query = new Query().filter(Property.forName("*", "delivery").equalTo(true));
		boolean avaible = test(terminalContainer, null, QueryCoordinator.newSelector(query)) > 0;
		Map<String, Boolean> map = new HashMap<>();
		map.put(SBCConstants.NotificationKeys.IS_DELIVERY_ORDER_AVAILABLE, avaible);
		return map;
	}
}
