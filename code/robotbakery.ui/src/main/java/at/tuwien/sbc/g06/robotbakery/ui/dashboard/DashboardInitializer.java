package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.io.IOException;

import at.ac.tuwien.sbc.g06.robotbakery.core.Bakery;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardInitializer {

	private DashboardInitializer() {

	}

	public static void initializeDashboard(Stage primaryStage, Bakery bakery) throws IOException {

		DashboardData bakeryData = new DashboardData();
		bakery.registerChangeListener(bakeryData);
		bakery.initialize();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(
				DashboardInitializer.class.getClassLoader().getResource(UIConstants.DASHBOARD_FXML_FILENAME));
		Parent root = loader.load();
		primaryStage.setTitle(UIConstants.DASHBOARD_TITLE);
		Scene scene = new Scene(root);
		scene.getStylesheets().add(DashboardInitializer.class.getClassLoader()
				.getResource(UIConstants.MAIN_CSS_FILENAME).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		DashboardController controller = loader.getController();
		controller.initializeUIData(bakeryData);
		controller.setMainStage(primaryStage);
	}
}
