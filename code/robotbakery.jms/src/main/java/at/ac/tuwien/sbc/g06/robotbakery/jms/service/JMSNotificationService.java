package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.INotificationService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public class JMSNotificationService implements INotificationService {

	@Override
	public boolean sendNotification(NotificationMessage notification, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

}
