package at.ac.tuwien.sbc.g06.robotbakery.core.service;

/**
 * Interface for bakery
 */
public interface IBakeryService {

	/**
	 * Creates exactly one water pipe object which will be added to the storage
	 * coordination room. If an initProperties file exists in the implementing
	 * class the file will be processed and the objects will be added toe the
	 * coordination rooms accordingly
	 */
	void init();

	/**
	 * 
	 * @param fileName
	 *            Properties filename
	 */
	
}
