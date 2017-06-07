package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;

public interface INotificationService {
	
	boolean sendNotification(NotificationMessage notification);
}
