package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.IBakeRoomCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.ICounterCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.IStorageCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.ITerminalCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.RecipeRegistry;

public abstract class Bakery {
	private static Logger logger = Logger.getLogger(Bakery.class);
	private ExecutorService executor = Executors.newCachedThreadPool();

	private ITerminalCoordinator terminal;
	private ICounterCoordinator counter;
	private IBakeRoomCoordinator bakeRoom;
	private IStorageCoordinator storage;
	private Map<UUID, Robot> activeRobots;

	public Bakery(ITerminalCoordinator terminal, ICounterCoordinator counter, IBakeRoomCoordinator bakeRoom,
			IStorageCoordinator storage) {
		super();
		this.terminal = terminal;
		this.counter = counter;
		this.bakeRoom = bakeRoom;
		this.storage = storage;
		initializeRecipes();

	}

	private void initializeRecipes() {
		// Kaisersemmel Recipe
		Recipe recipe = new Recipe("Kaisersemmel");
		recipe.addIngredient(Ingredient.FLOUR, 100);
		recipe.addIngredient(Ingredient.WATER, 100);
		recipe.addIngredient(Ingredient.BACKING_MIX_SPICY, 1);
		RecipeRegistry.INSTANCE.registerRecipe(recipe, true);

		// Bauernnrot Recipe
		recipe = new Recipe("Bauernbrot");
		recipe.addIngredient(Ingredient.FLOUR, 550);
		recipe.addIngredient(Ingredient.WATER, 500);
		recipe.addIngredient(Ingredient.BACKING_MIX_SPICY, 5);
		RecipeRegistry.INSTANCE.registerRecipe(recipe, true);

		// Marmorkuchen Recipe
		recipe = new Recipe("Marmorkuchen");
		recipe.addIngredient(Ingredient.FLOUR, 350);
		recipe.addIngredient(Ingredient.WATER, 550);
		recipe.addIngredient(Ingredient.EGGS, 5);
		recipe.addIngredient(Ingredient.BACKING_MIX_SWEET, 2);
		RecipeRegistry.INSTANCE.registerRecipe(recipe, true);

	}

	public Bakery() {
		activeRobots = new HashMap<UUID, Robot>();
	}

	public UUID addRobot(Class<? extends Robot> type) {
		Robot robot;
		try {
			robot = type.newInstance();
			executor.submit(robot);
			activeRobots.put(robot.getId(), robot);
			return robot.getId();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e);
			return null;
		}

	}

	public boolean removeRobot(Robot robot) {
		if (activeRobots.remove(robot.getId(), robot)) {
			robot.setStop(true);
			return true;
		}
		return false;
	}

	public boolean removeRobot(UUID id) {
		Robot robot = activeRobots.remove(id);
		if (robot != null) {
			robot.setStop(true);
			return true;
		}
		return false;

	}

	public void dispose() {
		if (executor != null && !executor.isTerminated()) {
			executor.shutdownNow();
		}

	}

	public IBakeRoomCoordinator getBakeRoom() {
		return bakeRoom;
	}

	public void setBakeRoom(IBakeRoomCoordinator bakeRoom) {
		this.bakeRoom = bakeRoom;
	}

	public ITerminalCoordinator getTerminal() {
		return terminal;
	}

	public ICounterCoordinator getCounter() {
		return counter;
	}

	public IStorageCoordinator getStorage() {
		return storage;
	}

}
