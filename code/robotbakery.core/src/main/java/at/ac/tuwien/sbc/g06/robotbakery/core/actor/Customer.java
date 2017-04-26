package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ICustomerService;

public class Customer extends Actor {


	private ICustomerService counterService;
	

	public Customer(ICustomerService customerService) {
		this.counterService = customerService;
	}

	@Override
	public void run() {
		Order order = new Order();
		order.addItem("Kaisersemmel", 2);
		order.setCustomerId(getId());
		counterService.addOrderToCounter(order);

	}


}
