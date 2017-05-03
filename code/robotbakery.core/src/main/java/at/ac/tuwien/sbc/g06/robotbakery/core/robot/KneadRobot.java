package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
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

	private boolean getAdditionalIngredients(ITransaction tx) {
		for (Entry<IngredientType, Integer> entry : nextProduct.getRecipe().getAdditionalIngredients()) {
			List<Ingredient> ingredients = service.getIngredientsFromStorage(entry.getKey(), entry.getValue(), tx);
			if (ingredients == null || ingredients.size() < entry.getValue())
				return false;
		}
		return true;
	}

	private boolean addAddtionalIngredientsAndFinsish(ITransaction tx) {
		// If we don't have enough ingredients for finishing, put it in storage
		if (!getAdditionalIngredients(tx)) {
			debug("Not enough additional ingredients -> put dough in storage");
			return service.putBaseDoughInStorage(nextProduct, tx);
		}
		// Stir dough
		sleepFor(1000, 3000);

		// Final dough finished, Add contribution tag and put in bakeroom
		nextProduct.addContribution(getId(), ContributionType.DOUGH_FINAL, getClass());
		debug("Dough finished -> prepare for baking");
		return service.putDoughInBakeroom(nextProduct, tx);
	}

	ITransactionalTask getBaseIngredients = tx -> {
		int flourAmount = nextProduct.getRecipe().getAmount(IngredientType.FLOUR);
		// Take flour
//		FlourPack pack = null;
//		while (flourAmount > 0) {
//			pack = (FlourPack) service.getPackFromStorage(tx);
//			if (pack == null)
//				return false;
//			flourAmount = pack.takeFlour(flourAmount);
//		}
//		if (pack.getCurrentAmount() > 0) {
//			service.putPackInStorage(pack, tx);
//		}
		
		service.takeFlourFromStorage(flourAmount, tx);
		// Take water
		long time = (long) (nextProduct.getRecipe().getAmount(IngredientType.WATER) / 500d * 2000);
		if (!service.useWaterPipe(time, tx))
			return false;
		return true;

	};

	ITransactionalTask tryToMakeDough = tx -> {
		debug("Trying to make new fresh dough");
		// Taking base ingredients is a own subtask, so that the transaction can
		// get committed as soon as possible, to free up the water resource
		if (!doTask(getBaseIngredients)) {
			debug("Not enough base ingredients");
			return false;
		}

		// Stir dough
		sleepFor(1000, 3000);
		// base dough is ready-> add contribution tag.
		nextProduct.addContribution(getId(), ContributionType.DOUGH_BASE, getClass());

		return addAddtionalIngredientsAndFinsish(tx);

	};

	ITransactionalTask tryToFinishExistingDough = tx -> {
		debug("Trying to finish basedough");
		nextProduct = service.getProductFromStorage(nextProduct.getId(), tx);
		return nextProduct == null ? false : addAddtionalIngredientsAndFinsish(tx);
	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			ProductChooser productChooser = new ProductChooser(service, null);
			if (productChooser.correctlyInitialized()) {
				nextProduct = productChooser.getFinishableBaseDough();
				if (nextProduct != null) {
					doTask(tryToFinishExistingDough);
				} else {
					nextProduct = productChooser.getNextProduct();
					if (nextProduct != null)
						doTask(tryToMakeDough);
				}

			}

		}

	}

	private void debug(String message) {
		System.out.println(String.format("%s %s", message,
				nextProduct != null ? "(" + nextProduct.getId() + ", " + nextProduct.getProductName() + ")" : ""));
	}

}
