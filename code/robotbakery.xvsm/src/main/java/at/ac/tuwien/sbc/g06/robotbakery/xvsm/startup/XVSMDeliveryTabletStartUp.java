package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier.XVSMDeliveryTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier.XVSMTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMTabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Matthias Höllthaler on 01.06.2017.
 */
public class XVSMDeliveryTabletStartUp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize Customer-instance and required ui listeners;

        TabletUIChangeNotifer changeNotifer = new XVSMDeliveryTabletUIChangeNotifier();
        TabletInitializer.intitalizeTablet(primaryStage, changeNotifer, new XVSMDeliveryTabletUIService());

    }

    public static void main(String[] args) {
        launch(args);
    }
}
