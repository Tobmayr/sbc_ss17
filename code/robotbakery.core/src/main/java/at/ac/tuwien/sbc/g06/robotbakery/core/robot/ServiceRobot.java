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
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;

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

			doTask(getProductFromStorage);
			doTask(processNextOrder);

		}

	}

	ITransactionalTask declineOrder = tx -> {
		currentOrder.setState(OrderState.UNDELIVERABLE);
		service.updateOrder(currentOrder, tx);
		return true;

	};

	ITransactionalTask packOrderAndPutInTerminal = tx -> {
		PackedOrder packedOrder = new PackedOrder(currentOrder.getCustomerId(), currentOrder.getId());
		for (Item item : currentOrder.getItemsMap().values()) {
			List<Product> temp = service.getProductsFromCounter(item.getProductName(), item.getAmount(), tx);
			if (temp != null && !temp.isEmpty())
				packedOrder.addAll(temp);
			else
				return false;
		}
		
		//simulate packing duration
		sleepFor(1000,3000);

		for(Product product: packedOrder.getProducts()) {
			product.addContribution(getId(), ContributionType.PACK_UP, getClass());
		}

		if (service.putPackedOrderInTerminal(packedOrder, tx)) {
			currentOrder.setState(OrderState.DELIVERED);
			currentOrder.setServiceRobotId(getId());
			return service.updateOrder(currentOrder, tx);
		}
		return false;
	};

	ITransactionalTask processNextOrder = tx -> {
		currentOrder = service.getNextOrder(tx);
		if (currentOrder == null)
			return false;
		System.out.println("New order with id: " + currentOrder.getId() + " is now processed");
		if (!doTask(packOrderAndPutInTerminal)) {
			return doTask(declineOrder);
		}

		return true;

	};

	ITransactionalTask getProductFromStorage = tx -> {
		Map<String, Integer> missingProducts = service.getCounterStock();
		if (missingProducts == null || missingProducts.isEmpty())
			return false;
		missingProducts = CollectionsUtil.sortMapByValues(missingProducts, false);

		List<Product> productsForCounter = new ArrayList<>();
		for (Entry<String, Integer> entry : missingProducts.entrySet()) {
			List<Product> temp = service.getProductsFromStorage(entry.getKey(), entry.getValue(), tx);
			if (temp != null && !temp.isEmpty())
				productsForCounter.addAll(temp);
		}
		for (Product product: productsForCounter) {
			product.addContribution(getId(), ContributionType.TRANSFER_TO_COUNTER, getClass());
		}
		return service.addToCounter(productsForCounter, tx);
	};

}
