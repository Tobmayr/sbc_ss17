package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_DELIVERY_ORDER_AVAILABLE;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class DeliveryRobot extends Robot {

	private IDeliveryRobotService service;

	public DeliveryRobot(IDeliveryRobotService service, ChangeNotifer changeNotifer,
			ITransactionManager transactionManager, String id) {
		super(transactionManager, changeNotifer, id);
		this.service = service;
		notificationState=service.getInitialState();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));

	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			if (notificationState.get(IS_DELIVERY_ORDER_AVAILABLE)) {
				doTask(processNextDelivery);
				notificationState.put(IS_DELIVERY_ORDER_AVAILABLE, false);
			}

		}

	}

	ITransactionalTask processNextDelivery = tx -> {
		PackedOrder delivery = service.getPackedDeliveryOrder();
		if (delivery == null) {
			return false;
		}
		System.out.println("New delivery order with id: " + delivery.getId() + " is now processed & delivered");
		if (service.checkDestination(delivery)) {
			sleepFor(SBCConstants.DELIVER_DURATION);
			if (!service.deliverOrder(delivery))
				return false;
			sleepFor(SBCConstants.DELIVER_DURATION);
			delivery.setState(Order.OrderState.DELIVERED);
		} else {
			delivery.setState(Order.OrderState.UNDELIVERALBE);

		}
		return service.updateOrder(delivery);
	};

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_TERMINAL) && object instanceof PackedOrder
				&& ((PackedOrder) object).isDelivery()) {
			notificationState.put(IS_DELIVERY_ORDER_AVAILABLE, true);
		}

	}
}
