package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardData.ProductCount;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

	@FXML
	private TableView<Ingredient> ingredientsTable;
	@FXML
	private TableColumn<Ingredient, IngredientType> ingredientsType;
	@FXML
	private TableColumn<Ingredient, String> ingredientsStock;

	@FXML
	private TableView<ProductCount> productsStorageTable;
	@FXML
	private TableColumn<ProductCount, String> productsStorageType;
	@FXML
	private TableColumn<ProductCount, String> productsStorageStock;

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

	@FXML
	TableView<ProductCount> productsCounterTable;
	@FXML
	TableColumn<ProductCount, String> productsCounterType;
	@FXML
	TableColumn<ProductCount, String> productsCounterStock;

	private List<TableView<Product>> collectedProducTables;
	private List<TableColumn<Product, String>> collectedProductsIds;
	private List<TableColumn<Product, String>> collectedProductsTypes;
	private Stage mainStage;

	public DashboardController() {
	}

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}

	public void showContributionDetails(Product product) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getClassLoader().getResource(UIConstants.CONTRIBUTION_DIALOG_FXML_FILENAME));
			Parent root = loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle(UIConstants.CONTRIBUTION_DIALOG_TITLE);
			dialogStage.initOwner(mainStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			Scene scene = new Scene(root);
			dialogStage.setResizable(false);
			dialogStage.setScene(scene);

			ContributionDialogController contributionDialogController = loader.getController();
			contributionDialogController.setProduct(product);

			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void initializeUIData(DashboardData data) {
		ordersTable.setItems(data.getOrders());
		ingredientsTable.setItems(data.getIngredients());
		productsStorageTable.setItems(data.getProductsInStorage());
		productsCounterTable.setItems(data.getProductsInCounter());
		setProductTableData(data.getStateToProductsMap());

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

	@FXML
	public void initialize() {
		collectedProducTables = Arrays.asList(productsTable, productsTable1, productsTable2, productsTable3,
				productsTable4, productsTable5);
		collectedProductsIds = Arrays.asList(productsId, productsId1, productsId2, productsId3, productsId4,
				productsId5);
		collectedProductsTypes = Arrays.asList(productsType, productsType1, productsType2, productsType3, productsType4,
				productsType5);

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
		productsStorageStock.setCellValueFactory(new PropertyValueFactory<>("amount"));
		productsCounterStock.setCellValueFactory(new PropertyValueFactory<>("amount"));
		productsCounterType.setCellValueFactory(new PropertyValueFactory<>("productName"));

		collectedProductsIds.forEach(pID -> pID.setCellValueFactory(new PropertyValueFactory<>("id")));
		collectedProductsTypes.forEach(pID -> pID.setCellValueFactory(new PropertyValueFactory<>("productName")));

		ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldOrder, newOrder) -> {
			if (newOrder != null) {
				itemsTable.setItems(FXCollections.observableArrayList(newOrder.getItems()));
				orderCustomerId.setText(newOrder.getCustomerId().toString());
				if (newOrder.getServiceRobotId() != null) {
					orderServiceId.setText(newOrder.getServiceRobotId().toString());
				}

			}
		});

		collectedProducTables.forEach(table -> {
			MenuItem detailsMenu = new MenuItem("Show contributions");

			detailsMenu.setOnAction((e) -> {
				Product product = table.getItems().get(table.getSelectionModel().getSelectedIndex());
				if (product != null) {
					showContributionDetails(product);
				}
			});
			table.setContextMenu(new ContextMenu(detailsMenu));
		});

	}

}
