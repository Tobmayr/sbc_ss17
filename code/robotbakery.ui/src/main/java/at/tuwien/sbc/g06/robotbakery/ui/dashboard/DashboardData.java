package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardData implements IBakeryChangeListener {

	private ObservableList<Order> ordersData = FXCollections.observableArrayList();


	public ObservableList<Order> getOrdersData() {
		return ordersData;
	}

	@Override
	public void onOrderAddedOrUpdated(Order order) {
		int index = ordersData.indexOf(order);
		if (index == -1)
			ordersData.add(order);
		else
			ordersData.set(index, order);

	}

}
