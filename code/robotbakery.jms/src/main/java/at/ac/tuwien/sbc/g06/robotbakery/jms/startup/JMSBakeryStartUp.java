package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSBakery;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSServer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSNotificationService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class JMSBakeryStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Bakery-instance and required ui listeners
		JMSServer server = new JMSServer();
		server.startUp(JMSConstants.SERVER_ADDRESS);
		Bakery bakery = new JMSBakery();

		DashboardInitializer.initializeDashboard(primaryStage, bakery, new JMSBakeryUIService(), new JMSNotificationService());

	}

	public static void main(String[] args) {
		launch(args);
	}
}
