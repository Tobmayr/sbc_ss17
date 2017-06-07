package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.net.URI;
import java.sql.Timestamp;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DeliveryTabletController extends AbstractTabletController {
	@FXML
	protected TextField deliveryAddressText;
	@FXML
	protected TextField deliveryRobotText;

	@Override
	public void initialize(TabletData data, ITabletUIService uiService, UUID customerID) {
		// TODO Auto-generated method stub
		super.initialize(data, uiService, customerID);
		order.setDelivery(true);
		URI deliveryAddress = service.getDeliveryURI();
		order.setDeliveryAddress(deliveryAddress);
		deliveryAddressText.setText(deliveryAddress.toString());
	}

	@Override
	public void onStatusButtonClicked() {
		if (order.getState() == OrderState.ORDERED || order.getState() == OrderState.UNGRANTABLE) {
			if (orderValid()) {
				order.setState(OrderState.ORDERED);
				order.setTimestamp(new Timestamp(System.currentTimeMillis()));
				service.addOrderToCounter(order);
				disableOrderEdit(true);
			} else
				invalidOrderAlert.showAndWait();
		}

	}
	
	

	@Override
	public void onAddButtonClicked() {
		// TODO Auto-generated method stub
		super.onAddButtonClicked();
	}

	@Override
	protected boolean orderValid() {
		return itemsTable.getItems().stream().allMatch(i -> i.getAmount() <= SBCConstants.COUNTER_MAX_CAPACITY);

	}

	@Override
	protected void changeState(OrderState state) {
		// DO nothing
	}

	@Override
	protected boolean amountIsInValid(int amount, String selectedString) {
		return amount == 0 || selectedString == null || amount > SBCConstants.COUNTER_MAX_CAPACITY;
	}

	@Override
	public void onOrderUpdated(Order updated) {
		super.onOrderUpdated(updated);
		if (updated.getDeliveryRobotId() != null) {
			deliveryRobotText.setText(updated.getDeliveryRobotId().toString());
		}
	}

}
