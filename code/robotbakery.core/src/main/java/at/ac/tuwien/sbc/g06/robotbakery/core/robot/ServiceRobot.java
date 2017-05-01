package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.List;
import java.util.SortedMap;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

public class ServiceRobot extends Robot {

	private IServiceRobotService service;
	private Order currentOrder;
	SortedMap<String, Integer> missingProducts;

	public ServiceRobot(IServiceRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {

			doTask(getCounterStock);
			doTask(processNextOrder);
			if(currentOrder!=null) doTask(checkCounter);


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

		return true;
	};

	ITransactionalTask checkCounter = tx -> {
		List<Product> products = service.checkCounter(currentOrder, tx);
		if(products == null)
			return false;
		System.out.println("Order with id: " + currentOrder.getId() + " checked, begin packing");
		if(!doTask(packOrderAndPutInTerminal)) {
			return doTask(declineOrder);
		}
		return true;
	};

	ITransactionalTask getCounterStock = tx -> {
		missingProducts = service.getCounterStock(tx);
		return true;
	};

	ITransactionalTask getProductFromStorage = tx -> {
		List<Product> productsForCounter = service.getProductFromStorage(missingProducts, tx);
		service.addToCounter(productsForCounter, tx);
		return true;
	};

	private PackedOrder packOrder(Order order) {
		System.out.println("Packing");

		// TODO: Add implementation
		return new PackedOrder(order.getCustomerId(), order.getId());
	}
}
