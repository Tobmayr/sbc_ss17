package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import java.util.UUID;

public abstract class TabletUIChangeNotifer extends ChangeListenerNotifier<ITableUIChangeListener> {


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
	 * Needs to be extendend by each framework-specific implementation (XVSM &
	 * JSM)
	 */

}
