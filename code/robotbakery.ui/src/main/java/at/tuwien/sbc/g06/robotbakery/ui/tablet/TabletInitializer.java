package at.tuwien.sbc.g06.robotbakery.ui.tablet;

import java.io.IOException;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardInitializer;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TabletInitializer {
	private TabletInitializer() {

	}

	public static void intitalizeTablet(Stage primaryStage, TabletUIChangeNotifer changeNotifer,
			ITabletUIService uiService) throws IOException {
		UUID customerID= UUID.randomUUID();
		changeNotifer.setCustomerID(customerID);
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(DashboardInitializer.class.getClassLoader().getResource(UIConstants.TABLE_FXML_FILENAME));
		Parent root = loader.load();
		primaryStage.setTitle(UIConstants.TABLET_TITLE);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(
				TabletInitializer.class.getClassLoader().getResource(UIConstants.MAIN_CSS_FILENAME).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		primaryStage.setResizable(false);
		
		TabletController controller = loader.getController();
		TabletData tabletData = new TabletData(controller);
		changeNotifer.registerChangeListener(tabletData);
		controller.initialize(tabletData, uiService,customerID);
	}

}
