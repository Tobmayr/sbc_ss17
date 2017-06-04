package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthias Höllthaler on 01.06.2017.
 */
public class XVSMDeliveryTabletUIService extends GenericXVSMService implements IDeliveryTabletUIService {

    private ContainerReference counterContainer;
    private ContainerReference terminalContainer;

    public XVSMDeliveryTabletUIService() {
        super(new Capi(DefaultMzsCore.newInstance()));
        counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
        terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.DELIVERY_CONTAINER_NAME, null);
    }

    @Override
    public boolean addOrderToCounter(Order order) {
        return write(order, counterContainer, null);
    }

    @Override
    public PackedOrder getOrderPackage(Order order) {
        Query query = new Query()
                .filter(Matchmakers.and(Property.forName("*", "customerID").equalTo(order.getCustomerId()),
                        Property.forName("*", "orderID").equalTo(order.getId())))
                .cnt(1);
        return takeFirst(terminalContainer, null,
                QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_ALL));

    }

    @Override
    public boolean payOrder(PackedOrder order) {
        order.setState(Order.OrderState.PAID);
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

}
