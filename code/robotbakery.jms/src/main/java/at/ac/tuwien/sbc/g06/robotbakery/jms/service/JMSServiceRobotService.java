package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

public class JMSServiceRobotService implements IServiceRobotService {

	@Override
	public Order getNextOrder(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addToCounter(List<Product> products, ITransaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Product checkCounter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateOrder(Order order, ITransaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx) {
		// TODO Auto-generated method stub
		
	}


}
