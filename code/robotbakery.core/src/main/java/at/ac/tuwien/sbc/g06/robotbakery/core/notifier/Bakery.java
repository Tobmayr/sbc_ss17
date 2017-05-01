package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryUIChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
/**
 * Class which represents the bakery. Is used for initialization purposes, surves as ChangeListenerNotifer;
 * @author Tobias Ortmayr (1026279)
 *
 */
public abstract class Bakery extends ChangeNotifer<IBakeryUIChangeListener> implements IBakeryService {
	/**
	 * Needs to be extendend by each framework-specific implementation (XVSM &
	 * JMS)
	 */
}
