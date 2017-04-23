package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Message;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface ICustomerService {

	void addOrderToCounter(Order order);

	Message takeMessageFromTerminal();

}
