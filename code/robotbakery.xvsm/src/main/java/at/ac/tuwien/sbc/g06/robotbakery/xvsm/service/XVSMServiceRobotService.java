package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

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
	public Order getNextOrder() {
		try {
			Query query = new Query().filter(Property.forName("*", "state").equalTo(OrderState.OPEN))
					.sortup(ComparableProperty.forName("*", "timestamp")).cnt(1);
			List<Order> test = capi.take(counterContainer,
					Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL)),
					MzsConstants.RequestTimeout.DEFAULT, null);
			return test.get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public void addToCounter(List<Product> products) {
		try {
			List<Entry> entries = new ArrayList<>();
			products.forEach(product -> entries.add(new Entry(product)));
			capi.write(entries, counterContainer, RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

	@Override
	public Product checkCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateOrder(Order order) {
		try {
			Entry entry = new Entry(order);
			capi.write(entry, counterContainer, RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

	@Override
	public void putPackedOrderInTerminal(PackedOrder packedOrder) {
		try {
			Entry entry = new Entry(packedOrder);
			capi.write(entry, terminalContainer, RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}
	}

}
