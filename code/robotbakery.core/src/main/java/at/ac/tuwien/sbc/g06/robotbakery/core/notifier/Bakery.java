package at.ac.tuwien.sbc.g06.robotbakery.core.notifier;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.IntStream;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.IBakeryUIChangeListener;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

/**
 * Class which represents the bakery. Is used for initialization purposes,
 * serves as ChangeListenerNotifer;
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public abstract class Bakery extends ChangeNotifer<IBakeryUIChangeListener> implements IBakeryService {
	private Properties initProperties;

	/**
	 * Needs to be extendend by each framework-specific implementation (XVSM &
	 * JMS)
	 */

	public Bakery() {
	}

	public Bakery(Properties initProperties) {
		this.initProperties = initProperties;
	}

	@Override
	public void init() {
		if (initProperties != null) {
			loadTestData();

		}
	}

	private void loadTestData() {
		UUID kneadRobotID = UUID.fromString("11111111-8cf0-11bd-b23e-10b96e4ef00d");
		UUID serviceRobotID = UUID.fromString("22222222-8cf0-11bd-b23e-10b96e4ef00d");
		UUID bakeRobotID = UUID.fromString("33333333-8cf0-11bd-b23e-10b96e4ef00d");
		List<Serializable> forStorage = new ArrayList<>();
		List<Product> forCounter = new ArrayList<>();
		List<Product> forBakeroom = new ArrayList<>();
		SBCConstants.Keys.ALL_STORAGE_BASE.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.DOUGH);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					forStorage.add(product);
				});

			}
		});

		SBCConstants.Keys.ALL_STORAGE_FINAL.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.FINALPRODUCT);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_FINAL, KneadRobot.class);
					product.addContribution(bakeRobotID, ContributionType.BAKE, BakeRobot.class);
					forCounter.add(product);
				});

			}
		});

		SBCConstants.Keys.ALL_INGREDIENTS.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Ingredient ingredient;
					if (key.equals(SBCConstants.Keys.DOUGH)) {
						ingredient = new FlourPack();
					} else {
						ingredient = new Ingredient(getType(key));
					}
					forStorage.add(ingredient);
				});

			}
		});

		SBCConstants.Keys.ALL_COUNTER.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.FINALPRODUCT);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_FINAL, KneadRobot.class);
					product.addContribution(serviceRobotID, ContributionType.TRANSFER_TO_COUNTER, ServiceRobot.class);
					forStorage.add(product);
				});

			}
		});

		SBCConstants.Keys.ALL_BAKEROOM.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.DOUGH);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_FINAL, KneadRobot.class);
					forBakeroom.add(product);
				});

			}
		});
		addItemsToStorage(forStorage);
		addProductsToCounter(forCounter);
		addProductsToBakeroom(forBakeroom);

	}

	private IngredientType getType(String key) {
		switch (key) {
		case SBCConstants.Keys.EGGS:
			return IngredientType.FLOUR;
		case SBCConstants.Keys.BAKEMIX_SPICY:
			return IngredientType.BAKING_MIX_SPICY;
		case SBCConstants.Keys.BAKEMIX_SWEET:
			return IngredientType.BAKING_MIX_SWEET;
		default:
			return null;
		}
	}

	private String getName(String key) {
		if (key.endsWith(".product1"))
			return SBCConstants.PRODUCT1_NAME;
		if (key.endsWith(".product2"))
			return SBCConstants.PRODUCT2_NAME;
		if (key.endsWith(".product3"))
			return SBCConstants.PRODUCT3_NAME;
		if (key.endsWith(".product4"))
			return SBCConstants.PRODUCT4_NAME;
		if (key.endsWith(".product5"))
			return SBCConstants.PRODUCT5_NAME;
		return "";
	}

	private int getAmount(String key) {
		String value = initProperties.getProperty(key, "0");
		return Integer.parseInt(value);
	}

}
