package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryService implements IBakeryService {
	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryService.class);
	private final ContainerReference storageContainer;
	private Capi capi;


	public XVSMBakeryService(Capi capi) {
		this.capi=capi;
		storageContainer = XVSMUtil.getContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
	}

	@Override
	public void initializeStorageWaterPipe(WaterPipe waterPipe) {
		try {
			Entry entry = new Entry(waterPipe);
			capi.write(storageContainer, RequestTimeout.TRY_ONCE, null, entry);
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
		}

	}

}
