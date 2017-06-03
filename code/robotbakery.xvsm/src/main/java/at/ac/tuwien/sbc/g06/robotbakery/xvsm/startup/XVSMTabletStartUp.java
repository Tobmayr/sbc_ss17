package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier.XVSMTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMTabletUIService;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class XVSMTabletStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		TabletInitializer.initializeTabletStartUp(primaryStage, new XVSMTabletUIChangeNotifier(),
				new XVSMTabletUIService());

	}
	
	public static void main(String [] args){
		launch();
	}

}
