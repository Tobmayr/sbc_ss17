package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.GenericXVSMService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryChangeNotifer extends ChangeNotifer implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryChangeNotifer.class);
	private GenericXVSMService service;
	private ContainerReference counterContainer;
	private ContainerReference storageContainer;
	private ContainerReference terminalContainer;
	private ContainerReference bakeroomContainer;
	private ArrayList<Notification> notifications;

	public XVSMBakeryChangeNotifer(Capi server) {
		service = new GenericXVSMService(server);
		counterContainer = service.getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		storageContainer = service.getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
		terminalContainer = service.getContainer(XVSMConstants.TERMINAL_CONTAINER_NAME);
		bakeroomContainer = service.getContainer(XVSMConstants.BAKEROOM_CONTAINER_NAME);
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
		entries.forEach(ser -> {
			Serializable object = XVSMUtil.unwrap(ser);
			String coordinationRoom = XVSMUtil.getCoordinationRoom(notification.getObservedContainer());
			registeredChangeListeners
					.forEach(ls -> ls.onObjectChanged(object, coordinationRoom, operation == Operation.WRITE));

		});

	}

}
