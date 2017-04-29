package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

public class ServiceRobot extends Robot {

	private IServiceRobotService service;
	private Order currentOrder;

	public ServiceRobot(IServiceRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {

			doTask(processNextOrder);
			doTask(checkCounter);

		}

	}

	ITransactionalTask packOrderAndPutInTerminal = tx -> {
		PackedOrder packedOrder = packOrder(currentOrder);
		if (packedOrder == null)
			return false;
		service.putPackedOrderInTerminal(packedOrder, tx);
		service.putPackedOrderInTerminal(packedOrder, null);
		currentOrder.setState(OrderState.DELIVERED);
		currentOrder.setServiceRobotId(getId());
		service.updateOrder(currentOrder, tx);
		return true;
	};

	ITransactionalTask declineOrder = tx -> {
		currentOrder.setState(OrderState.UNDELIVERABLE);
		service.updateOrder(currentOrder, tx);
		return true;

	};

	ITransactionalTask processNextOrder = tx -> {
		currentOrder = service.getNextOrder(tx);
		if (currentOrder == null)
			return false;
		System.out.println("New order with id: " + currentOrder.getId() + " received");
		if (!doTask(packOrderAndPutInTerminal)) {
			return doTask(declineOrder);
		}

		return true;
	};

	ITransactionalTask checkCounter = tx -> {
		// TODO: Add implementation

		return true;
	};

	private PackedOrder packOrder(Order order) {
		System.out.println("Packing");

		// TODO: Add implementation
		return new PackedOrder(order.getCustomerId(), order.getId());
	}
}
