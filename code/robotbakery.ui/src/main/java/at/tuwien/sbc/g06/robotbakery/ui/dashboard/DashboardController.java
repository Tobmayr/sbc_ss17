package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
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
	private static final String PRODUCT4 = "Product 4";
	private static final String PRODUCT5 = "Prodcut 5";
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
	private TextField product4Stock;
	@FXML
	private TextField product5Stock;
	@FXML
	private Label product1Label;
	@FXML
	private Label product2Label;
	@FXML
	private Label product3Label;
	@FXML
	private Label product4Label;
	@FXML
	private Label product5Label;

	@FXML
	private TableView<Ingredient> ingredientsTable;
	@FXML
	private TableColumn<Ingredient, String> ingredientsType;
	@FXML
	private TableColumn<Ingredient, String> ingredientsStock;

	@FXML
	private TableView<Product> productsStorageTable;
	@FXML
	private TableColumn<Product, String> productsStorageType;
	@FXML
	private TableColumn<Product, String> productsStorageStock;

	@FXML
	private TableView<Product> productsTable;
	@FXML
	private TableColumn<Product, String> productsId;
	@FXML
	private TableColumn<Product, String> productsType;
	@FXML
	private TableView<Product> productsTable1;
	@FXML
	private TableColumn<Product, String> productsId1;
	@FXML
	private TableColumn<Product, String> productsType1;
	@FXML
	private TableView<Product> productsTable2;
	@FXML
	private TableColumn<Product, String> productsId2;
	@FXML
	private TableColumn<Product, String> productsType2;
	@FXML
	private TableView<Product> productsTable3;
	@FXML
	private TableColumn<Product, String> productsId3;
	@FXML
	private TableColumn<Product, String> productsType3;
	@FXML
	private TableView<Product> productsTable4;
	@FXML
	private TableColumn<Product, String> productsId4;
	@FXML
	private TableColumn<Product, String> productsType4;
	@FXML
	private TableView<Product> productsTable5;
	@FXML
	private TableColumn<Product, String> productsId5;
	@FXML
	private TableColumn<Product, String> productsType5;

	public void initializeUIData(DashboardData data) {
		ordersTable.setItems(data.getOrders());
		ingredientsTable.setItems(data.getIngredients());
		productsStorageTable.setItems(data.getProductsInStorage());
		setProductTableData(data.getStateToProductsMap());

		MapChangeListener<String, Integer> ls = (change) -> {
			updateProductInCounter(change.getKey(), change.getValueAdded());
		};
		data.getProductAmountInCounterMap().addListener(ls);
	}

	private void setProductTableData(Map<ProductState, ObservableList<Product>> stateToProductsMap) {
		stateToProductsMap.forEach((state, list) -> {
			switch (state) {
			case DOUGH_IN_STORAGE:
				productsTable.setItems(list);
				break;
			case DOUGH_IN_BAKEROOM:
				productsTable1.setItems(list);
				break;
			case PRODUCT_IN_STORAGE:
				productsTable2.setItems(list);
				break;
			case PRODUCT_IN_COUNTER:
				productsTable3.setItems(list);
				break;
			case PRODUCT_IN_TERMINAL:
				productsTable4.setItems(list);
				break;
			case PRODUCT_SOLD:
				productsTable5.setItems(list);
				break;
			}
		});

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
		case PRODUCT4:
			product4Stock.setText("" + stock);
			break;
		case PRODUCT5:
			product5Stock.setText("" + stock);
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

		ingredientsType.setCellValueFactory(new PropertyValueFactory<>("type"));
		// ingredientsStock.setCellValueFactory(new
		// PropertyValueFactory<>("amount"));

		productsStorageType.setCellValueFactory(new PropertyValueFactory<>("productName"));
		// productsStorageStock.setCellValueFactory(new
		// PropertyValueFactory<>("amount"));

		productsId.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productsId1.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType1.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productsId2.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType2.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productsId3.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType3.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productsId4.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType4.setCellValueFactory(new PropertyValueFactory<>("productName"));
		productsId5.setCellValueFactory(new PropertyValueFactory<>("id"));
		productsType5.setCellValueFactory(new PropertyValueFactory<>("productName"));

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
		product4Label.setText(PRODUCT4 + ":");
		product5Label.setText(PRODUCT5 + ":");

	}

}
