package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import java.util.UUID;

public abstract class TabletUIChangeNotifer extends ChangeListenerNotifier<ITableUIChangeListener> {

	private final UUID customerID;

	public TabletUIChangeNotifer(UUID customerID) {
		super();
		this.customerID = customerID;
	}

	public UUID getCustomerID() {
		return customerID;
	}

	
	/**
	 * Needs to be extendend by each framework-specific implementation (XVSM &
	 * JSM)
	 */

}
