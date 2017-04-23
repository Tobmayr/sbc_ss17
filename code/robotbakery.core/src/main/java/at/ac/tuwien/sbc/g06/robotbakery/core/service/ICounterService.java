package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface ICounterService {

	void addOrder(Order order);

	Order takeNextOrder();
}
