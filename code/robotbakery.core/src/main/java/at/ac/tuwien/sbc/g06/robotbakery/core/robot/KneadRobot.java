package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;

public class KneadRobot extends Robot {

	private IKneadRobotService service;

	public KneadRobot(IKneadRobotService service) {
		this.service = service;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			ProductChooser productChooser = new ProductChooser(service);

			Product nextProduct = productChooser.getFinishableBaseDough();
			if (nextProduct != null)
				finishBaseDough(nextProduct);
			nextProduct = productChooser.getNextProductForCounter();
			if (nextProduct == null)
				nextProduct = productChooser.getNextProductForStorage();
			if (nextProduct != null)
				bakeNewProduct(nextProduct);
			nextProduct = productChooser.getNextBaseDoughForStorage();
			if (nextProduct != null)
				bakeNewBaseDough(nextProduct);
		}

	}

	private void bakeNewBaseDough(Product nextProduct) {
		// TODO Auto-generated method stub

	}

	private void bakeNewProduct(Product nextProduct) {
		// TODO Auto-generated method stub

	}

	private void finishBaseDough(Product nextProduct) {
		// TODO Auto-generated method stub

	}

}
