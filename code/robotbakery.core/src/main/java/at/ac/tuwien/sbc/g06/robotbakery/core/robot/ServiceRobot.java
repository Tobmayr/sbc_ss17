package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
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
			Order order = service.getNextOrder();
			if (order != null) {
				System.out.println("New order with id: " + order.getId() + " received");
				if (order.getItemsMap().keySet().size()>2){
					System.out.print("Declining");
					order.setState(OrderState.UNDELIVERABLE);
					service.updateOrder(order);
				}else{
					System.out.println("Packing");
					PackedOrder packedOrder = packOrder(order);
					service.putPackedOrderInTerminal(packedOrder);
					order.setState(OrderState.DELIVERED);
					order.setServiceRobotId(getId());
					service.updateOrder(order);
				}

				
				
			

			} else {

				// Checks Counter and gets list of products that are missing
				// from counter
				Product missingProducts = service.checkCounter();

				// TODO gets missing product and deliver it to counter

			}
		}

	}

	private PackedOrder packOrder(Order order) {
		// TODO
		return new PackedOrder(order.getCustomerId(), order.getId());
	}

}
