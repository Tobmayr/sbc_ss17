package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.DeliveryOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * Interface for service robot
 */
public interface IServiceRobotService extends IRobotService {

	/**
	 * get an open order
	 * 
	 * @param tx
	 *            Transaction
	 * @return open order
	 */
	Order getNextOrder(ITransaction tx);

	/**
	 * add product to counter
	 * 
	 * @param product
	 *            product to put in counter
	 * @param tx
	 *            Transaction
	 * @return true for success or false for exception
	 */
	boolean addToCounter(Product product, ITransaction tx);

	/**
	 * get stock numbers from counter
	 * 
	 * @return Map with product names as keys and the amount as values
	 */
	Map<String, Integer> getCounterStock();

	/**
	 * update order
	 * 
	 * @param order
	 *            order to update
	 * @param tx
	 *            Transaction
	 * @return true for success and false for exception
	 */
	boolean updateOrder(Order order, ITransaction tx);

	/**
	 * Put packed order in terminal so that customers can be notified
	 * 
	 * @param packedOrder
	 *            Packed order consists of products
	 * @param tx
	 *            Transaction
	 * @return true for success and false for exception
	 */
	boolean putPackedOrderInTerminal(PackedOrder packedOrder, ITransaction tx);

	/**
	 * get products from storage
	 * 
	 * @param productType
	 *            type of product
	 * @param amount
	 *            amount
	 * @param tx
	 *            Transaction
	 * @return List of products from defined type or null for exception
	 */
	List<Product> getProductsFromStorage(String productType, int amount, ITransaction tx);
	
	List<Product> getProductsFromStorage(int amount, ITransaction tx);
	/**
	 * get products from counter with specified name
	 * 
	 * @param productName
	 *            productName
	 * @param amount
	 *            amount
	 * @param tx
	 *            Transaction
	 * @return List of products from defined type or null for exception
	 */
	List<Product> getProductsFromCounter(String productName, int amount, ITransaction tx);

	/**
	 * Returns a delivery to the counter collection area
	 * 
	 * @param currentOrder
	 * @param tx 
	 * @return
	 */
	boolean returnDeliveryOrder(DeliveryOrder currentOrder, ITransaction tx);
	
	boolean putPrepackageInTerminal(Prepackage prepackage, ITransaction tx);

	int readAllPrepackages();


}
