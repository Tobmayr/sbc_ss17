package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

/**
 * Created by Matthias Höllthaler on 20.05.2017.
 */
public class DeliveryRobot extends Robot {

    private IDeliveryRobotService service;

    private DeliveryOrder delivery;

    public DeliveryRobot(IDeliveryRobotService service, ITransactionManager transactionManager, String id) {
        super(transactionManager, id);
        this.service = service;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));

    };


    @Override
    public void run() {
        service.startRobot();
        while(!Thread.interrupted()) {
            doTask(processNextDelivery);
            delivery = null;
        }

    }

    ITransactionalTask processNextDelivery = tx -> {
        delivery = service.getDelivery();
        if(delivery == null) {
            return false;
        }
        System.out.println("New delivery order with id: " + delivery.getId() + " is now processed & delivered");
        if(service.checkDestination(delivery.getDestination())) {
            sleepFor(5000);
            service.deliverOrder(delivery);
            sleepFor(5000);
        } else {
            delivery.setState(Order.OrderState.UNDELIVERABLE);
            return false;
        }
        return true;
    };

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		// TODO DO NOT IMPLEMENT
		
	}
}
