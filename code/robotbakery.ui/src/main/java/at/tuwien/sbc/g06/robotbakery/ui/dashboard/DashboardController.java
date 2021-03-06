package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.Item;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardData.ItemCount;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardData.ProductState;
import at.tuwien.sbc.g06.robotbakery.ui.test.TestDataInitializer;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController {

	/*
	 * BakeryOverview-Tab
	 */
	@FXML
	private TableView<ItemCount> ingredientsTable;
	@FXML
	private TableColumn<ItemCount, String> ingredientsType;
	@FXML
	private TableColumn<ItemCount, String> ingredientsStock;

	@FXML
	private TableView<ItemCount> productsStorageTable;
	@FXML
	private TableColumn<ItemCount, String> productsStorageType;
	@FXML
	private TableColumn<ItemCount, String> productsStorageStock;

	@FXML
	private TableView<ItemCount> productsCounterTable;
	@FXML
	private TableColumn<ItemCount, String> productsCounterType;
	@FXML
	TableColumn<ItemCount, String> productsCounterStock;

	@FXML
	private TextField restockFlourAmount;
	@FXML
	private TextField restockEggAmount;
	@FXML
	private TextField restockBakeSweetAmount;
	@FXML
	private TextField restockBakeSpicyAmount;
	@FXML
	private Button clearButton;
	@FXML
	private Button restockButton;

	private List<TextField> collectedRestockFields;
	@FXML
	private TextField serviceRobotCounter;
	@FXML
	private TextField kneadRobotCounter;
	@FXML
	private TextField bakeRobotCounter;

	@FXML
	private TextField fileChooserText;
	@FXML
	private Button fileChoserOpenButton;
	@FXML
	private Button loadPropButton;
	/*
	 * Order/Prepackage Tab
	 */
	@FXML
	private TableView<Order> ordersTable;
	@FXML
	private TableColumn<Order, String> orderId;
	@FXML
	private TableColumn<Order, OrderState> orderState;
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

	/*
	 * Product Details Tab
	 */
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

	private List<TableView<Product>> collectedProductTables;
	private List<TableColumn<Product, String>> collectedProductsIds;
	private List<TableColumn<Product, String>> collectedProductsTypes;

	/*
	 * Controller fields
	 */
	private Stage mainStage;
	private IBakeryUIService uiService;

	private TestDataInitializer testDataInitializer;

	public DashboardController() {
	}

	public void initialize(DashboardData data, Stage mainStage, IBakeryUIService uiService) {
		this.mainStage = mainStage;
		this.uiService = uiService;
		testDataInitializer = new TestDataInitializer(uiService);
		ordersTable.setItems(data.getOrders());
		prepackagesTable.setItems(data.getPrepackages());
		ingredientsTable.setItems(data.getIngredients());
		productsStorageTable.setItems(data.getProductsInStorage());
		productsCounterTable.setItems(data.getProductsInCounter());
		setProductTableData(data.getStateToProductsMap());

	}

	public void showContributionDetails(Product product) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getClassLoader().getResource(UIConstants.CONTRIBUTION_DIALOG_FXML));
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
		collectedProductTables = Arrays.asList(productsTable, productsTable1, productsTable2, productsTable3,
				productsTable4, productsTable5);
		collectedProductsIds = Arrays.asList(productsId, productsId1, productsId2, productsId3, productsId4,
				productsId5);
		collectedProductsTypes = Arrays.asList(productsType, productsType1, productsType2, productsType3, productsType4,
				productsType5);
		collectedRestockFields = Arrays.asList(restockFlourAmount, restockEggAmount, restockBakeSweetAmount,
				restockBakeSpicyAmount);

		orderId.setCellValueFactory(new PropertyValueFactory<>("id"));
		orderState.setCellValueFactory(new PropertyValueFactory<>("state"));
		orderTotalSum.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getTotalSum())));

		prepackageId.setCellValueFactory(new PropertyValueFactory<>("id"));
		prepackageState.setCellValueFactory(new PropertyValueFactory<>("state"));
		prepackageTotal.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getTotalSum())));

		itemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		itemAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
		itemCost.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getCost())));

		prepackageItemProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
		prepackageItemCost.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrice())));

		ingredientsType.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		ingredientsStock.setCellValueFactory(new PropertyValueFactory<>("amount"));

		productsStorageType.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		productsStorageStock.setCellValueFactory(new PropertyValueFactory<>("amount"));
		productsCounterStock.setCellValueFactory(new PropertyValueFactory<>("amount"));
		productsCounterType.setCellValueFactory(new PropertyValueFactory<>("itemName"));

		collectedProductsIds.forEach(pID -> pID.setCellValueFactory(new PropertyValueFactory<>("id")));
		collectedProductsTypes.forEach(pID -> pID.setCellValueFactory(new PropertyValueFactory<>("productName")));

		ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldOrder, newOrder) -> {
			Platform.runLater(() -> {
				if (newOrder != null) {
					itemsTable.setItems(FXCollections.observableArrayList(newOrder.getItemsMap().values()));
					orderCustomerId.setText(newOrder.getCustomerId().toString());
					if (newOrder.getServiceRobotId() != null) {
						orderServiceId.setText(newOrder.getServiceRobotId().toString());
					}

				}
			});

		});

		prepackagesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldPackage, newPackage) -> {
			Platform.runLater(() -> {
				if (newPackage != null) {
					prepackageItemsTable.setItems(FXCollections.observableArrayList(newPackage.getProducts()));
					String customerId = newPackage.getCustomerId() == null ? "" : newPackage.getCustomerId().toString();
					prepackageCustomerId.setText(customerId);
					prepackageServiceRobotId.setText(newPackage.getServiceRobotId().toString());

				}
			});

		});

		collectedProductTables.forEach(table -> {
			MenuItem detailsMenu = new MenuItem("Show contributions");

			detailsMenu.setOnAction((e) -> {
				Product product = table.getItems().get(table.getSelectionModel().getSelectedIndex());
				if (product != null) {
					showContributionDetails(product);
				}
			});
			table.setContextMenu(new ContextMenu(detailsMenu));
		});

		collectedRestockFields.forEach(field -> {
			field.textProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue == null | newValue.isEmpty()) {
					field.setText("" + 0);
				} else {
					try {
						Integer.parseInt(newValue);
					} catch (NumberFormatException e) {
						field.setText(oldValue);
					}
				}

			});
			field.setText("" + 0);
		});

	}

	@FXML
	public void clearButtonClicked() {
		collectedRestockFields.forEach(field -> field.setText("" + 0));
	}

	@FXML
	public void restockButtonClicked() {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		collectedRestockFields.forEach(field -> {
			int amount = Integer.parseInt(field.getText());
			IntStream.range(0, amount).forEach(i -> ingredients.add(generateIngredient(field)));

		});
		uiService.addIngredientsToStorage(ingredients);
		clearButtonClicked();

	}

	private Ingredient generateIngredient(TextField field) {
		if (field.equals(restockFlourAmount))
			return new FlourPack();
		if (field.equals(restockEggAmount))
			return new Ingredient(IngredientType.EGGS);
		if (field.equals(restockBakeSpicyAmount))
			return new Ingredient(IngredientType.BAKING_MIX_SPICY);
		else
			return new Ingredient(IngredientType.BAKING_MIX_SWEET);

	}

	public void onRobotStart(Class<? extends Robot> robot) {
		if (robot.equals(ServiceRobot.class)) {
			changeRobotCounter(serviceRobotCounter, true);
		} else if (robot.equals(KneadRobot.class)) {
			changeRobotCounter(kneadRobotCounter, true);
		} else if (robot.equals(BakeRobot.class)) {
			changeRobotCounter(bakeRobotCounter, true);
		}

	}

	public void onRobotShutdown(Class<? extends Robot> robot) {
		if (robot.equals(ServiceRobot.class)) {
			changeRobotCounter(serviceRobotCounter, false);
		} else if (robot.equals(KneadRobot.class)) {
			changeRobotCounter(kneadRobotCounter, false);
		} else if (robot.equals(BakeRobot.class)) {
			changeRobotCounter(bakeRobotCounter, false);
		}

	}

	private void changeRobotCounter(TextField field, boolean increment) {
		int prev = Integer.parseInt(field.getText());
		if (increment)
			prev++;
		else
			prev--;
		field.setText("" + prev);
	}

	public IBakeryUIService getUiService() {
		return uiService;
	}

	@FXML
	public void openFileChooser() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Properties Files", "*.properties");
		fileChooser.getExtensionFilters().add(extFilter);

		// Show open file dialog
		File file = fileChooser.showOpenDialog(null);
		testDataInitializer.setFile(file);
		fileChooserText.setText(file.getPath());
	}

	@FXML
	public void loadTestData() {
		if (fileChooserText.getText() != null && !fileChooserText.getText().isEmpty()) {
			testDataInitializer.initFromProperties();
		}

	}
}
