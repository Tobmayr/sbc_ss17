package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.util.UUID;

/**
 * change listener implementation for customer tablet
 */
public abstract class TabletUIChangeNotifer extends ChangeNotifer{


	private UUID customerID;

	public TabletUIChangeNotifer() {
		super();
	
	}

	public UUID getCustomerID() {
		return customerID;
	}

	public void setCustomerID(UUID customerID) {
		this.customerID=customerID;
		
	}

	
	/**
	 * Needs to be extended by each framework-specific implementation (XVSM &
	 * JSM)
	 */

}
