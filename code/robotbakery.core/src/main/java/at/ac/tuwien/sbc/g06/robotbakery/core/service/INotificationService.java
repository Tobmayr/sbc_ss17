package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface INotificationService {
	
	boolean sendNotification(NotificationMessage notification, ITransaction tx);
}
