package at.tuwien.sbc.g06.robotbakery.ui.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.actor.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ProductState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class DashboardData implements IBakeryChangeListener {

	private final ObservableList<Order> orders = FXCollections.observableArrayList();
	private final ObservableList<Ingredient> ingredients = FXCollections.observableArrayList();
	private final ObservableList<Product> productsInStorage = FXCollections.observableArrayList();
	private final Map<ProductState, ObservableList<Product>> stateToProductsMap = new HashMap<ProductState, ObservableList<Product>>();
	private final ObservableMap<String, Integer> productAmountInCounterMap = FXCollections.observableHashMap();

	public DashboardData() {
		Arrays.asList(ProductState.values())
				.forEach(state -> stateToProductsMap.put(state, FXCollections.observableArrayList()));

	}

	public ObservableList<Order> getOrders() {
		return orders;
	}

	public ObservableList<Ingredient> getIngredients() {
		return ingredients;
	}

	public ObservableList<Product> getProductsInStorage() {
		return productsInStorage;
	}

	public ObservableMap<String, Integer> getProductAmountInCounterMap() {
		return productAmountInCounterMap;
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
		stateToProductsMap.forEach((state, table) -> {
			Product product = new Product("Kaisersemmel");
			product.setState(state);
			Arrays.asList(ContributionType.values())
					.forEach(ty -> product.addContribution(UUID.randomUUID(), ty, ServiceRobot.class));
			stateToProductsMap.get(state).add(product);

		});
	}

	@Override
	public void onProductAddedToStorage(Product product) {
		productsInStorage.add(product);
		ObservableList<Product> list = stateToProductsMap.get(product.getState());
		list.add(product);

	}

	@Override
	public void onProductRemovedFromStorage(Product product) {
		productsInStorage.remove(product);
		ObservableList<Product> list = stateToProductsMap.get(product.getState());
		list.remove(product);

	}

	@Override
	public void onProductsAddedToCounter(Product product) {
		ObservableList<Product> list = stateToProductsMap.get(product.getState());
		list.add(product);
		Integer amount = productAmountInCounterMap.get(product.getName());
		if (amount == null)
			amount = 0;
		productAmountInCounterMap.put(product.getName(), (amount + 1));

	}

	@Override
	public void onProductRemovedFromCounter(Product product) {
		ObservableList<Product> list = stateToProductsMap.get(product.getState());
		list.remove(product);
		Integer amount = productAmountInCounterMap.get(product.getName());
		if (amount != null)
			productAmountInCounterMap.put(product.getName(), amount--);
	}

	@Override
	public void onIngredientAddedToStorage(Ingredient ingredient) {
		ingredients.add(ingredient);
	}

	@Override
	public void onIngredientRemovedFromStorage(Ingredient ingredient) {
		ingredients.remove(ingredient);

	}

}
