package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.io.IOException;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TabletInitializer {
	private TabletInitializer() {

	}

	public static void initializeTabletStartUp(Stage primaryStage, TabletUIChangeNotifer changeNotifer,
			ITabletUIService uiService, IDeliveryTabletUIService deliveryUIService) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(
				TabletInitializer.class.getClassLoader().getResource(UIConstants.TABLET_STARTUP_DIALOG_FMXL));
		Parent root = loader.load();
		primaryStage.setTitle(UIConstants.TABLET_STARTUP_TITLE);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(
				TabletInitializer.class.getClassLoader().getResource(UIConstants.MAIN_CSS_FILENAME).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		primaryStage.setResizable(false);
		TabletStartUpController controller = loader.getController();
		controller.initialize(uiService, changeNotifer, deliveryUIService);

	}

	public static void intitalizeTablet(Stage primaryStage, TabletUIChangeNotifer changeNotifer,
			ITabletUIService uiService, boolean normalTablet) throws IOException {
		UUID customerID = UUID.randomUUID();
		changeNotifer.setCustomerID(customerID);
		FXMLLoader loader = new FXMLLoader();
		String fxmlFile = normalTablet ? UIConstants.TABLET_FXML : UIConstants.DELIVERY_TABLET_FMXL;
		loader.setLocation(TabletInitializer.class.getClassLoader().getResource(fxmlFile));
		Parent root = loader.load();
		String title = normalTablet ? UIConstants.TABLET_TITLE : UIConstants.DELIVERY_TABLET_TITLE;
		primaryStage.setTitle(title);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(
				TabletInitializer.class.getClassLoader().getResource(UIConstants.MAIN_CSS_FILENAME).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		primaryStage.setResizable(false);

		AbstractTabletController controller = loader.getController();
		TabletData tabletData = new TabletData();
		changeNotifer.registerChangeListener(tabletData);
		controller.initialize(tabletData, uiService, customerID);
		tabletData.delegateController(controller);

	}

}
