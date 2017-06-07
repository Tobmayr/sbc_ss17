package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.*;

/**
 * Service robot implementation for both implementations
 */
public class ServiceRobot extends Robot implements IChangeListener {

	private IServiceRobotService service;
	private Order currentOrder;

	public ServiceRobot(IServiceRobotService service, ChangeNotifer changeNotifer,
			ITransactionManager transactionManager, String id) {
		super(transactionManager, changeNotifer, id);
		this.service = service;
		notificationState = service.getInitialState();

	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			if (!notificationState.get(NO_MORE_PRODUCTS_IN_STORAGE) && !notificationState.get(IS_COUNTER_FULL))
				doTask(getProductFromStorage);

			if (!notificationState.get(IS_COUNTER_EMPTY) && notificationState.get(IS_ORDER_AVAILABLE))
				doTask(processNextOrder);

			if (!notificationState.get(NO_MORE_PRODUCTS_IN_STORAGE) && !notificationState.get(IS_PREPACKAGE_LIMIT)) {
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
		packedOrder.setState(OrderState.PACKED);
		packedOrder.setServiceRobotId(getId());
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
		packedOrder.getProducts().forEach(p -> p.addContribution(getId(), Product.PACK_UP, getClass()));
		if (packedOrder.isHighPriority()) {
			service.sendNotification(new NotificationMessage(NotificationMessage.ORDER_PROCESSING_FREE), tx);
		}
		return service.putPackedOrderInTerminal(packedOrder, tx);

	};

	/**
	 * service robot should wait for more products in counter
	 * @return true if there enough products in stock
	 */
	private boolean waitForEnoughProducts() {
		Map<String, Integer> counterStock = service.getCounterStock();
		while (!enoughProducts(counterStock)) {
			counterStock = service.getCounterStock();
		}
		return true;
	}

	/**
	 * checks if there are enough products for order in counter
	 * @param counterStock
	 * @return true if enough products are available
	 */
	private boolean enoughProducts(Map<String, Integer> counterStock) {
		return currentOrder.getItemsMap().keySet().stream().allMatch((s) -> currentOrder.getItemsMap().get(s)
				.getAmount() <= SBCConstants.COUNTER_MAX_CAPACITY - counterStock.get(s));
	}

	/**
	 * get next order to work on
	 */
	ITransactionalTask processNextOrder = tx -> {
		currentOrder = service.getNextOrder(tx);
		if (currentOrder == null
				|| (notificationState.get(IS_ORDER_PROCESSING_LOCKED) && !currentOrder.isHighPriority())) {
			return false;
		}
		if (currentOrder.isDelivery()) {
			boolean maxWaitReached = System.currentTimeMillis()
					- currentOrder.getTimestamp().getTime() >= SBCConstants.DELIVERY_MAX_WAIT;
			currentOrder.setHighPriority(maxWaitReached);

			if (maxWaitReached) {
				service.sendNotification(new NotificationMessage(NotificationMessage.ORDER_PROCESSING_LOCKED), tx);
				waitForEnoughProducts();

			}
		}

		System.out.println("New order with id: " + currentOrder.getId() + " is now processed & prepared for packing");
		if (!packOrderAndPutInTerminal(tx)) {
			if (currentOrder.isDelivery()) {
				System.out.println("Not enough products in stock. Delviery Order  is returned to the collection area!");
				currentOrder.setState(OrderState.WAITING);
				return service.returnOrder(currentOrder, tx);
			} else {
				System.out.println("Not enough products in stock. Order has been declined!");
				currentOrder.setState(OrderState.UNGRANTABLE);
				return service.updateOrder(currentOrder, tx);
			}

		}

		return true;

	};

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

		missingProducts.forEach((s, i) -> {
			List<Product> temp = service.getProductsFromStorage(s, i, tx);
			if (temp != null && !temp.isEmpty())
				productsForCounter.addAll(temp);
		});

		if (productsForCounter.isEmpty()) {
			return false;
		}

		System.out.println("Stocking up the counter");
		for (Product product : productsForCounter) {
			product.addContribution(getId(), Product.TRANSFER_TO_COUNTER, getClass());
			if (!service.addToCounter(product, tx))
				return false;
		}
		return true;
	};

	/**
	 * prepack products from storage and put them in the terminal as prepackages
	 */
	ITransactionalTask prepackProducts = tx -> {
		List<Product> products = service.getProductsFromStorage(SBCConstants.PREPACKAGE_SIZE, tx);
		if (products == null || products.isEmpty())
			return false;

		Prepackage prepackage = new Prepackage();
		prepackage.setProducts(products);
		prepackage.setServiceRobotId(this.getId());
		return service.putPrepackageInTerminal(prepackage, tx);
	};

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && object instanceof Product) {
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_STORAGE)) {
				notificationState.put(NO_MORE_PRODUCTS_IN_STORAGE, false);
			} else if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)) {
				notificationState.put(IS_COUNTER_EMPTY, false);
				notificationState.put(IS_COUNTER_FULL, false);
			}
		} else if (added && object instanceof Order) {
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)
					&& ((Order) object).getState() == OrderState.ORDERED) {
				notificationState.put(IS_ORDER_AVAILABLE, true);
			}
		} else if (!added && object instanceof Prepackage) {
			notificationState.put(IS_PREPACKAGE_LIMIT, false);
		} else if (added && object instanceof NotificationMessage) {
			NotificationMessage message = (NotificationMessage) object;

			switch (message.getMessageTyp()) {
			case NotificationMessage.NO_MORE_PRODUCTS_IN_COUNTER:
				notificationState.put(IS_COUNTER_EMPTY, true);
				break;
			case NotificationMessage.NO_MORE_PRODUCTS_IN_STORAGE:
				notificationState.put(NO_MORE_PRODUCTS_IN_STORAGE, true);
				break;
			case NotificationMessage.ORDER_PROCESSING_LOCKED:
				notificationState.put(IS_ORDER_PROCESSING_LOCKED, true);
				break;
			case NotificationMessage.ORDER_PROCESSING_FREE:
				notificationState.put(IS_ORDER_PROCESSING_LOCKED, false);
				break;
			case NotificationMessage.NO_MORE_ORDERS:
				notificationState.put(IS_ORDER_AVAILABLE, false);
			case NotificationMessage.PREPACKAGE_LIMIT_REACHED:
				notificationState.put(IS_PREPACKAGE_LIMIT, true);
				case NotificationMessage.COUNTER_STOCK_FULL:
					notificationState.put(IS_COUNTER_FULL, true);
			default:
				break;
			}

		}

	}

}
