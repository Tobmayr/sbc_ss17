package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.RecipeRegistry;

/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Order implements Serializable {
	public static final int OPEN = 1;
	public static final int DELIVERED = 2;
	public static final int PAID = 3;
	public static final int UNDELIVERABLE = 4;

	private final UUID id;
	private UUID customerId;
	private UUID serviceRobotId;

	private int state;

	private double totalSum;
	private List<Item> items;

	public Order() {
		this.id = UUID.randomUUID();
		items = new ArrayList<Item>();
		state = OPEN;
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
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public UUID getId() {
		return id;
	}

	public List<Item> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", customerId=" + customerId + ", serviceRobotId=" + serviceRobotId + ", state="
				+ state + ", totalSum=" + totalSum + ", items=" + items + "]";
	}

	public class Item implements Serializable {
		private final String productName;
		private final int amount;
		private final double cost;

		public Item(String productName, int amount, double cost) {
			super();
			this.productName = productName;
			this.amount = amount;
			this.cost = cost;
		}

		public String getProductName() {
			return productName;
		}

		public int getAmount() {
			return amount;
		}

		public double getCost() {
			return cost;
		}

	}

}
