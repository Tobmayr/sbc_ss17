package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import java.util.List;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

/**
 * Interface for BakeRoboter
 */
public interface IBakeRobotService extends IRobotService{
	
	Map<String,Boolean> getIntialState();

    /**
     * Get unbaked products from bake room
     * @param tx Transaction
     * @return List of unbaked products (1 to 5) from bake room or null
     */
    List<Product> getUnbakedProducts(ITransaction tx);

    /**
     * Insert baked products in storage
     * @param nextProduct baked product
     * @param tx Transaction
     * @return true for successful insert or false for exception
     */
    boolean putBakedProductsInStorage(Product nextProduct, ITransaction tx);

}
