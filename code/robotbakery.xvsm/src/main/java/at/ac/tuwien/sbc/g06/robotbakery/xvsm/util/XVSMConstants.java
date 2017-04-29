package at.ac.tuwien.sbc.g06.robotbakery.xvsm.util;

import java.net.URI;

public class XVSMConstants {
	// Connection information
	public static final String HOST = "localhost";
	public static final int PORT = 9876;
	public static final URI BASE_SPACE_URI = URI.create("xvsm://" + HOST + ":" + PORT);

	// Container names
	public static final String COUNTER_CONTAINER_NAME = "Bakery/Counter";
	public static final String TERMINAL_CONTAINER_NAME = "Bakery/Terminal";
	public static final String STORAGE_CONTAINER_NAME = "Bakery/Storage";
	public static final String BAKEROOM_CONTAINER_NAME = "Bakery/Bakeroom";
	
	
	//XVSM Configuration 
	public static final long TIMEOUT_MILLIS=2000;

	private XVSMConstants() {
	};
}
