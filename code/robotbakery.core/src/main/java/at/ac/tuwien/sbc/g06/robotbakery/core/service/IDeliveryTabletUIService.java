package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.net.URI;

/**
 * interface for delivery customer tablets
 */
public interface IDeliveryTabletUIService extends ITabletUIService{

	/**
	 * gets uri where orders should be send when they are delivered
	 * @return destination uri
	 */
	URI getDeliveryURI();
}
