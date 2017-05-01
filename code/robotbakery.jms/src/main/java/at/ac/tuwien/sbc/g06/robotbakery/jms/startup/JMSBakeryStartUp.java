package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import java.util.Arrays;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JSMBakery;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSServer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSKneadRobotService;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class JMSBakeryStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Bakery-instance and required ui listeners
		JMSServer server = new JMSServer();
	    server.startUp();
		Bakery bakery = new JSMBakery();
		IBakeryUIService bla=new JMSBakeryUIService();
		DashboardInitializer.initializeDashboard(primaryStage, bakery,bla,
				new JMSBakeryService());
		bla.addIngredientsToStorage(Arrays.asList(new FlourPack(),new FlourPack()));
		Map<IngredientType,Integer> test=new JMSKneadRobotService().getIngredientStock();
System.out.println();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
