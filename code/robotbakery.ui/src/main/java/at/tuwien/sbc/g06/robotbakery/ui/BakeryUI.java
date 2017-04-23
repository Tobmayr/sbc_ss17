package at.tuwien.sbc.g06.robotbakery.ui;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

public class BakeryUI implements IBakeryChangeListener {

	private Map<UUID, Order> orders;

	public BakeryUI() {

		orders = new TreeMap<>();
		System.out.println("Welcome to the RobotBakery");
	}

	private void update() {
		System.out.println("---------------------------------------------------------------------------");
		orders.forEach((id, order) -> System.out.println("Order:" + id + ", sum: " + order.totalSum()));
		System.out.println("---------------------------------------------------------------------------");
		System.out.println();

	}

	@Override
	public void onOrderAddedOrUpdated(Order order) {
		orders.put(order.getId(), order);
		update();

	}

}
