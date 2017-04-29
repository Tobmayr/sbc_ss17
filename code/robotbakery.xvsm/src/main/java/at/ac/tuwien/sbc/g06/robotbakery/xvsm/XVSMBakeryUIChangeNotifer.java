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

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.BakeryUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryUIChangeNotifer extends BakeryUIChangeNotifier implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryUIChangeNotifer.class);
	private final ContainerReference counterContainer;

	private final ContainerReference storageContainer;
	private ContainerReference terminalContainer;
	private ContainerReference bakeroomContainer;

	public XVSMBakeryUIChangeNotifer(Capi server) {
		counterContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.COUNTER_CONTAINER_NAME);
		storageContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.STORAGE_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.TERMINAL_CONTAINER_NAME);
		bakeroomContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.BAKEROOM_CONTAINER_NAME);
		createNotifications(server);
	}

	private void createNotifications(Capi server) {
		final List<Notification> notifications = new ArrayList<Notification>();
		NotificationManager manager = new NotificationManager(server.getCore());
		try {
			notifications.add(manager.createNotification(counterContainer, this, Operation.WRITE, Operation.TAKE));
			notifications.add(manager.createNotification(storageContainer, this, Operation.WRITE, Operation.TAKE));
			notifications.add(manager.createNotification(terminalContainer, this, Operation.WRITE, Operation.TAKE));
			notifications.add(manager.createNotification(bakeroomContainer, this, Operation.WRITE, Operation.TAKE));
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
				switch (containerName) {
				case XVSMConstants.STORAGE_CONTAINER_NAME:
					ls.onProductAddedToStorage(product);
					break;
				case XVSMConstants.COUNTER_CONTAINER_NAME:
					ls.onProductsAddedToCounter(product);
					break;
				case XVSMConstants.TERMINAL_CONTAINER_NAME:
					ls.onProductAddedToTerminal(product);
					break;
				case XVSMConstants.BAKEROOM_CONTAINER_NAME:
					ls.onProductAddedToBakeroom(product);
					break;
				default:
					break;
				}

			} else if (operation == Operation.TAKE) {
				switch (containerName) {
				case XVSMConstants.STORAGE_CONTAINER_NAME:
					ls.onProductRemovedFromStorage(product);
					break;
				case XVSMConstants.COUNTER_CONTAINER_NAME:
					ls.onProductRemovedFromCounter(product);
					break;
				case XVSMConstants.TERMINAL_CONTAINER_NAME:
					ls.onProductRemovedFromTerminal(product);
					break;
				case XVSMConstants.BAKEROOM_CONTAINER_NAME:
					ls.onProductAddedToBakeroom(product);
					break;
				default:
					break;
				}
			}
		});

	}

	private void notifiyListeners(Order order) {
		registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated(order));

	}

}
