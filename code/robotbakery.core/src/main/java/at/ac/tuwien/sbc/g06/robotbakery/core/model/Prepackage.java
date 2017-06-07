package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * class represents prepacked product package
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Prepackage implements Serializable {

	public static final String STATE_IN_TERMINAL = "in terminal";
	public static final String STATE_SOLD = "sold";
	
	private final UUID id;
	private List<Product> products;
	private UUID serviceRobotId;
	private UUID customerId;
	private double totalSum;
	private boolean sold;
	private String state;

	public Prepackage() {
		super();
		this.products = new ArrayList<>(SBCConstants.PREPACKAGE_SIZE);
		this.id = UUID.randomUUID();
		this.sold = false;
		this.state = STATE_IN_TERMINAL;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products.clear();
		this.products.addAll(products);
		this.totalSum = products.stream().mapToDouble(Product::getPrice).sum();
	}

	public UUID getId() {
		return id;
	}

	public void setServiceRobotId(UUID serviceRobotId) {
		this.serviceRobotId = serviceRobotId;
	}

	public UUID getServiceRobotId() {
		return serviceRobotId;
	}

	public double getTotalSum() {
		return totalSum;
	}

	public boolean isSold() {
		return sold;
	}

	public void setSold(boolean sold) {
		this.sold = sold;
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Prepackage) {
			Prepackage that = (Prepackage) obj;
			return this.getId().equals(that.getId());
		}
		return super.equals(obj);
	}

}
