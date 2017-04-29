package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;

public class ServiceRobot extends Robot {

	private IServiceRobotService service;

	public ServiceRobot(IServiceRobotService service) {
		super();
		this.service = service;
	};

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			Order order = service.processNextOrder();
			if (order != null) {
				System.out.println("New order with id: " + order.getId() + " received");
				service.addToCounter(Arrays.asList(new Product("Bauernbrot"), new Product("Bauernbrot"),
						new Product("Kaisersemmel")));
			} else {
				//Checks Counter and gets list of products that are missing from counter
				Product missingProducts = service.checkCounter();

				//TODO gets missing product and deliver it to counter


			}
		}

	}

	private void packOrder(Order order) {
		// TODO
	}

}
