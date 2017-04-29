package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

public class KneadRobot extends Robot {

	private IKneadRobotService service;

	private Product nextProduct;

	public KneadRobot(IKneadRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			doTask(bakeNextProduct);
		}

	}

	ITransactionalTask bakeNewBaseDough = tx -> {
		// TODO IMPLEMENT

		return true;
	};

	ITransactionalTask finishBaseDough = tx -> {
		// TODO IMPLEMENT

		return true;
	};

	ITransactionalTask putProductInStorage = tx -> {
		// TODO IMPLEMENT

		return true;
	};

	ITransactionalTask bakeNextProduct = tx -> {
		ProductChooser productChooser = new ProductChooser(service, tx);
		if (!productChooser.correctlyInitialized())
			return false;

		nextProduct = productChooser.getFinishableBaseDough();
		if (nextProduct != null) {
			if (doTask(finishBaseDough))
				return doTask(putProductInStorage);
			return true;
		}

		nextProduct = productChooser.getNextProduct();
		if (nextProduct != null) {
			if (doTask(bakeNewBaseDough)) {
				doTask(finishBaseDough);
				return doTask(putProductInStorage);

			}
			return true;
		}

		nextProduct = productChooser.getNextBaseDoughForStorage();
		if (nextProduct != null)
			if (doTask(bakeNewBaseDough)) {
				return doTask(putProductInStorage);
			}
		return true;

	};

}
