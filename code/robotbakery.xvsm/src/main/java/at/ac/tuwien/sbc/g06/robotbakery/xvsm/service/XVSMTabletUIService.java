package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mozartspaces.capi3.Matchmakers;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMTabletUIService extends GenericXVSMService implements ITabletUIService {

	private ContainerReference counterContainer;
	private ContainerReference terminalContainer;

	public XVSMTabletUIService(Capi capi) {
		super(capi);
		counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);

	}

	@Override
	public boolean addOrderToCounter(Order order) {
		return write(order, counterContainer, null);
	}

	@Override
	public PackedOrder getPackedOrder(Order order) {
		Query query = new Query()
				.filter(Matchmakers.and(Property.forName("*", "customerID").equalTo(order.getCustomerId()),
						Property.forName("*", "orderID").equalTo(order.getId())))
				.cnt(1);
		return takeFirst(terminalContainer, null,
				QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));

	}

	@Override
	public boolean payOrder(PackedOrder order) {
		order.setState(OrderState.PAID);
		return write(order, counterContainer, null);
	}

	@Override
	public Map<String, Integer> getInitialCounterProducts() {

		Map<String, Integer> map = new HashMap<>();
		for (String productName : SBCConstants.PRODUCTS_NAMES) {
			Query query = new Query().filter(Property.forName("*", "productName").equalTo(productName));
			map.put(productName,
					test(counterContainer, null, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
							TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX)));
		}
		return map;

	}

	@Override
	public Prepackage getPrepackage(UUID packageId) {
		Query query = new Query().filter(Property.forName("*", "id").equalTo(packageId));
		return takeFirst(terminalContainer, null, QueryCoordinator.newSelector(query));
	}

	@Override
	public List<Prepackage> getInitialPrepackages() {
		Query query = new Query().filter(Property.forName("*", "state").equalTo(Prepackage.STATE_IN_TERMINAL));
		return read(terminalContainer, null, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
				TypeCoordinator.newSelector(Prepackage.class, MzsConstants.Selecting.COUNT_MAX));
	}

	@Override
	public URI getDeliveryURI() {
		return capi.getCore().getConfig().getSpaceUri();
	}

	@Override
	public boolean updatePrepackage(Prepackage prepackage) {
		return write(prepackage, terminalContainer, null);
	}

}
