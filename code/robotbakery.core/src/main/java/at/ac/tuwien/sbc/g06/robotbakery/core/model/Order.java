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

	public enum OrderState {
		OPEN, DELIVERED, PAID, UNDELIVERABLE;
	}

	private final UUID id;
	private UUID customerId;
	private UUID serviceRobotId;
	private OrderState state;

	private double totalSum;
	private List<Item> items;

	public Order() {
		id = UUID.randomUUID();
		items = new ArrayList<Item>();
		state = OrderState.OPEN;
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

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
	}

	public List<Item> getItems() {
		return items;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", customerId=" + customerId + ", serviceRobotId=" + serviceRobotId + ", state="
				+ state + ", totalSum=" + totalSum + ", items=" + items + "]";
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Order) {
			Order that = (Order) obj;
			return this.getId().equals(that.getId());
		}
		return super.equals(obj);
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
