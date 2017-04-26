package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.Contribution;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ContributionDialogController {

	@FXML
	TableView<Contribution> contributionsTable;
	@FXML
	TableColumn<Contribution, ContributionType> contribution;
	@FXML
	TableColumn<Contribution, String> contributorId;
	@FXML
	TableColumn<Contribution, String> contributorType;
	@FXML
	TextField type;
	@FXML
	TextField productId;

	@FXML
	private void initialize() {
		contribution.setCellValueFactory(new PropertyValueFactory<>("type"));
		contribution.setCellValueFactory(new PropertyValueFactory<>("contributerId"));
		contributorType.setCellValueFactory(new PropertyValueFactory<>("contributor"));
	}

	public void setProduct(Product product) {
		contributionsTable.setItems(FXCollections.observableArrayList(product.getContributions()));
		productId.setText(product.getId().toString());
		type.setText(product.getProductName());
	}

}
