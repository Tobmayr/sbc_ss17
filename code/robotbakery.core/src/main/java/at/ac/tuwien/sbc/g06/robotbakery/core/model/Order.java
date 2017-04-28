package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;

import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;

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
		Item i = new Item(productName, amount);
		int index = items.indexOf(i);
		if (index != -1) {
			items.set(index, i);
		} else {
			items.add(i);
		}

		totalSum = items.stream().mapToDouble(Item::getCost).sum();

	}

	public void removeItem(Item item) {
		items.remove(item);
		totalSum -= item.getCost();

	}

	public double getTotalSum() {
		return totalSum;
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

		public Item(String productName, int amount) {
			super();
			this.productName = productName;
			this.amount = amount;
			Recipe recipe = RecipeRegistry.getInstance().getRecipeByName(productName);
			if (recipe != null)
				cost = recipe.getPricePerUnit() * amount;
			else
				cost = 0;

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

		@Override
		public int hashCode() {
			return productName.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Item) {
				Item that = (Item) obj;
				return this.productName.equals(that.productName);
			}
			return super.equals(obj);
		}

	}

}
