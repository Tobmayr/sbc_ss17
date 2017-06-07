package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;

/**
 * Class which represents the bakery. Is used for initialization purposes,
 * serves as ChangeListenerNotifer;
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public abstract class Bakery implements IBakeryService {
	private static Logger logger = LoggerFactory.getLogger(Bakery.class);
	protected ChangeNotifer changeNotifer;
	
	public Bakery(ChangeNotifer changeNotifer){
		this.changeNotifer=changeNotifer;
	}

	public ChangeNotifer getChangeNotifer() {
		return changeNotifer;
	}
	
	

}
