package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public interface INotificationService {

	/**
	 * sends notifications
	 * @param notification notification to send
	 * @param tx transaction
	 * @return true for success, else false
	 */
	boolean sendNotification(NotificationMessage notification, ITransaction tx);
}
