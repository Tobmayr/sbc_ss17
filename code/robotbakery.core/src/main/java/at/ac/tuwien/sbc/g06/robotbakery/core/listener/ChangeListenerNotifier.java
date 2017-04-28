package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import java.util.HashSet;
import java.util.Set;

public abstract class ChangeListenerNotifier<L extends IChangeListener> {
	protected Set<L> registeredChangeListeners = new HashSet<>();

	public boolean registerChangeListener(L listener) {
		return registeredChangeListeners.add(listener);
	}

	public boolean removeChangeListener(L listener) {
		return registeredChangeListeners.remove(listener);
	}
}
