package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Customer;
import at.ac.tuwien.sbc.g06.robotbakery.core.listener.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.XVSMTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMCustomerService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMTabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class XVSMTabletStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Customer-instance and required ui listeners;
		Customer customer = new Customer(new XVSMCustomerService());

		TabletUIChangeNotifer changeNotifer = new XVSMTabletUIChangeNotifier(customer.getId());
		TabletInitializer.intitalizeTablet(primaryStage, changeNotifer, new XVSMTabletUIService(), customer.getId());

	}

	public static void main(String[] args) {
		launch(args);
	}
}
