package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMRobotService extends AbstractXVSMService implements IRobotService {

	private ContainerReference storageContainer;
	private String message;


	XVSMRobotService(Capi capi, String message) {
		super(capi);
		storageContainer = getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
		this.message = message;
	}

	@Override
	public void startRobot() {
		write(message, storageContainer, null);
	}

	@Override
	public void shutdownRobot() {
		take(storageContainer, null, TypeCoordinator.newSelector(String.class), FifoCoordinator.newSelector(1));

	}
}
