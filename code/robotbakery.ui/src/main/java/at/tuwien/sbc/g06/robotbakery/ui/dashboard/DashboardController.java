package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import javax.jws.soap.SOAPBinding;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController {
	private static final String PRODUCT1 = "Kaisersemmel";
	private static final String PRODUCT2 = "Bauernbrot";
	private static final String PRODUCT3 = "Marmorkuchen";
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

	@FXML
	private TextField product1Stock;
	@FXML
	private TextField product2Stock;
	@FXML
	private TextField product3Stock;
	@FXML
	private Label product1Label;
	@FXML
	private Label product2Label;
	@FXML
	private Label product3Label;

	public DashboardController() {
	}

	public void initializeUIData(DashboardData data) {
		ordersTable.setItems(data.getOrders());

		MapChangeListener<String, Integer> ls = (change) -> {
			updateProductInCounter(change.getKey(), change.getValueAdded());
		};
		data.getProductAmountInCounterMap().addListener(ls);
	}

	private void updateProductInCounter(String product, Integer stock) {
		switch (product) {
		case PRODUCT1:
			product1Stock.setText("" + stock);
			break;
		case PRODUCT2:
			product2Stock.setText("" + stock);
			break;
		case PRODUCT3:
			product3Stock.setText("" + stock);
			break;
		default:
			break;
		}

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

		product1Label.setText(PRODUCT1 + ":");
		product2Label.setText(PRODUCT2 + ":");
		product3Label.setText(PRODUCT3 + ":");

	}

}
