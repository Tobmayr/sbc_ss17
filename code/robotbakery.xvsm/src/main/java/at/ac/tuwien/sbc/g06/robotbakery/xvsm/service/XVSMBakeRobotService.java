package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;
import org.mozartspaces.capi3.*;
import org.mozartspaces.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XVSMBakeRobotService implements IBakeRobotService {

	private static Logger logger = LoggerFactory.getLogger(XVSMKneadRobotService.class);
	private final ContainerReference storageContainer;
	private final ContainerReference bakeroomContainer;
	private Capi capi;
	private IRobotService robotService;

	public XVSMBakeRobotService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		bakeroomContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.BAKEROOM_CONTAINER_NAME);
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
		this.robotService = new XVSMRobotService(capi, BakeRobot.class.getSimpleName());

	}

	@Override
	public List<Product> getUnbakedProducts(ITransaction tx) {
		try {
			List<Product> products = new ArrayList<>();
			Product product = (Product) capi.take(bakeroomContainer, FifoCoordinator.newSelector(1),
					MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)).get(0);
			products.add(product);
			long t = System.currentTimeMillis();
			long end = t + SBCConstants.BAKE_WAIT;
			while (System.currentTimeMillis() < end) {
				try {
					Product nextProduct = (Product) capi.take(bakeroomContainer, FifoCoordinator.newSelector(1),
							MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)).get(0);
					if (nextProduct != null)
						products.add(nextProduct);
					if (products.size() == SBCConstants.BAKE_SIZE)
						break;
				} catch (MzsCoreException e) {
					// ignore
				}

			}

			return products;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean putBakedProductsInStorage(Product nextProduct, ITransaction tx) {
		try {
			Entry entry = new Entry(nextProduct);
			capi.write(entry, storageContainer, MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public void startRobot() {
		robotService.startRobot();

	}

	@Override
	public void shutdownRobot() {
		robotService.shutdownRobot();

	}
}
