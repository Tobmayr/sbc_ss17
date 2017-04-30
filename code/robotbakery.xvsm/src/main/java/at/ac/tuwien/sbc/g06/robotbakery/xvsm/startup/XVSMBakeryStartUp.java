package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.BakeryUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.XVSMBakeryUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMBakeryUIService;
import at.tuwien.sbc.g06.robotbakery.ui.dashboard.DashboardInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class XVSMBakeryStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Bakery-instance and required ui listeners
		Capi server = new Capi(DefaultMzsCore.newInstance());
		BakeryUIChangeNotifier changeNotifer = new XVSMBakeryUIChangeNotifer(server);
		DashboardInitializer.initializeDashboard(primaryStage, changeNotifer, new XVSMBakeryUIService(server),
				new XVSMBakeryService(server));

	}

	public static void main(String[] args) {
		launch(args);
	}
}
