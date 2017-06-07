package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.NotificationMessage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order.OrderState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Prepackage;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.INotificationService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardData implements IChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<Prepackage> prepackages = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> ingredients = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInStorage = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInCounter = FXCollections.observableArrayList();

	private final Map<String, ItemCount> ingredientsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> counterProductsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> storageProductsCounterMap = new HashMap<>();
	private final Map<ProductState, ObservableList<Product>> stateToProductsMap = new HashMap<ProductState, ObservableList<Product>>();
	private INotificationService notificationSerivce;

	public DashboardData(INotificationService notificationService) {
		this.notificationSerivce = notificationService;
		Arrays.asList(ProductState.values())
				.forEach(state -> stateToProductsMap.put(state, FXCollections.observableArrayList()));

		FXCollections.sort(productsInCounter);
		FXCollections.sort(productsInStorage);

		Arrays.asList(IngredientType.values()).stream().filter(i -> i != IngredientType.WATER).forEach(i -> {
			String name = getIngredientName(new Ingredient(i));
			ItemCount count = new ItemCount(name);
			ingredientsCounterMap.put(name, count);
			ingredients.add(count);
			FXCollections.sort(ingredients);
		});
	}

	public ObservableList<Order> getOrders() {
		return orders;
	}

	public ObservableList<ItemCount> getIngredients() {
		return ingredients;
	}

	public ObservableList<ItemCount> getProductsInStorage() {
		return productsInStorage;
	}

	public ObservableList<ItemCount> getProductsInCounter() {
		return productsInCounter;
	}

	public ObservableList<Prepackage> getPrepackages() {
		return prepackages;
	}

	public Map<ProductState, ObservableList<Product>> getStateToProductsMap() {
		return stateToProductsMap;
	}

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (object instanceof Product) {
			Product product = (Product) object;
			switch (coordinationRoom) {
			case SBCConstants.COORDINATION_ROOM_BAKEROOM:
				if (added)
					onProductAddedToBakeroom(product);
				else
					onProductRemovedFromBakeroom(product);
				break;
			case SBCConstants.COORDINATION_ROOM_COUNTER:
				if (added)
					onProductAddedToCounter(product);
				else
					onProductRemovedFromCounter(product);
				break;
			case SBCConstants.COORDINATION_ROOM_STORAGE:
				if (added)
					onProductAddedToStorage(product);
				else
					onProductRemovedFromStorage(product);
				break;
			case SBCConstants.COORDINATION_ROOM_TERMINAL:
				if (added)
					onProductAddedToTerminal(product);
				else
					onProductRemovedFromTerminal(product);
				break;
			default:
				break;
			}

		} else if (object instanceof Ingredient) {
			Ingredient ingredient = (Ingredient) object;
			if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_STORAGE)) {
				if (added)
					onIngredientAddedToStorage(ingredient);
				else
					onIngredientRemovedFromStorage(ingredient);
			}
		} else if (object instanceof Order) {
			Order order = (Order) object;
			if (added) {
				onOrderAddedOrUpdated(order);
				if (order instanceof PackedOrder) {
					((PackedOrder) order).getProducts().forEach(p -> {
						if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_TERMINAL)) {
							onProductRemovedFromCounter(p);
							onProductAddedToTerminal(p);
						} else if (coordinationRoom.equals(SBCConstants.COORDINATION_ROOM_COUNTER)) {
							onProductRemovedFromTerminal(p);
						}

					});
				}
			}
		} else if (object instanceof Prepackage) {
			Prepackage prepackage = (Prepackage) object;
			onPrepackageAdded(prepackage);

		}

	}

	private void onPrepackageAdded(Prepackage prepackage) {
		int index = prepackages.indexOf(prepackage);
		if (index == -1)
			prepackages.add(prepackage);
		else
			prepackages.set(index, prepackage);
		if (prepackages.size() == SBCConstants.PREPACKAGE_MAX_AMOUNT) {
			notificationSerivce.sendNotification(new NotificationMessage(NotificationMessage.PREPACKAGE_LIMIT_REACHED),
					null);
		}

	}

	private void onOrderAddedOrUpdated(Order order) {
		int index = orders.indexOf(order);
		if (index == -1)
			orders.add(order);
		else
			orders.set(index, order);

		if (!orders.stream()
				.anyMatch((o) -> o.getState() == OrderState.ORDERED || o.getState() == OrderState.WAITING)) {
			notificationSerivce.sendNotification(new NotificationMessage(NotificationMessage.NO_MORE_ORDERS), null);
		}
	}

	private void onProductAddedToStorage(Product product) {

		ItemCount count = storageProductsCounterMap.get(toFullProductName(product));
		if (count == null) {
			count = new ItemCount(toFullProductName(product));
			storageProductsCounterMap.put(toFullProductName(product), count);
			productsInStorage.add(count);
		}
		count.amount++;
		addOrUpdate(count, productsInStorage);
		ProductState state = product.getType() == BakeState.DOUGH ? ProductState.DOUGH_IN_STORAGE
				: ProductState.PRODUCT_IN_STORAGE;

		addOrUpdate(product, stateToProductsMap.get(state));

	}

	private void onProductRemovedFromStorage(Product product) {
		ItemCount count = storageProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				addOrUpdate(count, productsInStorage);
			} else {
				productsInStorage.remove(count);
				storageProductsCounterMap.remove(count.itemName);
			}
		}

		ProductState state = product.getType() == BakeState.DOUGH ? ProductState.DOUGH_IN_STORAGE
				: ProductState.PRODUCT_IN_STORAGE;
		stateToProductsMap.get(state).remove(product);

		if (stateToProductsMap.get(ProductState.PRODUCT_IN_STORAGE).isEmpty()) {
			notificationSerivce
					.sendNotification(new NotificationMessage(NotificationMessage.NO_MORE_PRODUCTS_IN_STORAGE), null);
		}

	}

	private void onProductAddedToCounter(Product product) {
		ItemCount count = counterProductsCounterMap.get(product.getProductName());
		if (count == null) {
			count = new ItemCount(product.getProductName());
			counterProductsCounterMap.put(product.getProductName(), count);
			productsInCounter.add(count);
		}
		count.amount++;
		addOrUpdate(count, productsInCounter);

		addOrUpdate(product, stateToProductsMap.get(ProductState.PRODUCT_IN_COUNTER));

	}

	private void onProductRemovedFromCounter(Product product) {
		ItemCount count = counterProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				addOrUpdate(count, productsInCounter);
			} else {
				productsInCounter.remove(count);
				counterProductsCounterMap.remove(count.itemName);
			}
		}

		stateToProductsMap.get(ProductState.PRODUCT_IN_COUNTER).remove(product);
		if (stateToProductsMap.get(ProductState.PRODUCT_IN_COUNTER).isEmpty()) {
			notificationSerivce
					.sendNotification(new NotificationMessage(NotificationMessage.NO_MORE_PRODUCTS_IN_COUNTER), null);
		}
	}

	private void onProductAddedToBakeroom(Product product) {
		addOrUpdate(product, stateToProductsMap.get(ProductState.DOUGH_IN_BAKEROOM));

	}

	private void onProductRemovedFromBakeroom(Product product) {
		stateToProductsMap.get(ProductState.DOUGH_IN_BAKEROOM).remove(product);
		if (stateToProductsMap.get(ProductState.DOUGH_IN_BAKEROOM).isEmpty()) {
			notificationSerivce
					.sendNotification(new NotificationMessage(NotificationMessage.NO_MORE_PRODUCTS_IN_BAKEROOM), null);
		}

	}

	private void onProductAddedToTerminal(Product product) {
		addOrUpdate(product, stateToProductsMap.get(ProductState.PRODUCT_IN_TERMINAL));

	}

	private void onProductRemovedFromTerminal(Product product) {
		stateToProductsMap.get(ProductState.PRODUCT_IN_TERMINAL).remove(product);
		addOrUpdate(product, stateToProductsMap.get(ProductState.PRODUCT_SOLD));

	}

	private void onIngredientAddedToStorage(Ingredient ingredient) {
		String ingredientName = getIngredientName(ingredient);
		ItemCount count = ingredientsCounterMap.get(ingredientName);
		if (count == null) {
			count = new ItemCount(ingredientName);
			ingredientsCounterMap.put(ingredientName, count);
			ingredients.add(count);
		}
		if (ingredient instanceof FlourPack) {
			FlourPack pack = (FlourPack) ingredient;
			count.amount += pack.getCurrentAmount();
		} else {
			count.amount++;
		}

		addOrUpdate(count, ingredients);
	}

	private void onIngredientRemovedFromStorage(Ingredient ingredient) {
		String ingredientName = getIngredientName(ingredient);
		ItemCount count = ingredientsCounterMap.get(ingredientName);
		if (count != null) {
			if (count.amount > 0) {
				if (ingredient instanceof FlourPack) {
					FlourPack pack = (FlourPack) ingredient;
					count.amount -= pack.getCurrentAmount();
				} else {
					count.amount--;
				}

				addOrUpdate(count, ingredients);
			} else {
				ingredients.remove(count);
				ingredientsCounterMap.remove(count.itemName);
			}

			if (!ingredientsCounterMap.values().stream().anyMatch((i) -> i.getAmount() > 0)) {
				notificationSerivce.sendNotification(
						new NotificationMessage(NotificationMessage.NO_MORE_INGREDIENTS_IN_STORAGE), null);
			}
		}

	}

	private String getIngredientName(Ingredient ingredient) {
		switch (ingredient.getType()) {
		case FLOUR:
			return "Flour (in g)";
		case EGGS:
			return "Eggs";
		case BAKING_MIX_SPICY:
			return "Bakingmix Spicy";
		case BAKING_MIX_SWEET:
			return "Bakingmix Sweet";
		default:
			return "";
		}

	}

	private String toFullProductName(Product product) {
		String suffix = product.getType() == BakeState.DOUGH ? " (Base dough)" : "";
		return product.getProductName() + suffix;
	}

	private <T> void addOrUpdate(T element, ObservableList<T> observableList) {
		int index = observableList.indexOf(element);
		if (index != -1) {
			observableList.set(index, element);
		} else {
			observableList.add(element);
		}

	}

	/**
	 * Helper class for representing Items and the current stock in the UI (e.g.
	 * Ingredients or Products)
	 * 
	 * @author Tobias Ortmayr (1026279)
	 *
	 */
	public class ItemCount implements Comparable<ItemCount> {

		private final String itemName;
		private int amount;

		public ItemCount(String itemName) {
			this.itemName = itemName;
		}

		public String getItemName() {
			return itemName;
		}

		public int getAmount() {
			return amount;
		}

		@Override
		public int compareTo(ItemCount that) {
			return this.itemName.compareTo(that.itemName);
		}

	}

	public enum ProductState {
		DOUGH_IN_STORAGE, DOUGH_IN_BAKEROOM, PRODUCT_IN_STORAGE, PRODUCT_IN_COUNTER, PRODUCT_IN_TERMINAL, PRODUCT_SOLD;
	}

}
