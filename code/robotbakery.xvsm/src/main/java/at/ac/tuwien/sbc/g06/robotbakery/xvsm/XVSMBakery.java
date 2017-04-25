package at.ac.tuwien.sbc.g06.robotbakery.xvsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;
import javafx.stage.Stage;

public class XVSMBakery extends Bakery implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakery.class);
	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;
	private final ContainerReference storageContainer;
	private final ContainerReference bakeroomContainer;

	public XVSMBakery(Capi server) {
		super(new XVSMBakeryService(server));

		counterContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.TERMINAL_CONTAINER_NAME);
		storageContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.STORAGE_CONTAINER_NAME);
		bakeroomContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.BAKEROOM_CONTAINER_NAME);
		createNotifications(server);
	}

	private void createNotifications(Capi server) {
		final List<Notification> notifications = new ArrayList<Notification>();
		NotificationManager manager = new NotificationManager(server.getCore());
		try {
			notifications.add(manager.createNotification(counterContainer, this, Operation.WRITE, Operation.TAKE));
			notifications.add(manager.createNotification(storageContainer, this, Operation.WRITE, Operation.TAKE));
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

			if (object instanceof Order) {
				if (operation != Operation.WRITE)
					return;
				Order order = (Order) object;
				notifiyListeners(order);

			}

			else if (object instanceof Product) {
				Product product = (Product) object;
				String containerName = XVSMUtil.getNameForContainer(notification.getObservedContainer());
				notifiyListeners(product, containerName, operation);

			}

			else if (object instanceof Ingredient) {
				Ingredient ingredient = (Ingredient) object;
				notifiyListeners(ingredient, operation);
			}
		});

	}

	private void notifiyListeners(Ingredient ingredient, Operation operation) {
		registeredChangeListeners.forEach(ls -> {
			if (operation == Operation.WRITE) {
				ls.onIngredientAddedToStorage(ingredient);
			} else if (operation == Operation.TAKE) {
				ls.onIngredientRemovedFromStorage(ingredient);
			}
		});

	}

	private void notifiyListeners(Product product, String containerName, Operation operation) {
		registeredChangeListeners.forEach(ls -> {
			if (operation == Operation.WRITE) {
				if (containerName.equals(XVSMConstants.STORAGE_CONTAINER_NAME)) {
					ls.onProductAddedToStorage(product);
				} else if (containerName.equals(XVSMConstants.COUNTER_CONTAINER_NAME)) {
					ls.onProductsAddedToCounter(product);
				}

			} else if (operation == Operation.TAKE) {
				if (containerName.equals(XVSMConstants.STORAGE_CONTAINER_NAME)) {
					ls.onProductRemovedFromStorage(product);
				} else if (containerName.equals(XVSMConstants.COUNTER_CONTAINER_NAME)) {
					ls.onProductRemovedFromStorage(product);
				}
			}
		});

	}

	private void notifiyListeners(Order order) {
		registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated(order));

	}

}
