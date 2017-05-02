package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public class JMSBakeRobotService implements IBakeRobotService {

	@Override
	public List<Product> getUnbakedProducts(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putBakedProductsInStorage(Product nextProduct, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRobot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdownRobot() {
		// TODO Auto-generated method stub
		
	}

}
