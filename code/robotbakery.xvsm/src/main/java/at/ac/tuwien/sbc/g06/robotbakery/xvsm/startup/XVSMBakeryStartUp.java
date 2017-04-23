package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import java.io.IOException;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.XVSMBakery;
import at.tuwien.sbc.g06.robotbakery.ui.BakeryUI;

public class XVSMBakeryStartUp {

	public static void main(String[] args) throws IOException {
		Capi server = new Capi(DefaultMzsCore.newInstance());
		Bakery bakery = new XVSMBakery(server);
		BakeryUI bakeryUI = new BakeryUI();
		bakery.registerChangeListener(bakeryUI);
		bakery.initialize();
		while (System.in.read() != -1) {
			///
		}
	}
}
