package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {
	@FXML
	private TableView<Order> ordersTable;
	@FXML
	private TableColumn<Order, String> orderId;
	@FXML
	private TableColumn<Order, String> orderStateId;
	@FXML
	private TableColumn<Order, String> orderTotalSum;

	@FXML
	private TableView<Item> itemsTable;
	@FXML
	private TableColumn<Item, String> itemProduct;
	@FXML
	private TableColumn<Item, String> itemAmount;
	@FXML
	private TableColumn<Item, String> itemCost;
	@FXML
	private TextField orderCustomerId;

	@FXML
	private TextField orderServiceId;

	public DashboardController() {
	}

	public void setUIData(DashboardData data) {
		ordersTable.setItems(data.getOrdersData());
	}

	@FXML
	public void initialize() {
		orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
		orderStateId.setCellValueFactory(new PropertyValueFactory<>("state"));
		orderTotalSum.setCellValueFactory(new PropertyValueFactory<>("totalSum"));

		itemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		itemAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		itemCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

		ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldOrder, newOrder) -> {
			if (newOrder != null) {
				itemsTable.setItems(FXCollections.observableArrayList(newOrder.getItems()));
				orderCustomerId.setText(newOrder.getCustomerId().toString());
				orderServiceId.setText(newOrder.getServiceRobotId().toString());
			}
		});
	}

}
