package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.COUNTER_MAX_CAPACITY;
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
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMServiceRobotService extends AbstractXVSMService implements IServiceRobotService {

	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;
	private final ContainerReference storageContainer;
	private XVSMRobotService robotService;

	public XVSMServiceRobotService() {
		super(new Capi(DefaultMzsCore.newInstance()));
		counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);
		storageContainer = getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
		this.robotService = new XVSMRobotService(capi, ServiceRobot.class.getSimpleName());

	}

	@Override
	public Order getNextOrder(ITransaction tx) {
		Query query = new Query().filter(Property.forName("*", "state").equalTo(OrderState.OPEN))
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
	public void startRobot() {
		robotService.startRobot();
	}

	@Override
	public void shutdownRobot() {
		robotService.shutdownRobot();

	}

	@Override
	public boolean returnDeliveryOrder(DeliveryOrder currentOrder, ITransaction tx) {
		return write(currentOrder, counterContainer, tx);
	}

}
