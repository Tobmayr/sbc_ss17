package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.XVSMBakery;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardController;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardData;
import at.tuwien.sbc.g06.robotbakery.ui.util.UIConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class XVSMBakeryStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Bakery-instance and required ui listeners
		Capi server = new Capi(DefaultMzsCore.newInstance());
		Bakery bakery = new XVSMBakery(server);
		DashboardData bakeryData = new DashboardData();
		bakery.registerChangeListener(bakeryData);
		bakery.initialize();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource(UIConstants.DASHBOARD_FXML_FILENAME));
		Parent root = loader.load();
		primaryStage.setTitle("SBC RobotBakery Dashboard");
		Scene scene = new Scene(root);
		scene.getStylesheets()
				.add(getClass().getClassLoader().getResource(UIConstants.MAIN_CSS_FILENAME).toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(we -> System.exit(0));
		DashboardController controller = loader.getController();
		controller.setUIData(bakeryData);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
