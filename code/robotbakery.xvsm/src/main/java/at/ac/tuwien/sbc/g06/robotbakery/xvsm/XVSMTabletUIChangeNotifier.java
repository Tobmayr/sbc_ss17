package at.ac.tuwien.sbc.g06.robotbakery.xvsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMTabletUIChangeNotifier extends TabletUIChangeNotifer implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryUIChangeNotifer.class);

	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;

	public XVSMTabletUIChangeNotifier() {
		super();
		Capi capi = new Capi(DefaultMzsCore.newInstance());
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		createNotifications(capi);
	}

	private void createNotifications(Capi server) {
		final List<Notification> notifications = new ArrayList<Notification>();
		NotificationManager manager = new NotificationManager(server.getCore());
		try {
			notifications.add(manager.createNotification(counterContainer, this, Operation.WRITE, Operation.TAKE));
			notifications.add(manager.createNotification(terminalContainer, this, Operation.WRITE));
		} catch (MzsCoreException | InterruptedException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}

	}

	@Override
	public void entryOperationFinished(Notification notification, Operation operation,
			List<? extends Serializable> entries) {
		if (operation != Operation.WRITE && operation != Operation.TAKE) {
			return;
		}
		entries.forEach(ser -> {
			Serializable object;
			if (ser instanceof Entry) {
				object = ((Entry) ser).getValue();
			} else {
				object = ser;
			}

			if (object instanceof Product) {
				Product product = (Product) object;
				notifiyListeners(product, operation);

			}

			else if (object instanceof Order) {
				if (operation != Operation.WRITE)
					return;
				Order order = (Order) object;
				notifiyListeners(order);
			}

		});

	}

	private void notifiyListeners(Order order) {
		if (order.getCustomerId().equals(getCustomerID())) {
			registeredChangeListeners.forEach(ls -> ls.onOrderUpdated(order));
		}

	}

	private void notifiyListeners(Product product, Operation operation) {
		if (operation == Operation.WRITE) {
			registeredChangeListeners.forEach(ls -> ls.onProductsAddedToCounter(product));
		} else if (operation == Operation.TAKE) {
			registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromCounter(product));
		}

	}

}
