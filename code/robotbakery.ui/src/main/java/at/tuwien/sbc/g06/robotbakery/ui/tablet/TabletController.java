package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
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

	@FXML
	TextField customerIdText;
	@FXML
	TextField totalSum;

	@FXML
	private TableView<Prepackage> prepackagesTable;
	@FXML
	private TableColumn<Prepackage, String> prepackageId;
	@FXML
	private TableColumn<Prepackage, String> prepackageState;
	@FXML
	private TableColumn<Prepackage, String> prepackageTotal;
	@FXML

	private TextField prepackageCustomerId;
	@FXML
	private TextField prepackageServiceRobotId;

	@FXML
	private TableView<Product> prepackageItemsTable;
	@FXML
	private TableColumn<Product, String> prepackageItemProduct;
	@FXML
	private TableColumn<Product, String> prepackageItemCost;

	private Order order;
	private Alert invalidOrderAlert;
	private Map<String, CounterInformation> counterMap;
	private ITabletUIService service;
	private ObservableList<Item> itemsData;
	private PackedOrder packedOrder;

	public void initialize(TabletData data, ITabletUIService uiService, UUID customerID) {

		itemsData = itemsTable.getItems();
		productsTable.setItems(data.getCounterInformationData());
		prepackagesTable.setItems(data.getPrepackagesList());
		counterMap = data.getCounterProductsCounterMap();
		service = uiService;

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

		prepackageId.setCellValueFactory(new PropertyValueFactory<>("id"));
		prepackageState.setCellValueFactory(new PropertyValueFactory<>("state"));
		prepackageTotal.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getTotalSum())));

		prepackageItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		prepackageItemCost.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));

		prepackagesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldPackage, newPackage) -> {
			Platform.runLater(() -> {
				if (newPackage != null) {
					prepackageItemsTable.setItems(FXCollections.observableArrayList(newPackage.getProducts()));
					if (newPackage.getCustomerId() != null) {
						prepackageCustomerId.setText(newPackage.getCustomerId().toString());
					}
					prepackageServiceRobotId.setText(newPackage.getServiceRobotId().toString());

				}
			});

		});

	}

	private String getText(OrderState state) {
		if (state == null)
			return "";
		switch (state) {
		case OPEN:
			return "Ordered- Order will be processed by our service robots";
		case PAID:
			return "Paid- Your order has been paid.";
		case UNDELIVERABLE:
			return "Undeliverable- Your order can not be delivered. Not enough products in stock";
		case DELIVERED:
			return "Packed- Your order is packed and ready in the terminal";
		}
		return "";

	}

	@FXML
	public void onStatusButtonClicked() {
		if (order.getState() == OrderState.OPEN || order.getState() == OrderState.UNDELIVERABLE) {
			if (orderValid()) {
				order.setState(OrderState.OPEN);
				order.setTimestamp(new Timestamp(System.currentTimeMillis()));
				service.addOrderToCounter(order);
			} else
				invalidOrderAlert.showAndWait();
		} else if (order.getState() == OrderState.DELIVERED) {
			packedOrder = service.getOrderPackage(order);
			if (service.payOrder(packedOrder)) {
				statusButton.setText(getText(OrderState.PAID));
			}
		}

	}

	private boolean orderValid() {
		return itemsTable.getItems().stream()
				.allMatch(i -> i.getAmount() <= counterMap.get(i.getProductName()).getStock());
	}

	@FXML
	public void onAddButtonClicked() {
		int amount = Integer.parseInt(amountText.getText());
		String selectedString = productCombo.getSelectionModel().getSelectedItem();
		if (amount == 0 || selectedString == null || counterMap.get(selectedString).getStock() < amount)
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

	public void onOrderUpdated(Order updated) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				if (updated.getId().equals(order.getId())) {
					order.setState(OrderState.DELIVERED);
					changeState(updated.getState());
					statusField.setText(getText(updated.getState()));
				}

			}
		});

	}

	private void changeState(OrderState state) {
		switch (state) {
		case DELIVERED:
			statusButton.setDisable(false);
			statusButton.setText("Pay order");
			break;
		case OPEN:
			disableOrderEdit(true);
			break;
		case PAID:
			disableOrderEdit(true);
			break;
		case UNDELIVERABLE:
			order.setState(OrderState.OPEN);
			disableOrderEdit(false);
			statusButton.setText("Resend order");
			break;

		}

	}

	private void disableOrderEdit(boolean value) {
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

	@FXML
	public void onTakeButtonClicked() {
		UUID packageId = prepackagesTable.getSelectionModel().getSelectedItem().getId();
		Prepackage prepackage=service.getPrepackage(packageId);
		if (prepackage!=null){
			prepackagesTable.getItems().remove(prepackage);
			prepackageItemsTable.getItems().clear();
		}
			
		
	}

}
