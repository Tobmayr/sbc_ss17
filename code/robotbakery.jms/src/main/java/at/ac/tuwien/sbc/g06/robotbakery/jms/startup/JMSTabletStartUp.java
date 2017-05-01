package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSTabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class JMSTabletStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Customer-instance and required ui listeners;

		TabletUIChangeNotifer changeNotifer = new JMSTabletUIChangeNotifier();
		TabletInitializer.intitalizeTablet(primaryStage, changeNotifer, new JMSTabletUIService());

	}

	public static void main(String[] args) {
		launch(args);
	}
}
