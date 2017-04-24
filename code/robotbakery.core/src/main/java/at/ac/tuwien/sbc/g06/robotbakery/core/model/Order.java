package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.RecipeRegistry;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public class Order implements Serializable {
	public static final int OPEN = 1;
	public static final int DELIVERED = 2;
	public static final int PAID = 3;
	public static final int UNDELIVERABLE = 4;

	private final UUID id;
	private UUID customerId;
	private UUID serviceRobotId;

	private int State;

	private double totalSum;
	private List<Item> items;

	public Order() {
		this.id = UUID.randomUUID();
		items = new ArrayList<Item>();
	}

	public void addItem(String productName, Integer amount) {
		Recipe recipe = RecipeRegistry.getInstance().getRecipeByName(productName);
		if (recipe != null) {
			double cost = recipe.getPricePerUnit() * amount;
			items.add(new Item(productName, amount, cost));
			totalSum += cost;
		}

	}

	public void removeItem(Item item) {
		items.remove(item);
		totalSum -= item.getCost();

	}

	public double getTotalSum() {
		return totalSum;
	}

	public void setTotalSum(double totalSum) {
		this.totalSum = totalSum;
	}

	public void resetItems() {
		items.clear();
		totalSum = 0;
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

	public UUID getId() {
		return id;
	}

	public List<Item> getItems() {
		return items;
	}
	
	

}
