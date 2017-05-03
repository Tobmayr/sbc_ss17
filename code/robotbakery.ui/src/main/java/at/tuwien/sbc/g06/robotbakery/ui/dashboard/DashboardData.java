package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryUIChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardData implements IBakeryUIChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> ingredients = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInStorage = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInCounter = FXCollections.observableArrayList();

	private final Map<String, ItemCount> ingredientsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> counterProductsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> storageProductsCounterMap = new HashMap<>();
	private final Map<ProductState, ObservableList<Product>> stateToProductsMap = new HashMap<ProductState, ObservableList<Product>>();
	private DashboardController controller;

	public DashboardData() {
		Arrays.asList(ProductState.values())
				.forEach(state -> stateToProductsMap.put(state, FXCollections.observableArrayList()));

		SBCConstants.PRODUCTS_NAMES.forEach(p->{
			ItemCount count= new ItemCount(p);
			storageProductsCounterMap.put(p, count);
			productsInStorage.add(count);
			count= new ItemCount(p);
			counterProductsCounterMap.put(p, count);
			productsInCounter.add(count);
		});
		
		FXCollections.sort(productsInCounter);
		FXCollections.sort(productsInStorage);

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

	public Map<ProductState, ObservableList<Product>> getStateToProductsMap() {
		return stateToProductsMap;
	}

	@Override
	public void onOrderAddedOrUpdated(Order order) {
		int index = orders.indexOf(order);
		if (index == -1)
			orders.add(order);
		else
			orders.set(index, order);
	}

	@Override
	public void onProductAddedToStorage(Product product) {

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

	@Override
	public void onProductRemovedFromStorage(Product product) {
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

	}

	@Override
	public void onProductAddedToCounter(Product product) {
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

	@Override
	public void onProductRemovedFromCounter(Product product) {
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
	}

	@Override
	public void onProductAddedToBakeroom(Product product) {
		addOrUpdate(product, stateToProductsMap.get(ProductState.DOUGH_IN_BAKEROOM));

	}

	@Override
	public void onProductRemovedFromBakeroom(Product product) {
		stateToProductsMap.get(ProductState.DOUGH_IN_BAKEROOM).remove(product);

	}

	@Override
	public void onProductAddedToTerminal(Product product) {
		addOrUpdate(product, stateToProductsMap.get(ProductState.PRODUCT_IN_TERMINAL));

	}

	@Override
	public void onProductRemovedFromTerminal(Product product) {
		stateToProductsMap.get(ProductState.PRODUCT_IN_TERMINAL).remove(product);
		addOrUpdate(product, stateToProductsMap.get(ProductState.PRODUCT_SOLD));

	}

	@Override
	public void onIngredientAddedToStorage(Ingredient ingredient) {
		String ingredientName = getIngredientName(ingredient);
		ItemCount count = ingredientsCounterMap.get(ingredientName);
		if (count == null) {
			count = new ItemCount(ingredientName);
			ingredientsCounterMap.put(ingredientName, count);
			ingredients.add(count);
		}
		count.amount++;
		addOrUpdate(count, ingredients);
	}

	@Override
	public void onIngredientRemovedFromStorage(Ingredient ingredient) {
		String ingredientName = getIngredientName(ingredient);
		ItemCount count = ingredientsCounterMap.get(ingredientName);
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				addOrUpdate(count, ingredients);
			} else {
				ingredients.remove(count);
				ingredientsCounterMap.remove(count.itemName);
			}
		}

	}

	private String getIngredientName(Ingredient ingredient) {
		switch (ingredient.getType()) {
		case FLOUR:
			return "Flour (500g)";
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

	@Override
	public void onRobotStart(Class<? extends Robot> robot) {
		controller.onRobotStart(robot);

	}

	@Override
	public void onRobotShutdown(Class<? extends Robot> robot) {
		controller.onRobotShutdown(robot);

	}

	public void setController(DashboardController controller) {
		this.controller = controller;
	}

}
