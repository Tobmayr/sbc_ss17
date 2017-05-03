package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mozartspaces.capi3.Matchmaker;
import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMTabletUIService implements ITabletUIService {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryUIService.class);
	private Capi capi;
	private ContainerReference counterContainer;
	private ContainerReference terminalContainer;
	private UUID customerID;
	private UUID orderID;

	public XVSMTabletUIService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		try {
			Entry entry = new Entry(order);
			capi.write(counterContainer, RequestTimeout.TRY_ONCE, null, entry);
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}

	}

	@Override
	public PackedOrder getOrderPackage() {
		try {
			return (PackedOrder) capi
					.take(terminalContainer,
							Arrays.asList(QueryCoordinator.newSelector(matchingOrderQuery(),
									MzsConstants.Selecting.COUNT_ALL)),
							MzsConstants.RequestTimeout.DEFAULT, null)
					.get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public boolean payOrder(Order order) {
		try {
			order.setState(OrderState.PAID);
			capi.write(counterContainer, RequestTimeout.TRY_ONCE, null, new Entry(order));
			return true;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return false;
		}

	}

	private Query matchingOrderQuery() {
		return new Query().filter(Matchmakers.and(Property.forName("*", "customerID").equalTo(customerID),
				Property.forName("*", "orderID").equalTo(orderID))).cnt(1);
	}

	@Override
	public void initialize(UUID customerID, UUID orderID) {
		capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
		this.customerID = customerID;
		this.orderID = orderID;

	}

	@Override
	public Map<String, Integer> getInitialCounterProducts() {
		try {
			Map<String, Integer> map = new HashMap<>();
			for (String productName : SBCConstants.PRODUCTS_NAMES) {
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(productName));
				map.put(productName,
						capi.test(counterContainer,
								Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
										TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX)),
								MzsConstants.RequestTimeout.TRY_ONCE, null));
			}
			return map;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

}
