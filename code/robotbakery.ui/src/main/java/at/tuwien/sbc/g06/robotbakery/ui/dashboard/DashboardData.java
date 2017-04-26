package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardData implements IBakeryChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> ingredients = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInStorage = FXCollections.observableArrayList();
	private final ObservableList<ItemCount> productsInCounter = FXCollections.observableArrayList();

	private final Map<String, ItemCount> ingredientsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> counterProductsCounterMap = new HashMap<>();
	private final Map<String, ItemCount> storageProductsCounterMap = new HashMap<>();
	private final Map<ProductState, ObservableList<Product>> stateToProductsMap = new HashMap<ProductState, ObservableList<Product>>();

	public DashboardData() {
		Arrays.asList(ProductState.values())
				.forEach(state -> stateToProductsMap.put(state, FXCollections.observableArrayList()));

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

		ItemCount count = storageProductsCounterMap.get(product.getProductName());
		if (count == null) {
			count = new ItemCount(product.getProductName());
			storageProductsCounterMap.put(product.getProductName(), count);
			productsInStorage.add(count);
		}
		count.amount++;
		updateItemCount(count, productsInStorage);
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromStorage(Product product) {
		ItemCount count = storageProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				updateItemCount(count, productsInStorage);
			} else {
				productsInStorage.remove(count);
				storageProductsCounterMap.remove(count.itemName);
			}
		}

		stateToProductsMap.get(product.getState()).remove(product);

	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		ItemCount count = counterProductsCounterMap.get(product.getProductName());
		if (count == null) {
			count = new ItemCount(product.getProductName());
			counterProductsCounterMap.put(product.getProductName(), count);
			productsInCounter.add(count);
		}
		count.amount++;
		updateItemCount(count, productsInCounter);
		stateToProductsMap.get(product.getState()).add(product);

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		ItemCount count = counterProductsCounterMap.get(product.getProductName());
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				updateItemCount(count, productsInCounter);
			} else {
				productsInCounter.remove(count);
				counterProductsCounterMap.remove(count.itemName);
			}
		}

		stateToProductsMap.get(product.getState()).remove(product);
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
		updateItemCount(count, ingredients);
	}

	@Override
	public void onIngredientRemovedFromStorage(Ingredient ingredient) {
		String ingredientName = getIngredientName(ingredient);
		ItemCount count = ingredientsCounterMap.get(ingredientName);
		if (count != null) {
			if (count.amount > 0) {
				count.amount--;
				updateItemCount(count, ingredients);
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

	private void updateItemCount(ItemCount itemCount, ObservableList<ItemCount> list) {
		int index = list.indexOf(itemCount);
		if (index != -1)
			list.set(index, itemCount);
	}

	/**
	 * Helper class for representing Items and the current stock in the UI (e.g.
	 * Ingredients or Products)
	 * 
	 * @author Tobias Ortmayr (1026279)
	 *
	 */
	public class ItemCount {

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

	}

}
