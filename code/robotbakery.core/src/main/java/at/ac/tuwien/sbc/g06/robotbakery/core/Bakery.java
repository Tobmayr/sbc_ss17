package at.ac.tuwien.sbc.g06.robotbakery.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Actor;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Customer;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.IBakeRoomCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.ICounterCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.IStorageCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.coordination.ITerminalCoordinator;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.RecipeRegistry;
/**
 * 
 * @author Tobias Ortmayr (1026279)
 *
 */
public abstract class Bakery {
	private static Logger logger = Logger.getLogger(Bakery.class);
	private ExecutorService executor = Executors.newCachedThreadPool();

	private ITerminalCoordinator terminal;
	private ICounterCoordinator counter;
	private IBakeRoomCoordinator bakeRoom;
	private IStorageCoordinator storage;
	private Map<UUID, Actor> activeActors;

	public Bakery(ITerminalCoordinator terminal, ICounterCoordinator counter, IBakeRoomCoordinator bakeRoom,
			IStorageCoordinator storage) {
		super();
		this.terminal = terminal;
		this.counter = counter;
		this.bakeRoom = bakeRoom;
		this.storage = storage;
		activeActors= new HashMap<>();
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

	

	public UUID addActor(Class<? extends Actor> type) {
		Actor actor;
		try {
			actor = type.newInstance();
			executor.submit(actor);
			activeActors.put(actor.getId(), actor);
			return actor.getId();
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e);
			return null;
		}

	}


	public boolean removeActor(Actor actor) {
		if (activeActors.remove(actor.getId(), actor)) {
			actor.setStop(true);
			return true;
		}
		return false;
	}

	public boolean removeActor(UUID id) {
		Actor actor = activeActors.remove(id);
		if (actor != null) {
			actor.setStop(true);
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
