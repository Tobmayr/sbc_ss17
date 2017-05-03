package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.COUNTER_MAX_CAPACITY;
import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.PRODUCTS_NAMES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Matchmaker;
import org.mozartspaces.capi3.Matchmakers;
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
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMServiceRobotService implements IServiceRobotService {

	private static Logger logger = LoggerFactory.getLogger(XVSMServiceRobotService.class);
	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;
	private final ContainerReference storageContainer;
	private Capi capi;
	private XVSMRobotService robotService;

	public XVSMServiceRobotService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
		this.robotService = new XVSMRobotService(capi, ServiceRobot.class.getSimpleName());

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
	public boolean addToCounter(Product product, ITransaction tx) {
		try {
			Query query = new Query().filter(Property.forName("*", "productName").equalTo(product.getProductName()));
			Integer available = capi.test(counterContainer,
					Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX)),
					RequestTimeout.TRY_ONCE, null);
			if(available < SBCConstants.COUNTER_MAX_CAPACITY) capi.write(new Entry(product), counterContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}

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

		try {
			Matchmaker product = Property.forName("*", "productName").equalTo(productName);
			Matchmaker type = Property.forName("*", "type").equalTo(BakeState.FINALPRODUCT);
			Query query = new Query().filter(Matchmakers.and(product, type)).cnt(0,amount);
			return capi.take(containerReference,
					Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX)),
					RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));

		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

		return null;
	}

	@Override
	public boolean updateOrder(Order order, ITransaction tx) {
		try {
			Entry entry = new Entry(order);
			capi.write(entry, counterContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;

		}

	}

	@Override
	public boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		try {
			Entry entry = new Entry(packedOrder);
			capi.write(entry, terminalContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public Map<String, Integer> getCounterStock() {
		Map<String, Integer> missingProducts = new HashMap<>();
		try {
			for (String name : PRODUCTS_NAMES) {
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(name));
				Integer available = capi.test(counterContainer,
						Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX)),
						RequestTimeout.TRY_ONCE, null);
				if (available < COUNTER_MAX_CAPACITY) {
					missingProducts.put(name, COUNTER_MAX_CAPACITY - available);
				}
			}
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}
		return missingProducts;
	}

	@Override
	public void startRobot() {
		robotService.startRobot();
	}

	@Override
	public void shutdownRobot() {
		robotService.shutdownRobot();

	}

}
