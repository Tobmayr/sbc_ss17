package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;

/**
 * Service robot implementation for both implementations
 */
public class ServiceRobot extends Robot {

	private IServiceRobotService service;
	private Order currentOrder;

	public ServiceRobot(IServiceRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));

	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {

			 doTask(getProductFromStorage);
			doTask(processNextOrder);

		}

	}

	private boolean packOrderAndPutInTerminal(ITransaction tx) {
		PackedOrder packedOrder = new PackedOrder(currentOrder.getCustomerId(), currentOrder.getId());
		for (Item item : currentOrder.getItemsMap().values()) {
			List<Product> temp = service.getProductsFromCounter(item.getProductName(), item.getAmount(), tx);
			if (temp != null && temp.size() == item.getAmount())
				packedOrder.addAll(temp);
			else
				return false;
		}

		// simulate packing duration
		sleepFor(1000, 3000);
		// Update contribution
		packedOrder.getProducts().forEach(p -> p.addContribution(getId(), ContributionType.PACK_UP, getClass()));

		if (service.putPackedOrderInTerminal(packedOrder, tx)) {
			currentOrder.setState(OrderState.DELIVERED);
			currentOrder.setServiceRobotId(getId());
			System.out.println("Placing packed order in terminal");
			return service.updateOrder(currentOrder, tx);
		}
		return false;
	};

	ITransactionalTask processNextOrder = tx -> {
		currentOrder = service.getNextOrder(tx);
		if (currentOrder == null)
			return false;
		System.out.println("New order with id: " + currentOrder.getId() + " is now processed & prepared for packing");
		if (!packOrderAndPutInTerminal(tx)) {
			System.out.println("Not enough products in stock. Order has been declined!");
			currentOrder.setState(OrderState.UNDELIVERABLE);
			return service.updateOrder(currentOrder, tx);
		}
		return true;

	};

	ITransactionalTask getProductFromStorage = tx -> {
		System.out.println("Stocking up the counter");
		Map<String, Integer> missingProducts = service.getCounterStock();
		if (missingProducts == null || missingProducts.isEmpty())
			return false;
		missingProducts = CollectionsUtil.sortMapByValues(missingProducts, false);
		List<Product> productsForCounter = new ArrayList<>();

		missingProducts.forEach((s, i) -> {
			List<Product> temp = service.getProductsFromStorage(s, i, tx);
			if (temp != null && !temp.isEmpty())
				productsForCounter.addAll(temp);
		});

		if (productsForCounter.isEmpty())
			return false;
		for (Product product : productsForCounter) {
			product.addContribution(getId(), ContributionType.TRANSFER_TO_COUNTER, getClass());
			if (!service.addToCounter(product, tx))
				return false;
		}
		return true;
	};

}
