package at.ac.tuwien.sbc.g06.robotbakery.xvsm.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XVSMUtil {
	private static Logger logger = LoggerFactory.getLogger(XVSMUtil.class);

	private XVSMUtil() {
	};

	public static ContainerReference getOrCreateContainer(Capi capi, String containerName) {
		logger.debug("Lookup container:" + containerName);
		ContainerReference cref = null;
		try {
			cref = capi.lookupContainer(containerName, XVSMConstants.BASE_SPACE_URI, RequestTimeout.DEFAULT, null);
			logger.debug("Existing container found for: " + containerName);
		} catch (MzsCoreException e) {
			try {
				cref = capi.createContainer(containerName, XVSMConstants.BASE_SPACE_URI,
						MzsConstants.Container.UNBOUNDED, getObligatoryCoordsForContainer(containerName), null, null);
				logger.debug("New container has been created for: " + containerName);
			} catch (MzsCoreException e1) {
				logger.error(e1.getMessage());
			}
		}
		return cref;

	}

	private static List<Coordinator> getObligatoryCoordsForContainer(String containerName) {
		switch (containerName) {
		case XVSMConstants.COUNTER_CONTAINER_NAME:
			return Arrays.asList(new FifoCoordinator(), new TypeCoordinator());
		case XVSMConstants.STORAGE_CONTAINER_NAME:
			return Arrays.asList(new QueryCoordinator());
		case XVSMConstants.BAKEROOM_CONTAINER_NAME:
			return Arrays.asList(new FifoCoordinator());
		case XVSMConstants.TERMINAL_CONTAINER_NAME:
			return Arrays.asList(new QueryCoordinator());

		default:
			return null;
		}
	}
}
