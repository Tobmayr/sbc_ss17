package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;

/**
 * class that keeps the registered change listeners in a set, is used for notification
 * @param <T>
 */
public abstract class ChangeNotifer<T extends IChangeListener> {
	protected Set<T> registeredChangeListeners = new HashSet<>();

	public boolean registerChangeListener(T listener) {
		return registeredChangeListeners.add(listener);
	}

	public boolean removeChangeListener(T listener) {
		return registeredChangeListeners.remove(listener);
	}
}
