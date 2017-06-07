package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.Map;

/**
 * Interface for robots
 */
public interface IRobotService {
	Map<String, Boolean> getInitialState();

}
