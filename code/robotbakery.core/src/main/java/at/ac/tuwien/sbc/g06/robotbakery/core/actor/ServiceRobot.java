package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ICounterService;

public class ServiceRobot extends Actor {

	private ICounterService counterService;

	public ServiceRobot(ICounterService counterService) {
		super();
		this.counterService = counterService;
	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Order order = counterService.takeNextOrder();
			if (order != null) {
				System.out.println("New order with id: " + order.getId() + " received");
			}
		}

	}

}
