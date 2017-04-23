package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.Arrays;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMServiceRobotService implements IServiceRobotService {

	private static Logger logger = LoggerFactory.getLogger(XVSMServiceRobotService.class);
	private final ContainerReference counterContainer;
	private Capi capi;

	public XVSMServiceRobotService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
	}

	@Override
	public Order processNextOrder() {
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
