package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;

public abstract class Bakery {
	
	protected Set<IBakeryChangeListener> registeredChangeListeners;
	
	public Bakery() {
		registeredChangeListeners= new HashSet<>();
	}		
	
	public boolean registerChangeListener(IBakeryChangeListener listener) {
		return registeredChangeListeners.add(listener);
	}

	public boolean removeChangeListener(IBakeryChangeListener listener) {
		return registeredChangeListeners.remove(listener);
	}
}
