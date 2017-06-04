package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Templates;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * Service robot implementation for both implementations
 */
public class ServiceRobot extends Robot implements IChangeListener {

	private IServiceRobotService service;
	private Order currentOrder;
	private boolean isStorageEmtpy = true;
	private boolean isCounterEmpty = true;
	private boolean isOrderAvailable = false;
	private boolean isPrepackagesLimit = false;

	public ServiceRobot(IServiceRobotService service, ITransactionManager transactionManager, String id) {
		super(transactionManager, id);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));

	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			if (!isStorageEmtpy)
				doTask(getProductFromStorage);

			if (!isCounterEmpty && isOrderAvailable)
				doTask(processNextOrder);

			if (!isPrepackagesLimit && !isStorageEmtpy) {
				doTask(prepackProducts);
			}

		}

	}

	/**
	 * pack order and put it in terminal for customer
	 * 
	 * @param tx
	 *            Transaction
	 * @return true if success, false if not enough products in counter or
	 *         exception
	 */
	private boolean packOrderAndPutInTerminal(ITransaction tx) {
		PackedOrder packedOrder = new PackedOrder(currentOrder);
		packedOrder.setState(OrderState.DELIVERED);
		packedOrder.setServiceRobotId(getId());
		for (Item item : currentOrder.getItemsMap().values()) {
			List<Product> temp = service.getProductsFromCounter(item.getProductName(), item.getAmount(), tx);
			if (temp != null && temp.size() == item.getAmount())
				packedOrder.addAll(temp);
			else
				return false;
		}
		Map<String, Integer> missingProducts;
		// simulate packing duration
		sleepFor(1000, 3000);
		// Update contribution
		packedOrder.getProducts().forEach(p -> p.addContribution(getId(), ContributionType.PACK_UP, getClass()));

		return service.putPackedOrderInTerminal(packedOrder, tx);

	};

	/**
	 * get next order to work on
	 */
	ITransactionalTask processNextOrder = tx -> {
		currentOrder = service.getNextOrder(tx);
		if (currentOrder == null) {
			isOrderAvailable = false;
			return false;
		}

		System.out.println("New order with id: " + currentOrder.getId() + " is now processed & prepared for packing");
		if (!packOrderAndPutInTerminal(tx)) {
			if (currentOrder instanceof DeliveryOrder) {
				System.out.println("Not enough products in stock. Order has is returned to collection area!");
				currentOrder.setState(OrderState.OPEN);
				return service.returnDeliveryOrder((DeliveryOrder) currentOrder, tx);
			} else {
				System.out.println("Not enough products in stock. Order has been declined!");
				currentOrder.setState(OrderState.UNDELIVERABLE);
				return service.updateOrder(currentOrder, tx);
			}

		}
		return true;

	};

	private int testInt;
	/**
	 * get products from storage to fill up counter, get only products that are
	 * missing in counter
	 */
	ITransactionalTask getProductFromStorage = tx -> {

		Map<String, Integer> missingProducts = service.getCounterStock();
		if (missingProducts == null)
			return false;
		missingProducts = CollectionsUtil.sortMapByValues(missingProducts, false);

		List<Product> productsForCounter = new ArrayList<>();
		testInt = Integer.MAX_VALUE;
		missingProducts.forEach((s, i) -> {
			if (i < testInt)
				testInt = i;
			List<Product> temp = service.getProductsFromStorage(s, i, tx);
			if (temp != null && !temp.isEmpty())
				productsForCounter.addAll(temp);
		});

		if (testInt == 10) {
			isCounterEmpty = true;
		}

		if (productsForCounter.isEmpty()) {
			isStorageEmtpy = true;
			return false;
		}

		System.out.println("Stocking up the counter");
		for (Product product : productsForCounter) {
			product.addContribution(getId(), ContributionType.TRANSFER_TO_COUNTER, getClass());
			if (!service.addToCounter(product, tx))
				return false;
		}
		return true;
	};

	ITransactionalTask prepackProducts = tx -> {
		if (service.readAllPrepackages() < SBCConstants.PREPACKAGE_MAX_AMOUNT) {
			List<Product> products = service.getProductsFromStorage(SBCConstants.PREPACKAGE_SIZE, tx);
			if (products == null || products.isEmpty())
				return false;

			Prepackage prepackage = new Prepackage();
			prepackage.setProducts(products);
			prepackage.setServiceRobotId(this.getId());
			return service.putPrepackeInTerminal(prepackage, tx);
		} else {
			isPrepackagesLimit = true;
			return true;
		}
	};

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && object instanceof Product) {
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_STORAGE)) {
				isStorageEmtpy = false;
			} else if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)) {
				isCounterEmpty = false;
			}
		} else if (added && object instanceof Order) {
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)) {
				isOrderAvailable = true;
			}

			// TODO: Add Notifications for Prepackages
		}

	}

}
