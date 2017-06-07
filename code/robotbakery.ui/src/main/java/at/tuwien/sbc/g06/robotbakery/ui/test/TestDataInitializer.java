package at.tuwien.sbc.g06.robotbakery.ui.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.IntStream;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeryUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;

public class TestDataInitializer {

	private IBakeryUIService service;
	private Properties initProperties;
	private File file;

	public TestDataInitializer(IBakeryUIService service) {
		this.service = service;
	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			properties.load(input);
			input.close();
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean initFromProperties() {
		initProperties = loadProperties();
		if (initProperties != null) {
			loadTestData();
			return true;
		}
		return false;
	}

	/**
	 * Convenience helper method which enables to load an abitary bakery state
	 * (products, ingredients, counter etc.) from a properties file to speed up
	 * testing.
	 * 
	 * 
	 */
	private void loadTestData() {

		UUID kneadRobotID = UUID.fromString("11111111-8cf0-11bd-b23e-10b96e4ef00d");
		UUID serviceRobotID = UUID.fromString("22222222-8cf0-11bd-b23e-10b96e4ef00d");
		UUID bakeRobotID = UUID.fromString("33333333-8cf0-11bd-b23e-10b96e4ef00d");
		List<Serializable> forStorage = new ArrayList<>();
		List<Product> forCounter = new ArrayList<>();
		List<Product> forBakeroom = new ArrayList<>();
		SBCConstants.TestDataKeys.ALL_STORAGE_BASE.forEach(key -> {
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

		SBCConstants.TestDataKeys.ALL_STORAGE_FINAL.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.FINALPRODUCT);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_FINAL, KneadRobot.class);
					product.addContribution(bakeRobotID, ContributionType.BAKE, BakeRobot.class);
					forStorage.add(product);
				});

			}
		});

		SBCConstants.TestDataKeys.ALL_INGREDIENTS.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Ingredient ingredient;
					if (key.equals(SBCConstants.TestDataKeys.FLOUR)) {
						ingredient = new FlourPack();
					} else {
						ingredient = new Ingredient(getType(key));
					}
					forStorage.add(ingredient);
				});

			}
		});

		SBCConstants.TestDataKeys.ALL_COUNTER.forEach(key -> {
			int amount = getAmount(key);
			if (amount > 0) {
				IntStream.range(0, amount).forEach(i -> {
					Product product = new Product(getName(key));
					product.setTimestamp(new Timestamp(System.currentTimeMillis()));
					product.setType(BakeState.FINALPRODUCT);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_BASE, KneadRobot.class);
					product.addContribution(kneadRobotID, ContributionType.DOUGH_FINAL, KneadRobot.class);
					product.addContribution(serviceRobotID, ContributionType.TRANSFER_TO_COUNTER, ServiceRobot.class);
					forCounter.add(product);
				});

			}
		});

		SBCConstants.TestDataKeys.ALL_BAKEROOM.forEach(key -> {
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
		service.addItemsToStorage(forStorage);
		service.addProductsToCounter(forCounter);
		service.addProductsToBakeroom(forBakeroom);

	}

	private IngredientType getType(String key) {
		switch (key) {
		case SBCConstants.TestDataKeys.EGGS:
			return IngredientType.EGGS;
		case SBCConstants.TestDataKeys.BAKEMIX_SPICY:
			return IngredientType.BAKING_MIX_SPICY;
		case SBCConstants.TestDataKeys.BAKEMIX_SWEET:
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

	public void setFile(File file) {
		this.file = file;

	}

}
