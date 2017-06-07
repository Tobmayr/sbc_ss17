package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_DELIVERY_ORDER_AVAILABLE;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * DeliveryRobot to deliver orders to customers
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class DeliveryRobot extends Robot {

	private IDeliveryRobotService service;

	public DeliveryRobot(IDeliveryRobotService service, ChangeNotifer changeNotifer,
			ITransactionManager transactionManager, String id) {
		super(transactionManager, changeNotifer, id);
		this.service = service;
		notificationState = service.getInitialState();

	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			if (notificationState.get(IS_DELIVERY_ORDER_AVAILABLE)) {
				doTask(processNextDelivery);
			}

		}

	}

	/**
	 * gets next delivery from counter, checks for destination and puts order in destination space
	 */
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
			delivery.setDeliveryRobotId(this.getId());
			delivery.getProducts().forEach(p -> p.addContribution(getId(), Product.DELIVERD, DeliveryRobot.class));
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
		} else if(added && object instanceof NotificationMessage) {
			NotificationMessage message = (NotificationMessage) object;

			if(message.getMessageTyp()==NotificationMessage.NO_MORE_DELIVERY_ORDERS) {
				notificationState.put(IS_DELIVERY_ORDER_AVAILABLE, false);
			}
		}

	}
}
