package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
	
	private final UUID orderID;
	private UUID customerID;
	private UUID serviceRobotId;
	private OrderState state=OrderState.OPEN;
	private Timestamp timestamp; 

	private double totalSum;
	private final Map<String,Item> itemsMap;

	public Order() {
		orderID = UUID.randomUUID();
		itemsMap = new HashMap<>();
	}

	public Item addItem(String productName, Integer amount) {
		Item item = new Item(productName, amount);
		itemsMap.put(productName, item);

		totalSum = itemsMap.values().stream().mapToDouble(Item::getCost).sum();
		return item;
	}

	
	public Timestamp getTimestamp(){
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setTotalSum(double totalSum) {
		this.totalSum = totalSum;
	}

	public Item removeItem(String productName) {
		Item item=itemsMap.remove(productName);
		totalSum -= item.getCost();
		return item;

	}

	public double getTotalSum() {
		return totalSum;
	}

	public void resetItems() {
		itemsMap.clear();
		totalSum = 0;
	}

	public UUID getCustomerId() {
		return customerID;
	}

	public void setCustomerId(UUID customerId) {
		this.customerID = customerId;
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


	public Map<String, Item> getItemsMap() {
		return itemsMap;
	}


	public UUID getId() {
		return orderID;
	}

	

	@Override
	public String toString() {
		return "Order [id=" + orderID + ", customerId=" + customerID + ", serviceRobotId=" + serviceRobotId + ", state="
				+ state + ", totalSum=" + totalSum + ", itemsMap=" + itemsMap + "]";
	}

	@Override
	public int hashCode() {
		return orderID.hashCode();
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

