package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.COUNTER_MAX_CAPACITY;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.*;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.PRODUCTS_NAMES;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Matchmaker;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMServiceRobotService extends GenericXVSMService implements IServiceRobotService {

	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;
	private final ContainerReference storageContainer;

	public XVSMServiceRobotService() {
		super(new Capi(DefaultMzsCore.newInstance()));
		counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);
		storageContainer = getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		Query query = new Query()
				.filter(Matchmakers.or(Property.forName("*", "state").equalTo(OrderState.ORDERED),
						Property.forName("*", "state").equalTo(OrderState.WAITING)))
				.sortup(ComparableProperty.forName("*", "timestamp")).cnt(1);
		return takeFirst(counterContainer, null,
				QueryCoordinator.newSelector(query, MzsConstants.Selecting.DEFAULT_COUNT));

	}

	@Override
	public boolean addToCounter(Product product, ITransaction tx) {
		Query query = new Query().filter(Property.forName("*", "productName").equalTo(product.getProductName()));
		Integer available = test(counterContainer, null,
				QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX));
		if (available < SBCConstants.COUNTER_MAX_CAPACITY)
			write(product, counterContainer, tx);
		return true;

	}

	@Override
	public List<Product> getProductsFromStorage(String productName, int amount, ITransaction tx) {
		return getProducts(storageContainer, productName, amount, tx);
	}

	@Override
	public List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx) {
		return getProducts(counterContainer, productName, amount, tx);
	}

	private List<Product> getProducts(ContainerReference containerReference, String productName, int amount,
			ITransaction tx) {
		Matchmaker product = Property.forName("*", "productName").equalTo(productName);
		Matchmaker type = Property.forName("*", "type").equalTo(BakeState.FINALPRODUCT);
		Query query = new Query().filter(Matchmakers.and(product, type)).cnt(0, amount);
		return take(containerReference, tx, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX));
	}

	@Override
	public boolean updateOrder(Order order, ITransaction tx) {
		return write(order, counterContainer, tx);

	}

	@Override
	public boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		return write(packedOrder, terminalContainer, tx);
	}

	@Override
	public Map<String, Integer> getCounterStock() {
		Map<String, Integer> missingProducts = new HashMap<>();

		for (String name : PRODUCTS_NAMES) {
			Query query = new Query().filter(Property.forName("*", "productName").equalTo(name));
			Integer available = test(counterContainer, null,
					QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX));
			if (available < COUNTER_MAX_CAPACITY) {
				missingProducts.put(name, COUNTER_MAX_CAPACITY - available);
			}
		}

		return missingProducts;
	}

	@Override
	public boolean returnOrder(Order currentOrder, ITransaction tx) {
		return write(currentOrder, counterContainer, tx);
	}

	@Override
	public boolean putPrepackageInTerminal(Prepackage prepackage, ITransaction tx) {
		return write(prepackage, terminalContainer, tx);
	}

	@Override
	public List<Product> getProductsFromStorage(int amount, ITransaction tx) {
		Query query = new Query().filter(Property.forName("*", "type").equalTo(BakeState.FINALPRODUCT)).cnt(amount);
		return take(storageContainer, tx, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
				TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX));
	}

	@Override
	public Map<String, Boolean> getInitialState() {
		Query productQuery = new Query().filter(Property.forName("*", "type").equalTo(BakeState.FINALPRODUCT));
		Query highPriorityQuery = new Query().filter(Property.forName("*", "highPriority").equalTo(true));
		Map<String, Boolean> notificationState = new HashMap<>();
		notificationState.put(IS_COUNTER_EMPTY,
				test(counterContainer, null, TypeCoordinator.newSelector(Product.class)) == 0);
		notificationState.put(IS_COUNTER_FULL,
				test(counterContainer, null, TypeCoordinator.newSelector(Product.class)) == 40);
		notificationState.put(NO_MORE_PRODUCTS_IN_STORAGE, test(storageContainer, null, QueryCoordinator.newSelector(productQuery),
				TypeCoordinator.newSelector(Product.class)) == 0);
		notificationState.put(IS_ORDER_AVAILABLE,
				test(counterContainer, null, TypeCoordinator.newSelector(Order.class)) > 0);
		notificationState.put(IS_PREPACKAGE_LIMIT, test(terminalContainer, null,
				TypeCoordinator.newSelector(Prepackage.class)) >= SBCConstants.PREPACKAGE_MAX_AMOUNT);
		notificationState.put(IS_ORDER_PROCESSING_LOCKED, test(counterContainer, null,
				QueryCoordinator.newSelector(highPriorityQuery), TypeCoordinator.newSelector(Order.class)) > 0);
		return notificationState;
	}

	@Override
	public boolean sendNotification(NotificationMessage notification, ITransaction tx) {
		return write(notification, counterContainer, tx);
	}

}
