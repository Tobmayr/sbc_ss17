package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.util.Arrays;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message.MessageType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletData.CounterInformation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TabletController {

	@FXML
	TableView<CounterInformation> productsTable;
	@FXML
	TableColumn<CounterInformation, String> productsType;
	@FXML
	TableColumn<CounterInformation, String> productsStock;
	@FXML
	TableColumn<CounterInformation, String> productsPrice;

	@FXML
	TextField statusField;
	@FXML
	Button statusButton;
	@FXML
	TextField orderIdText;

	@FXML
	ComboBox<String> productCombo;
	@FXML
	Button addButton;
	@FXML
	TextField amountText;
	@FXML

	TableView<Item> itemsTable;
	@FXML
	TableColumn<Item, String> itemProduct;
	@FXML
	TableColumn<Item, String> itemAmount;
	@FXML
	TableColumn<Item, String> itemCost;
	private Order order;
	private Alert invalidOrderAlert;
	private Map<String, CounterInformation> counterMap;
	private ITabletUIService service;

	public void initialize(TabletData data, ITabletUIService uiService) {

		itemsTable.setItems(FXCollections.observableArrayList(order.getItems()));
		productsTable.setItems(data.getCounterInformationData());
		counterMap = data.getCounterProductsCounterMap();
		service = uiService;
		productCombo.getItems().forEach(s -> productsTable.getItems().add(data.new CounterInformation(s, 50, 500d)));

	}

	@FXML
	public void initialize() {
		order = new Order();
		orderIdText.setText(order.getId().toString());
		statusField.setText(getText(order.getState()));
		itemsTable.setItems(FXCollections.observableArrayList(order.getItems()));
		itemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		itemAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		itemCost.setCellValueFactory(new PropertyValueFactory<>("cost"));

		productsType.setCellValueFactory(new PropertyValueFactory<>("type"));
		productsPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerPiece"));
		productsStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

		productCombo.setItems(
				FXCollections.observableArrayList(Arrays.asList(SBCConstants.PRODUCT1_NAME, SBCConstants.PRODUCT2_NAME,
						SBCConstants.PRODUCT3_NAME, SBCConstants.PRODUCT4_NAME, SBCConstants.PRODUCT5_NAME)));

		itemAmount.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null | newValue.isEmpty()) {
				itemAmount.setText("" + 0);
			} else {
				try {
					Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					itemAmount.setText(oldValue);
				}
			}

		});
		itemAmount.setText("" + 0);

		invalidOrderAlert = new Alert(AlertType.ERROR);
		invalidOrderAlert.setTitle("Invalid order");
		invalidOrderAlert.setHeaderText("Your order contains invalid items");
		invalidOrderAlert.setContentText("The amount of some of your order's items is invalid. Please check again!");

		itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			productCombo.getSelectionModel().select(newValue.getProductName());
			itemAmount.setText("" + newValue.getAmount());
		});

		MenuItem itemsMenu = new MenuItem("Remove this item");

		itemsMenu.setOnAction((e) -> {
			Item item = itemsTable.getItems().get(itemsTable.getSelectionModel().getSelectedIndex());
			if (item != null) {
				order.removeItem(item);
			}
		});
		itemsTable.setContextMenu(new ContextMenu(itemsMenu));

	}

	private String getText(OrderState state) {
		switch (state) {
		case OPEN:
			return "Ordered- Order will be processed by our service robots";
		case PAID:
			return "Paid- Your order has been paid.";
		case UNDELIVERABLE:
			return "Undeliverable- Your order can not be delivered. Not enough products in stock";
		default:
			return "";
		}

	}

	@FXML
	public void onStatusButtonClicked() {
		if (orderValid()) {
			service.addOrderToCounter(order);
		} else
			invalidOrderAlert.showAndWait();
	}

	private boolean orderValid() {
		return itemsTable.getItems().stream()
				.allMatch(i -> i.getAmount() <= counterMap.get(i.getProductName()).getStock());
	}

	@FXML
	public void onAddButtonClicked() {
		order.addItem(productCombo.getSelectionModel().getSelectedItem(), Integer.parseInt(amountText.getText()));

	}

	public void onMessageAddedToTerminal(Message message) {
		if (message.getMessageType() == MessageType.ORDER_DECLINED) {
			// TODO ?
			return;
		}
		statusField.setText("Packed- Order is ready for payment in terminal");

	}

	public void onOrderUpdated(Order updated) {
		if (updated.getId().equals(order.getId())) {
			statusField.setText(getText(order.getState()));
		}

	}

}
