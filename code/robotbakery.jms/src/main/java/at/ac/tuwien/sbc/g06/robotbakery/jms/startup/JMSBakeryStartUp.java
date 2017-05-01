package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.BakeryUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSBakeryUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeryUIService;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class JMSBakeryStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Bakery-instance and required ui listeners
		BakeryUIChangeNotifier changeNotifer = new JMSBakeryUIChangeNotifier();
		DashboardInitializer.initializeDashboard(primaryStage, changeNotifer, new JMSBakeryUIService(),
				new JMSBakeryService());

	}

	public static void main(String[] args) {
		launch(args);
	}
}
