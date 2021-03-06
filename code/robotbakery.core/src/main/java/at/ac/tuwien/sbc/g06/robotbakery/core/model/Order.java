package at.ac.tuwien.sbc.g06.robotbakery.core.model;

import java.io.Serializable;
import java.net.URI;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Objects;

import at.ac.tuwien.sbc.g06.robotbakery.core.util.RecipeRegistry;

/**
 * order class
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
@SuppressWarnings("serial")
public class Order implements Serializable {

	public enum OrderState {
		ORDERED, WAITING, PACKED, PAID, DELIVERED, UNDELIVERALBE, UNGRANTABLE;
	}

	private final UUID orderID;
	private UUID customerID;
	private UUID serviceRobotId;
	private OrderState state = OrderState.ORDERED;
	private Timestamp timestamp;

	private boolean highPriority = false;
	private boolean delivery = false;
	private URI deliveryAddress;
	private UUID deliveryRobotId;

	private double totalSum;
	private final Map<String, Item> itemsMap;

	public Order() {
		this(UUID.randomUUID());
	}

	public Order(UUID orderID) {
		this.orderID = orderID;
		itemsMap = new HashMap<>();
	}

	/**
	 * add an item to order
	 * 
	 * @param productName
	 *            name of the added product
	 * @param amount
	 *            amount of items
	 * @return return the created and added item
	 */
	public Item addItem(String productName, Integer amount) {
		Item item = new Item(productName, amount);
		itemsMap.put(productName, item);

		totalSum = itemsMap.values().stream().mapToDouble(Item::getCost).sum();
		return item;
	}

	
	public boolean isHighPriority() {
		return highPriority;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setTotalSum(double totalSum) {
		this.totalSum = totalSum;
	}

	public UUID getDeliveryRobotId() {
		return deliveryRobotId;
	}

	public void setDeliveryRobotId(UUID deliveryRobotId) {
		this.deliveryRobotId = deliveryRobotId;
	}

	public Item removeItem(String productName) {
		Item item = itemsMap.remove(productName);
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

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public URI getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(URI deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
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

	/**
	 * item subclass, item represents a product in order
	 */
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
