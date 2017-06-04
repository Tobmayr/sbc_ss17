package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.ContributionType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import ch.qos.logback.core.CoreConstants;

public class KneadRobot extends Robot {

	private IKneadRobotService service;

	private boolean isStorageEmpty = false;
	private Product nextProduct;

	public KneadRobot(IKneadRobotService service, ITransactionManager transactionManager, String id) {
		super(transactionManager, id);
		this.service = service;
		Runtime.getRuntime().addShutdownHook(new Thread(() -> service.shutdownRobot()));
	}

	/**
	 * get ingredients from storage for a recipe to finish a base dough
	 * 
	 * @param tx
	 *            Transaction
	 * @return return true if ingredients are fetched and false for exceptions
	 *         or when there are not enough in the storage
	 */
	private boolean getAdditionalIngredients(ITransaction tx) {
		for (Entry<IngredientType, Integer> entry : nextProduct.getRecipe().getAdditionalIngredients()) {
			List<Ingredient> ingredients = service.getIngredientsFromStorage(entry.getKey(), entry.getValue(), tx);
			if (ingredients == null || ingredients.size() < entry.getValue())
				return false;
		}
		return true;
	}

	/**
	 * use ingredients and base dough to make the final dough and put it in the
	 * bakeroom
	 * 
	 * @param tx
	 *            Transaction
	 * @return true if dough is finished and put in bakeroom, false if exception
	 */
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

	/**
	 * get flour and water for base dough
	 */
	ITransactionalTask getBaseIngredients = tx -> {
		int flourAmount = nextProduct.getRecipe().getAmount(IngredientType.FLOUR);

		if (!service.takeFlourFromStorage(flourAmount, tx)) {
			return false;
		}
		// Take water
		long time = (long) (nextProduct.getRecipe().getAmount(IngredientType.WATER) / 500d * 2000);
		if (!service.useWaterPipe(time, tx))
			return false;
		return true;

	};

	/**
	 * make base dough and emulate working time
	 */
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
		nextProduct.setTimestamp(new Timestamp(System.currentTimeMillis()));

		return addAddtionalIngredientsAndFinsish(tx);

	};

	/**
	 * get base dough from storage and try to finish it with getting the
	 * ingredients
	 */
	ITransactionalTask tryToFinishExistingDough = tx -> {
		debug("Trying to finish basedough");
		nextProduct = service.getProductFromStorage(nextProduct.getId(), tx);
		return nextProduct == null ? false : addAddtionalIngredientsAndFinsish(tx);
	};

	@Override
	public void run() {
		service.startRobot();
		while (!Thread.interrupted()) {
			if (!isStorageEmpty) {
				ProductChooser productChooser = new ProductChooser(service, null);
				if (productChooser.correctlyInitialized()) {
					nextProduct = productChooser.getFinishableBaseDough();
					if (nextProduct != null) {
						doTask(tryToFinishExistingDough);
					} else {
						nextProduct = productChooser.getNextProduct();
						if (nextProduct != null) {
							doTask(tryToMakeDough);
						} else {
							isStorageEmpty = true;
						}

					}

				}
			}

		}

	}

	private void debug(String message) {
		System.out.println(String.format("%s %s", message,
				nextProduct != null ? "(" + nextProduct.getId() + ", " + nextProduct.getProductName() + ")" : ""));
	}

	@Override
	public void onObjectChanged(Serializable object, String coordinationRoom, boolean added) {
		if (added && object instanceof Ingredient && coordinationRoom == SBCConstants.COORDINATION_ROOM_STORAGE)
			isStorageEmpty = false;

	}

}
