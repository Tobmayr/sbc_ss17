package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.INotificationService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMNotificationService extends GenericXVSMService implements INotificationService {

	private ContainerReference counterContainer;

	public XVSMNotificationService(Capi capi) {
		super(capi);
		this.counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);

	}

	@Override
	public boolean sendNotification(NotificationMessage notification, ITransaction tx) {
		return write(notification, counterContainer, tx);
	}

}
