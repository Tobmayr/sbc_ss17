package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Order extends Entity {
	public static final int OPEN = 1;
	public static final int DELIVERED = 2;
	public static final int PAID = 3;
	public static final int UNDELIVERABLE = 4;

	private Map<Recipe, Integer> items;

	private int State;
	private UUID customerId;
	private UUID serviceRobotId;

	public Order() {
		super();
		items = new HashMap<Recipe, Integer>();
	}

	public void addItem(String productName, Integer amount) {

		items.put(RecipeRegistry.INSTANCE.getRecipeByName(productName), amount);
	}

	public void resetItems() {
		items.clear();
	}

	private double sum;

	public double totalSum() {
		sum = 0;
		items.forEach((recipe, count) -> sum += recipe.getPricePerUnit() * count);
		return sum;
	}

	public Integer removeItem(String productName) {
		return items.remove(productName);
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}

	public UUID getServiceRobotId() {
		return serviceRobotId;
	}

	public void setServiceRobotId(UUID serviceRobotId) {
		this.serviceRobotId = serviceRobotId;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

}
