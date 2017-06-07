package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.Map;

/**
 * Interface for robots
 */
public interface IRobotService {

	/**
	 * current state of the bakery is delivered to robots
	 * @return returns a map with notification strings as key and a boolean value
	 */
	Map<String, Boolean> getInitialState();

}
