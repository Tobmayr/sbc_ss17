package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

import java.util.List;

public class BakeRobot extends Robot {

	private IBakeRobotService service;

	public BakeRobot(IBakeRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));
	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			doTask(bakeProducts);

		}

	}

	ITransactionalTask bakeProducts = tx -> {
		List<Product> products = service.getUnbakedProducts(null);
		if (products == null || products.isEmpty())
			return false;
		sleepFor(5000);
		for (Product product : products) {
			product.addContribution(getId(), ContributionType.BAKE, getClass());
			product.setType(BakeState.FINALPRODUCT);
			if (!service.putBakedProductsInStorage(product, tx)) {
				System.out.println("Could not put Product with id " + product.getId() + " in storage!");
				return false;
			}
		}

		return true;
	};

}
