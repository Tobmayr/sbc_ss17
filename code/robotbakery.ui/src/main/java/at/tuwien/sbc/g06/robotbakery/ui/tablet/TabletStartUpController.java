package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.io.IOException;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TabletStartUpController {

	@FXML
	Button normalCustomerStartButton;

	@FXML
	Button deliveryCustomerStartButton;

	private ITabletUIService uiService;

	private TabletUIChangeNotifer changeNotifer;

	private IDeliveryTabletUIService deliveryUIService;

	private TabletUIChangeNotifer deliveryChangeNotifer;

	@FXML
	public void onRegularCustomerClicked() throws IOException {
		Stage stage = (Stage) normalCustomerStartButton.getScene().getWindow();
		TabletInitializer.intitalizeTablet(stage, changeNotifer, uiService, true);

	}

	@FXML
	public void onDeliveryCustomerClicked() throws IOException {
		Stage stage = (Stage) deliveryCustomerStartButton.getScene().getWindow();
		TabletInitializer.intitalizeTablet(stage, deliveryChangeNotifer, deliveryUIService, false);

	}

	public void initialize(ITabletUIService uiService, TabletUIChangeNotifer changeNotifer,
			IDeliveryTabletUIService deliveryUIService, TabletUIChangeNotifer deliveryChangeNotifer) {
		this.uiService = uiService;
		this.changeNotifer = changeNotifer;
		this.deliveryUIService = deliveryUIService;
		this.deliveryChangeNotifer = deliveryChangeNotifer;

	}

}
