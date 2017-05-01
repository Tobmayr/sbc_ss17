package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.*;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.COUNTER_MAX_CAPACITY;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.PRODUCTS_NAMES;

public class XVSMServiceRobotService implements IServiceRobotService {

	private static Logger logger = LoggerFactory.getLogger(XVSMServiceRobotService.class);
	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;
	private Capi capi;

	public XVSMServiceRobotService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		try {
			Query query = new Query().filter(Property.forName("*", "state").equalTo(OrderState.OPEN))
					.sortup(ComparableProperty.forName("*", "timestamp")).cnt(1);
			List<Order> test = capi.take(counterContainer,
					Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.DEFAULT_COUNT)),
					MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return test.get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public boolean addToCounter(List<Product> products, ITransaction tx) {
		try {
			List<Entry> entries = new ArrayList<>();
			products.forEach(product -> entries.add(new Entry(product)));
			capi.write(entries, counterContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return false;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return true;
		}

	}

	public List<Product> checkCounter(Order order, ITransaction tx) {
		List<Product> products = new ArrayList<>();
		Map<String, Order.Item> neededProducts = order.getItemsMap();
		Iterator it = neededProducts.entrySet().iterator();
		try {
			while(it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Order.Item item = (Order.Item) pair.getValue();
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(pair.getKey())).cnt((item.getAmount()));
				products.addAll(capi.read(counterContainer,Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL)), RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)));
			}

			return products;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void updateOrder(Order order, ITransaction tx) {
		try {
			Entry entry = new Entry(order);
			capi.write(entry, counterContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

	@Override
	public List<Product> getProductFromStorage(SortedMap<String, Integer> missingProducts, ITransaction tx) {
		Iterator it = missingProducts.entrySet().iterator();
		List<Product> productsForCounter = new ArrayList<>();
		try {
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(pair.getKey())).cnt((Integer) pair.getValue());
				productsForCounter.addAll(capi.take(counterContainer, Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL)), RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)));
			}
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

		return productsForCounter;
	}

	@Override
	public void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		try {
			Entry entry = new Entry(packedOrder);
			capi.write(entry, terminalContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}
	}

	@Override
	public SortedMap<String, Integer> getCounterStock(ITransaction tx) {
		SortedMap<String, Integer> missingProducts = new TreeMap<>();
		try {
			for (String name : PRODUCTS_NAMES) {
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(name));
				Integer available = capi.test(counterContainer, Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL)), RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
				if(available < COUNTER_MAX_CAPACITY) {
					missingProducts.put(name, COUNTER_MAX_CAPACITY-available);
				}
			}
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}
		return missingProducts;
	}



}
