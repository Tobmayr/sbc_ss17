package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map.Entry;

import javax.print.attribute.standard.PDLOverrideSupported;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.Contribution;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

public class KneadRobot extends Robot {

	private IKneadRobotService service;

	private Product nextProduct;

	public KneadRobot(IKneadRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));
	}

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			doTask(bakeNextProduct);
		}

	}

	ITransactionalTask takeWater = tx -> {
		long time = (long) (nextProduct.getRecipe().getAmount(IngredientType.WATER) / 500d * 2000);
		if (!service.useWaterPipe(time, tx))
			return false;
		return true;
	};

	ITransactionalTask makeNewBaseDough = tx -> {
		int flourAmount = nextProduct.getRecipe().getAmount(IngredientType.FLOUR);
		FlourPack pack = null;
		while (flourAmount > 0) {
			pack = (FlourPack) service.getPackFromStorage(tx);
			if (pack == null)
				return false;
			flourAmount = pack.takeFlour(flourAmount);
		}
		if (pack.getCurrentAmount() > 0) {
			service.putPackInStorage(pack, tx);
		}

		// Take water
		if (!doTask(takeWater))
			return false;
		// Stir dough
		sleepFor(1000, 3000);
		nextProduct.addContribution(getId(), ContributionType.DOUGH_BASE, getClass());
		nextProduct.setTimestamp(new Timestamp(System.currentTimeMillis()));
		return true;
	};

	ITransactionalTask finishBaseDough = tx -> {

		// Get additional ingredients
		for (Entry<IngredientType, Integer> entry : nextProduct.getRecipe().getAdditionalIngredients()) {
			List<Ingredient> ingredients = service.getIngredientsFromStorage(entry.getKey(), entry.getValue(), tx);
			if (ingredients == null || ingredients.size() < entry.getValue())
				return false;
		}

		// Stir dough
		sleepFor(1000, 3000);
		nextProduct.addContribution(getId(), ContributionType.DOUGH_FINAL, getClass());
		return true;
	};

	ITransactionalTask bakeNextProduct = tx -> {
		ProductChooser productChooser = new ProductChooser(service, null);
		if (!productChooser.correctlyInitialized())
			return false;
		nextProduct = productChooser.getFinishableBaseDough();
		if (nextProduct != null) {
			// the product chooser is just read, so we have to take the
			// basedough now for real
			nextProduct = service.getProductFromStorage(nextProduct.getId(), tx);
			if (nextProduct == null)
				return false;
			if (!doTask(finishBaseDough))
				return service.putBaseDoughInStorage(nextProduct, tx);
			return service.putDoughInBakeroom(nextProduct, tx);
		}

		nextProduct = productChooser.getNextProduct();
		if (nextProduct != null) {
			if (doTask(makeNewBaseDough)) {
				Product baseDough = nextProduct;
				if (!doTask(finishBaseDough)) {
					return service.putBaseDoughInStorage(baseDough, tx);
				}
				return service.putDoughInBakeroom(nextProduct, tx);

			}
			return true;
		}

		nextProduct = productChooser.getNextBaseDoughForStorage();
		if (nextProduct != null)
			if (doTask(makeNewBaseDough)) {
				return service.putBaseDoughInStorage(nextProduct, tx);
			}

		return false;

	};

}
