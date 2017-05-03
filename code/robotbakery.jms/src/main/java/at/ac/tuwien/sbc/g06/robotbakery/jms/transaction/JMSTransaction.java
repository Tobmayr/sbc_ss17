package at.ac.tuwien.sbc.g06.robotbakery.jms.transaction;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * Dummy class, just needed to be conform to the interface. (Because JMS doesnt
 * use transactions per se and manages that via session)
 * 
 * @author Tobias Ortmayr,1026279
 *
 */
public class JMSTransaction implements ITransaction {

	public JMSTransaction() {

	}

}
