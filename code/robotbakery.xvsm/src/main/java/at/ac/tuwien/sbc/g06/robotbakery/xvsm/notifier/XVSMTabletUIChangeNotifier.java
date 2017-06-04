package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMTabletUIChangeNotifier extends TabletUIChangeNotifer implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakery.class);

	private final ContainerReference counterContainer;
	private final ContainerReference terminalContainer;

	private ArrayList<Notification> notifications;

	public XVSMTabletUIChangeNotifier() {
		super();
		Capi capi = new Capi(DefaultMzsCore.newInstance());
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME, null);
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME, null);
		createNotifications(capi);

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
			notifications.add(manager.createNotification(terminalContainer, this, Operation.WRITE));
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
