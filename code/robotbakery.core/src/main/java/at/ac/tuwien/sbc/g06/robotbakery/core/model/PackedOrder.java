package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("serial")
public class PackedOrder implements Serializable {

	private final UUID customerID;
	private final UUID orderID;
	private final List<Product> products;

	public PackedOrder(UUID customerID, UUID orderID) {
		super();
		this.customerID = customerID;
		this.orderID = orderID;
		this.products = new ArrayList<Product>();
	}

	public void addAll(List<Product> products){
		this.products.addAll(products);
	}
	public UUID getCustomerID() {
		return customerID;
	}

	public UUID getOrderID() {
		return orderID;
	}

	public void addProduct(Product product) {
		products.add(product);
	}

	public List<Product> getProducts() {
		return products;
	}

}
