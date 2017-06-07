package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import static at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants.NotificationKeys.IS_BAKEROOM_EMPTY;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.ChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.CollectionsUtil;
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

				Map<Integer, List<Product>> baketimeMap = CollectionsUtil
						.sortMayByKeys(products.stream().collect(Collectors.groupingBy(Product::getBaketime)), true);

				// Baking
				int currentBakeTime = 0;
				for (List<Product> mappedProducts : baketimeMap.values()) {
					int bakeTime = mappedProducts.get(0).getBaketime() * 1000;
					int restBakeTime = bakeTime - currentBakeTime;
					sleepFor(restBakeTime);
					currentBakeTime += restBakeTime;
					System.out.println(
							String.format("Baked %s products for: %s ms", mappedProducts.size(), currentBakeTime));

					mappedProducts.forEach(p -> {
						p.addContribution(getId(), ContributionType.BAKE, getClass());
						p.setType(BakeState.FINALPRODUCT);
					});

					if (!service.putBakedProductsInStorage(mappedProducts, null)) {
						System.out.println("Error! Couldn't put products  in storage. Producst might be lost");

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
