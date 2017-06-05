package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier.XVSMTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMDeliveryTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMTabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.tuwien.sbc.g06.robotbakery.ui.tablet.TabletInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

public class XVSMTabletStartUp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MzsCore core= DefaultMzsCore.newInstance(XVSMConstants.RANDOM_FREE_PORT);
		Capi capi= new Capi(core);
		TabletInitializer.initializeTabletStartUp(primaryStage, new XVSMTabletUIChangeNotifier(capi),
				new XVSMTabletUIService(capi), new XVSMDeliveryTabletUIService(capi));

	}
	
	public static void main(String [] args){
		launch();
	}

}
