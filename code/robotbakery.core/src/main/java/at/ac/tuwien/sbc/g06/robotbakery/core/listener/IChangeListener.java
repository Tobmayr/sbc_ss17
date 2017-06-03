package at.ac.tuwien.sbc.g06.robotbakery.core.listener;

import java.io.Serializable;

/**
 * 
 * Listener-Interface which can be used to react to changes in the bakery (i.e.
 * changing objects of the counter,terminal,storage oder bakeroom) Implemented
 * by the GUI to enable automated change-based refreshing.
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public interface IChangeListener   {

	void onObjectChanged(Serializable object, String coordinationRoom, boolean added);

}
