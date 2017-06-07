package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_BAKEROOM_EMPTY;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class BakeRobot extends Robot {

	private IBakeRobotService service;

	public BakeRobot(IBakeRobotService service, ChangeNotifer changeNotifer, ITransactionManager transactionManager,
			String id) {
		super(transactionManager, changeNotifer, id);
		this.service = service;
		notificationState = service.getIntialState();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));
	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			if (!notificationState.get(IS_BAKEROOM_EMPTY)) {
				List<Product> products = service.getUnbakedProducts(null);
				if (products == null || products.isEmpty()) {
					continue;
				}

				System.out.println(
						String.format("New charge of size: %s received. Init baking process", products.size()));
				Collections.sort(products, (a, b) -> a.getRecipe().getBakeTime() < b.getRecipe().getBakeTime() ? -1
						: a.getRecipe().getBakeTime() == b.getRecipe().getBakeTime() ? 0 : 1);

				// Baking
				int currentBakeTime = 0;
				for (Product product : products) {
					int bakeTime = product.getRecipe().getBakeTime() * 1000;
					if (bakeTime > currentBakeTime) {
						sleepFor(bakeTime);
						currentBakeTime = bakeTime;
						System.out.println(String.format("Baked for: %s", bakeTime));
					}
					product.addContribution(getId(), ContributionType.BAKE, getClass());
					product.setType(BakeState.FINALPRODUCT);
					if (!service.putBakedProductsInStorage(product, null)) {
						System.out.println(String.format(
								"Error! Couldn't put product with id \"%s\" in storage. Product might be lost",
								product.getId()));
					}
				}
			}

		}

	}

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && object instanceof Product && coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_BAKEROOM)) {
			notificationState.put(IS_BAKEROOM_EMPTY, false);
		} else if (object instanceof NotificationMessage
				&& ((NotificationMessage) object).getMessageTyp() == NotificationMessage.NO_MORE_PRODUCTS_IN_BAKEROOM) {
			notificationState.put(IS_BAKEROOM_EMPTY, true);
		}

	}

}
