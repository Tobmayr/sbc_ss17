package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

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

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryUIChangeNotifer extends Bakery implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryUIChangeNotifer.class);
	private final ContainerReference counterContainer;

	private final ContainerReference storageContainer;
	private ContainerReference terminalContainer;
	private ContainerReference bakeroomContainer;
	private List<Notification> notifications;

	public XVSMBakeryUIChangeNotifer(Capi server) {
		counterContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.COUNTER_CONTAINER_NAME);
		storageContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.STORAGE_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.TERMINAL_CONTAINER_NAME);
		bakeroomContainer = XVSMUtil.getOrCreateContainer(server, XVSMConstants.BAKEROOM_CONTAINER_NAME);
		createNotifications(server);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				for (Notification notification : notifications) {
					notification.destroy();
				}
			} catch (MzsCoreException e) {
				// ignore
			}
		}));
	}

	private void createNotifications(Capi server) {
		notifications = new ArrayList<Notification>();
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
			Serializable object = XVSMUtil.unwrap(ser);

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

			else if (object instanceof String) {
				String msg = (String) object;
				notifiyListeners(msg, operation);
			}
		});

	}

	private void notifiyListeners(Ingredient ingredient, Operation operation) {
		registeredChangeListeners.forEach(ls -> {
			if (operation == Operation.WRITE) {
				if (ingredient instanceof FlourPack
						&& ((FlourPack) ingredient).getCurrentAmount() != SBCConstants.FLOUR_PACK_SIZE)
					return;
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
					ls.onProductAddedToCounter(product);
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
					ls.onProductRemovedFromBakeroom(product);
					break;
				default:
					break;
				}
			}
		});

	}

	private void notifiyListeners(String msg, Operation operation) {
		final Class<? extends Robot> clazz;
		if (msg.equals(ServiceRobot.class.getSimpleName())) {
			clazz = ServiceRobot.class;
		} else if (msg.equals(KneadRobot.class.getSimpleName())) {
			clazz = KneadRobot.class;
		} else if (msg.equals(BakeRobot.class.getSimpleName())) {
			clazz = BakeRobot.class;
		} else {
			clazz = null;
		}

		if (operation == Operation.WRITE)
			registeredChangeListeners.forEach(ls -> ls.onRobotStart(clazz));
		else
			registeredChangeListeners.forEach(ls -> ls.onRobotShutdown(clazz));

	}

	private void notifiyListeners(Order order) {
		registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated(order));

	}

	@Override
	public void initializeStorageWaterPipe(WaterPipe waterPipe, ITransaction tx) {
		// TODO Auto-generated method stub

	}

}
