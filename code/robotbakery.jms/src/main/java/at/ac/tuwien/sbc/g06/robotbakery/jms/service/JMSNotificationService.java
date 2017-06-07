package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import javax.jms.Session;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.INotificationService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSNotificationService extends AbstractJMSService implements INotificationService {

	public JMSNotificationService() {
		super(false, Session.AUTO_ACKNOWLEDGE, JMSConstants.SERVER_ADDRESS);

	}

	@Override
	public boolean sendNotification(NotificationMessage notification, ITransaction tx) {
		return notify(notification, false, JMSConstants.Queue.COUNTER.replace("queue://", ""));
	}

}
