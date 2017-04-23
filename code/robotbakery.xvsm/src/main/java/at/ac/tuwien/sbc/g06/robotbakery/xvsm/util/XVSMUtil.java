package at.ac.tuwien.sbc.g06.robotbakery.xvsm.util;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class XVSMUtil {
	private static Logger logger = Logger.getLogger(XVSMUtil.class);

	public static ContainerReference getOrCreateContainer(String containerName, Capi capi,
			List<Coordinator> obligatoryCoords, String host, int port) {
		logger.debug("Lookup container:" + containerName);
		ContainerReference cref = null;
		try {
			cref = capi.lookupContainer(containerName, XVSMConstants.BASE_SPACE_URI, RequestTimeout.DEFAULT, null);
			logger.debug("Existing container found for: " + containerName);
		} catch (MzsCoreException e) {
			try {
				cref = capi.createContainer(containerName, XVSMConstants.BASE_SPACE_URI,
						MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
				logger.debug("New container has been created for: " + containerName);
			} catch (MzsCoreException e1) {
				logger.error(e1.getMessage());
			}
		}

		return cref;
	}
}
