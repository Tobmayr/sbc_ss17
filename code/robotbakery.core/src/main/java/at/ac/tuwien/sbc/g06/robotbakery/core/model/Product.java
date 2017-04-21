package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.UUID;

public class Product {

	public enum State {
		UNDEFINED, BASE_DOUGH_IN_STORAGE, DOUGH_IN_BAKERY, FINAL_PRODUCT_IN_STORAGE, PRODUCT_IN_COUNTER, PRODUCT_IN_TERMINAL, PRODUCT_SOLD
	}

	private UUID id;
	private String name;
	private State state;

	public Product(String name) {
		super();
		this.name = name;
		id = UUID.randomUUID();

	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
