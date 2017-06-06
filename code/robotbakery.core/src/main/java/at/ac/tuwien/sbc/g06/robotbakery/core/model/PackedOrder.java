package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * packed order that is sold to the customer
 */
@SuppressWarnings("serial")
public class PackedOrder extends Order implements Serializable {

	private final List<Product> products;


	public PackedOrder(Order order) {
		super(order.getId());
		this.products = new ArrayList<Product>();

		setCustomerId(order.getCustomerId());
		setServiceRobotId(order.getServiceRobotId());
		setTimestamp(order.getTimestamp());
		order.getItemsMap().values().forEach(i -> addItem(i.getProductName(), i.getAmount()));
		setDelivery(order.isDelivery());
		setDeliveryAddress(order.getDeliveryAddress());
		
	}

	public void addAll(List<Product> products) {
		this.products.addAll(products);
	}

	public UUID getCustomerID() {
		return getCustomerID();
	}

	public UUID getOrderID() {
		return getId();
	}

	public void addProduct(Product product) {
		products.add(product);
	}

	public List<Product> getProducts() {
		return products;
	}

	
}
