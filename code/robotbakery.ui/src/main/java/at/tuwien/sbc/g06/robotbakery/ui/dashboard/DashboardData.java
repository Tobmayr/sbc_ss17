package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;


import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardData implements IBakeryChangeListener {
	
	private Map<UUID, Order> ordersData = FXCollections.observableMap(new LinkedHashMap<>());

	@Override
	public void onOrderAddedOrUpdated(Order order) {
		ordersData.put(order.getId(), order);
		
	}

}
