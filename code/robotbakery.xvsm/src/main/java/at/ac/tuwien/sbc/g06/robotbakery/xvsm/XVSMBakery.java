package at.ac.tuwien.sbc.g06.robotbakery.xvsm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
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
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakery extends Bakery implements NotificationListener {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakery.class);

	public XVSMBakery(Capi server) {
		ContainerReference counterContainer = XVSMUtil.getOrCreateContainer(server,
				XVSMConstants.COUNTER_CONTAINER_NAME, new FifoCoordinator(), new TypeCoordinator());
		final List<Notification> notifications = new ArrayList<Notification>();
		NotificationManager manager = new NotificationManager(server.getCore());
		try {
			notifications.add(manager.createNotification(counterContainer, this, Operation.WRITE));
		} catch (MzsCoreException | InterruptedException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void entryOperationFinished(Notification notification, Operation operation,
			List<? extends Serializable> entries) {
		if (operation != Operation.WRITE) {
			return;
		}
		entries.forEach(ser -> {
			Entry entry = (Entry) ser;
			if (entry.getValue() instanceof Order) {
				Order order = (Order) entry.getValue();
				registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated(order));
			}
		});

	}

}
