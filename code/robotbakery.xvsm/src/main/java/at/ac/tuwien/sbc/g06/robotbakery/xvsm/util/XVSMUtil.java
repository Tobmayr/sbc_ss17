package at.ac.tuwien.sbc.g06.robotbakery.xvsm.util;

import java.util.Arrays;

import org.mozartspaces.capi3.Coordinator;
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

	public static ContainerReference createContainer(Capi capi, String containerName, Coordinator... obligatoryCoords) {

		ContainerReference cref = null;
		;
		try {
			cref = capi.createContainer(containerName, XVSMConstants.BASE_SPACE_URI, MzsConstants.Container.UNBOUNDED,
					Arrays.asList(obligatoryCoords), null, null);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return cref;
	}

	public static ContainerReference getContainer(Capi capi, String containerName) {
		ContainerReference cref = null;
		try {
			cref = capi.lookupContainer(containerName, XVSMConstants.BASE_SPACE_URI, RequestTimeout.DEFAULT, null);
			logger.debug("Existing container found for: " + containerName);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
		}
		return cref;
	}

}
