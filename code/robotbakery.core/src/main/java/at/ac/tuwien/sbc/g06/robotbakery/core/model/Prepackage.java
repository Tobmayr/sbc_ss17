package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

@SuppressWarnings("serial")
public class Prepackage implements Serializable {

	private final UUID id;
	private List<Product> products = new ArrayList<>(SBCConstants.PREPACKAGE_SIZE);
	private UUID serviceRobotId;

	public Prepackage() {
		super();
		this.id = UUID.randomUUID();
	}

	public boolean addProducts(Collection<Product> products) {
		return this.products.addAll(products);
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public UUID getId() {
		return id;
	}

	public void setServiceRobotId(UUID serviceRobotId) {
		this.serviceRobotId=serviceRobotId;
	}

	public UUID getServiceRobotId() {
		return serviceRobotId;
	}

	
}
