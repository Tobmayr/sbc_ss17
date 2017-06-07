package at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.GenericXVSMService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

public class XVSMBakery extends Bakery {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakery.class);;

	private final ContainerReference storageContainer;

	private GenericXVSMService service;

	public XVSMBakery(Capi server) {
		super(new XVSMBakeryChangeNotifer(server));
		service = new GenericXVSMService(server);
		storageContainer = service.getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);

	}

	@Override
	public void init() {
		service.write(new WaterPipe(), storageContainer, null);

	}

}
