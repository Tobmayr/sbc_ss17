package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.Arrays;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMRobotService implements IRobotService {
	private static Logger logger = LoggerFactory.getLogger(XVSMRobotService.class);
	private ContainerReference storageContainer;
	private String message;
	private Capi capi;

	XVSMRobotService(Capi capi, String message) {
		this.capi = capi;
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
		this.message = message;
	}

	@Override
	public void startRobot() {
		try {
			Entry entry = new Entry(message);
			capi.write(entry, storageContainer, MzsConstants.RequestTimeout.TRY_ONCE, null);

		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());

		}

	}

	@Override
	public void shutdownRobot() {
		try {
			capi.take(storageContainer,
					Arrays.asList(TypeCoordinator.newSelector(String.class), FifoCoordinator.newSelector(1)),
					MzsConstants.RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());

		}

	}
}
