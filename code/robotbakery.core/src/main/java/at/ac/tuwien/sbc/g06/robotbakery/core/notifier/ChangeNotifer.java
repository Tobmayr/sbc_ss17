package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;

/**
 * class that keeps the registered change listeners in a set, is used for
 * notification
 * 
 * @param <T>
 */
public abstract class ChangeNotifer {
	protected Set<IChangeListener> registeredChangeListeners = new HashSet<>();

	public boolean registerChangeListener(IChangeListener listener) {
		return registeredChangeListeners.add(listener);
	}

	public boolean removeChangeListener(IChangeListener listener) {
		return registeredChangeListeners.remove(listener);
	}
}
 