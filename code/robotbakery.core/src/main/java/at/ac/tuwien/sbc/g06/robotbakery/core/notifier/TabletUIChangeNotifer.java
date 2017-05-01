package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.ITableUIChangeListener;

public abstract class TabletUIChangeNotifer extends ChangeNotifer<ITableUIChangeListener>{


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
