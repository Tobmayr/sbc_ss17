package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryUIService extends AbstractXVSMService implements IBakeryUIService {

	private final ContainerReference storageContainer;

	public XVSMBakeryUIService(Capi capi) {
		super(capi);
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
	}

	@Override
	public boolean addIngredientsToStorage(List<Ingredient> ingredients) {
		return write(ingredients, storageContainer, null);
	}

}
