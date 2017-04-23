package at.tuwien.sbc.g06.robotbakery.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IUINotifier;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

public class BakeryUI implements IUINotifier {

	private List<Order> orders;

	public BakeryUI() {
		orders = new ArrayList<>();
	}

	public void start() {
		System.out.println("Welcome to the RobotBakery");

	}

	// Simple CLI at the
	public static void main(String[] args) {

		for (;;) {
			// wait and chill
		}

	}

	@Override
	public void onOrderAdded(Order order) {
		orders.add(order);
		update();

	}

	private void update() {
		orders.forEach(order -> System.out.println("Order:" + order.getId() + ", sum: " + order.totalSum()));

	}

	@Override
	public void onOrderStateChanged(Order order) {
		// TODO Auto-generated method stub

	}

}
