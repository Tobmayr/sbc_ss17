package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TabletController extends AbstractTabletController {

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

	@Override
	public void initialize(TabletData data, ITabletUIService uiService, UUID customerID) {
		super.initialize(data, uiService, customerID);
		prepackagesTable.setItems(data.getPrepackagesList());
	}

	@Override
	public void initialize() {
		super.initialize();
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

	@FXML
	public void onTakeButtonClicked() {
		UUID packageId = prepackagesTable.getSelectionModel().getSelectedItem().getId();
		Prepackage prepackage = service.getPrepackage(packageId);
		prepackage.setCustomerId(customerId);
		prepackage.setState(Prepackage.STATE_SOLD);
		service.updatePrepackage(prepackage);
		if (prepackage != null) {
			prepackagesTable.getItems().remove(prepackage);
			prepackageItemsTable.getItems().clear();
		}

	}

}
