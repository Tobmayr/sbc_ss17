package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class DeliveryRobot extends Robot {

	private IDeliveryRobotService service;

	public DeliveryRobot(IDeliveryRobotService service, ITransactionManager transactionManager, String id) {
		super(transactionManager, id);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));

	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			doTask(processNextDelivery);

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
		// TODO DO NOT IMPLEMENT

	}
}
