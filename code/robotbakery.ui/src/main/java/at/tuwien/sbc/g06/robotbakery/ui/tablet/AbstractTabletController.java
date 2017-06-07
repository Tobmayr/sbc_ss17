package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletData.CounterInformation;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public abstract class AbstractTabletController {
	@FXML
	protected TableView<CounterInformation> productsTable;
	@FXML
	protected TableColumn<CounterInformation, String> productsType;
	@FXML
	protected TableColumn<CounterInformation, String> productsStock;
	@FXML
	protected TableColumn<CounterInformation, String> productsPrice;

	@FXML
	protected TextField statusField;
	@FXML
	protected Button statusButton;
	@FXML
	protected TextField orderIdText;

	@FXML
	protected ComboBox<String> productCombo;
	@FXML
	protected Button addButton;
	@FXML
	protected TextField amountText;
	@FXML

	protected TableView<Item> itemsTable;
	@FXML
	protected TableColumn<Item, String> itemProduct;
	@FXML
	protected TableColumn<Item, String> itemAmount;
	@FXML
	protected TableColumn<Item, String> itemCost;

	@FXML
	protected TextField customerIdText;
	@FXML
	protected TextField totalSum;

	protected Order order;
	protected Alert invalidOrderAlert;
	protected Map<String, CounterInformation> counterMap;
	protected ITabletUIService service;
	protected ObservableList<Item> itemsData;
	protected PackedOrder packedOrder;
	protected UUID customerId;

	public void initialize(TabletData data, ITabletUIService uiService, UUID customerID) {

		itemsData = itemsTable.getItems();
		productsTable.setItems(data.getCounterInformationData());

		counterMap = data.getCounterProductsCounterMap();
		service = uiService;
		this.customerId = customerID;
		customerIdText.setText(customerID.toString());
		order.setCustomerId(customerID);

	}

	@FXML
	public void initialize() {
		order = new Order();
		orderIdText.setText(order.getId().toString());
		statusField.setText(getText(order.getState()));
		itemsTable.setItems(FXCollections.observableArrayList(order.getItemsMap().values()));
		itemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		itemAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		itemCost.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getCost())));

		productsType.setCellValueFactory(new PropertyValueFactory<>("type"));
		productsPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerPiece"));
		productsStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

		productCombo.setItems(
				FXCollections.observableArrayList(Arrays.asList(SBCConstants.PRODUCT1_NAME, SBCConstants.PRODUCT2_NAME,
						SBCConstants.PRODUCT3_NAME, SBCConstants.PRODUCT4_NAME, SBCConstants.PRODUCT5_NAME)));

		amountText.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null | newValue.isEmpty()) {
				amountText.setText("" + 0);
			} else {
				try {
					Integer.parseInt(newValue);
				} catch (NumberFormatException e) {
					amountText.setText(oldValue);
				}
			}

		});
		amountText.setText("" + 0);

		invalidOrderAlert = new Alert(AlertType.ERROR);
		invalidOrderAlert.setTitle("Invalid order");
		invalidOrderAlert.setHeaderText("Your order contains invalid items");
		invalidOrderAlert.setContentText("The amount of some of your order's items is invalid. Please check again!");

		itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				productCombo.getSelectionModel().select(newValue.getProductName());
				amountText.setText("" + newValue.getAmount());
				addButton.setText("Update");
			}

		});

		MenuItem itemsMenu = new MenuItem("Remove this item");

		itemsMenu.setOnAction((e) -> {
			Item item = itemsTable.getItems().get(itemsTable.getSelectionModel().getSelectedIndex());
			order.removeItem(item.getProductName());
			itemsData.remove(item);
			totalSum.setText(String.format("%.2f", order.getTotalSum()));
			if (itemsData.isEmpty())
				statusButton.setDisable(true);
		});
		itemsTable.setContextMenu(new ContextMenu(itemsMenu));
		statusField.setText("New- Add items to your order and click \"Send order\"");

		productCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			Item item = order.getItemsMap().get(newValue);
			if (item != null) {
				amountText.setText("" + item.getAmount());
				addButton.setText("Update");
			} else {
				amountText.setText("" + 0);
				addButton.setText("Add");
			}

		});

		totalSum.setText("0");
	}

	protected String getText(OrderState state) {
		if (state == null)
			return "";
		switch (state) {
		case ORDERED:
			return "Ordered- Order will be processed by our service robots";
		case PAID:
			return "Paid- Your order has been paid.";
		case UNGRANTABLE:
			return "Undeliverable- Your order can not be delivered. Not enough products in stock";
		case PACKED:
			return "Packed- Your order is packed and ready in the terminal";
		case DELIVERED:
			return "Delivered- Your order has arrived!";
		case UNDELIVERALBE:
			return "Undeliverable- Your order could not be delivered successfully";
		case WAITING:
			return " Waiting- A service robot is waiting for enough products to complete the order";
		}
		return "";

	}

	@FXML
	public void onStatusButtonClicked() {

		if (order.getState() == OrderState.ORDERED || order.getState() == OrderState.UNGRANTABLE) {
			if (orderValid()) {
				order.setState(OrderState.ORDERED);
				order.setTimestamp(new Timestamp(System.currentTimeMillis()));
				service.addOrderToCounter(order);
			} else
				invalidOrderAlert.showAndWait();
		} else if (order.getState() == OrderState.PACKED) {

			packedOrder = service.getPackedOrder(order);
			packedOrder.setState(OrderState.PAID);
			service.payOrder(packedOrder);
		
		}

	}

	boolean orderValid() {
		return itemsTable.getItems().stream()
				.allMatch(i -> i.getAmount() <= counterMap.get(i.getProductName()).getStock());
	}

	@FXML
	public void onAddButtonClicked() {
		int amount = Integer.parseInt(amountText.getText());
		String selectedString = productCombo.getSelectionModel().getSelectedItem();
		if (amountIsInValid(amount, selectedString))
			return;
		Item item = order.addItem(selectedString, amount);

		int index = itemsData.indexOf(item);
		if (index == -1)
			itemsData.add(item);
		else
			itemsData.set(index, item);
		if (statusButton.isDisabled())
			statusButton.setDisable(false);
		totalSum.setText(String.format("%.2f", order.getTotalSum()));
		addButton.setText("Update");

	}

	protected boolean amountIsInValid(int amount, String selectedString) {
		return amount == 0 || selectedString == null || counterMap.get(selectedString).getStock() < amount;
	}

	public void onOrderUpdated(Order updated) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (updated.getId().equals(order.getId())) {
					order.setState(updated.getState());
					changeState(updated.getState());
					statusField.setText(getText(updated.getState()));
				}

			}
		});

	}

	protected void changeState(OrderState state) {
		switch (state) {
		case PACKED:
			statusButton.setDisable(false);
			statusButton.setText("Pay order");
			break;
		case ORDERED:
			disableOrderEdit(true);
			break;
		case PAID:
			disableOrderEdit(true);
			break;
		case UNGRANTABLE:
			order.setState(OrderState.ORDERED);
			disableOrderEdit(false);
			statusButton.setText("Resend order");
			break;

		}

	}

	protected void disableOrderEdit(boolean value) {
		addButton.setDisable(value);
		productCombo.setDisable(value);
		amountText.setDisable(value);
		statusButton.setDisable(value);
	}

	public PackedOrder getPackedOrder() {
		return packedOrder;
	}

	public Order getOrder() {
		return order;
	}

	public ITabletUIService getService() {
		return service;
	}

}
