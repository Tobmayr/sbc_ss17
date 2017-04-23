package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMUIService implements IUIService {
	private static Logger logger = LoggerFactory.getLogger(XVSMUIService.class);
	private final ContainerReference storageContainer;
	private Capi capi;

	public XVSMUIService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		storageContainer = XVSMUtil.getContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
	}

	@Override
	public void addIngredientsToStorage(Ingredient ingredient, int amount) {
		// TODO Auto-generated method stub

	}

}
