package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ICustomerService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMCustomerService implements ICustomerService {

	private static Logger logger = LoggerFactory.getLogger(XVSMCustomerService.class);
	private final ContainerReference counterContainer;
	private Capi capi;

	public XVSMCustomerService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
	}

	@Override
	public void addOrderToCounter(Order order) {
		try {
			Entry entry = new Entry(order);
			capi.write(counterContainer, RequestTimeout.TRY_ONCE, null, entry);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

	@Override
	public Message takeMessageFromTerminal() {
		try {
			return (Message) capi.take(counterContainer, TypeCoordinator.newSelector(Message.class),
					XVSMConstants.TIMEOUT_MILLIS, null).get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

}
