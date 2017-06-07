package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSDeliveryTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSServer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class JMSTabletStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Initialize Customer-instance and required ui listeners;
		JMSServer server = new JMSServer();
		int port = 0;
		for (port = JMSConstants.PORT_RANGE_START; port < JMSConstants.PORT_RANGE_END; port++) {
			if (JMSUtil.available(port))
				break;
		}
		String address = JMSConstants.LOCALHOST_ADDRESS + port;
		server.startUp(address);
		TabletUIChangeNotifer changeNotifer = new JMSTabletUIChangeNotifier();
		TabletUIChangeNotifer deliveryChangeNotifier = new JMSDeliveryTabletUIChangeNotifier();
		JMSTabletUIService tabletUIService = new JMSTabletUIService();
		JMSDeliveryTabletUIService deliveryUIService = new JMSDeliveryTabletUIService(tabletUIService, address);
		TabletInitializer.initializeTabletStartUp(primaryStage, changeNotifer, deliveryChangeNotifier, tabletUIService,
				deliveryUIService);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
