package at.ac.tuwien.sbc.g06.robotbakery.core.service;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;

import java.util.List;

public interface IBakeRobotService {

    List<Product> getUnbakedProducts(ITransaction tx);

    boolean putBakedProductsInStorage(Product nextProduct, ITransaction tx);

}
