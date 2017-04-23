package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.Arrays;

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

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ICounterService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMCounterService implements ICounterService {

	private static Logger logger = LoggerFactory.getLogger(XVSMCounterService.class);
	private final ContainerReference counterContainer;
	private Capi capi;

	public XVSMCounterService() {
		this.capi= new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi,XVSMConstants.COUNTER_CONTAINER_NAME, new FifoCoordinator(),
				new TypeCoordinator());
	}

	@Override
	public void addOrder(Order order) {
		try {
			Entry entry = new Entry(order);
			capi.write(counterContainer, RequestTimeout.TRY_ONCE, null, entry);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

	@Override
	public Order takeNextOrder() {
		try {
			return (Order) capi.take(counterContainer,
					Arrays.asList(FifoCoordinator.newSelector(1), TypeCoordinator.newSelector(Order.class)),
					XVSMConstants.TIMEOUT_MILLIS, null).get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return null;

	}

}
