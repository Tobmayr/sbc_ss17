package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;
import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class BakeRobot extends Robot {

	private IBakeRobotService service;
	private boolean isBakeroomEmpty = false;

	public BakeRobot(IBakeRobotService service, ITransactionManager transactionManager, String id) {
		super(transactionManager, id);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));
	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			if (!isBakeroomEmpty) {
				List<Product> products = service.getUnbakedProducts(null);
				if (products == null || products.isEmpty()) {
					isBakeroomEmpty = true;
					continue;
				}

				System.out.println(
						String.format("New charge of size: %s received. Init baking process", products.size()));
				// Baking
				sleepFor(5000);
				for (Product product : products) {
					product.addContribution(getId(), ContributionType.BAKE, getClass());
					product.setType(BakeState.FINALPRODUCT);
					if (!service.putBakedProductsInStorage(product, null)) {
						System.out.println(String.format(
								"Error! Couldn't put product wiht id \"%s\" in storage. Product might be lost",
								product.getId()));
					}
				}
			}

		}

	}

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && object instanceof Product && coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_BAKEROOM)) {
			isBakeroomEmpty = false;
		}

	}

}
