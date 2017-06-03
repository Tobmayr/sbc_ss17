package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.io.Serializable;
import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMBakeryUIService extends GenericXVSMService implements IBakeryUIService {

	private final ContainerReference storageContainer;
	private ContainerReference counterContainer;
	private ContainerReference bakeroomContainer;

	public XVSMBakeryUIService(Capi capi) {
		super(capi);
		storageContainer = getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
		counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		bakeroomContainer = getContainer(XVSMConstants.BAKEROOM_CONTAINER_NAME);
	}

	@Override
	public boolean addIngredientsToStorage(List<Ingredient> ingredients) {
		return write(ingredients, storageContainer, null);
	}

	@Override
	public void addItemsToStorage(List<Serializable> items) {
		write(items, storageContainer, null);

	}

	@Override
	public void addProductsToCounter(List<Product> products) {
		write(products, counterContainer, null);

	}

	@Override
	public void addProductsToBakeroom(List<Product> forBakeroom) {
		write(forBakeroom, bakeroomContainer, null);

	}

}
