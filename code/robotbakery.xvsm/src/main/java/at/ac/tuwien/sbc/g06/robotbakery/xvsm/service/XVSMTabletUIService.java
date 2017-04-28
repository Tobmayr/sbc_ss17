package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.List;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

public class XVSMTabletUIService implements ITabletUIService {

	private static Logger logger = LoggerFactory.getLogger(XVSMBakeryUIService.class);
	private Capi capi;
	private ContainerReference counterContainer;
	private ContainerReference terminalContainer;

	public XVSMTabletUIService() {
		capi = new Capi(DefaultMzsCore.newInstance());
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		terminalContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.TERMINAL_CONTAINER_NAME);
	}

	@Override
	public boolean addOrderToCounter(Order order) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Product> readProductsInCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PackedOrder getAndPayOrderPackage() {
		// TODO Auto-generated method stub
		return null;
	}

}
