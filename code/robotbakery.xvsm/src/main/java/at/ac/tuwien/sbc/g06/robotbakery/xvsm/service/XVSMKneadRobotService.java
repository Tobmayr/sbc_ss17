package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mozartspaces.capi3.ComparableProperty;
import org.mozartspaces.capi3.Property;
import org.mozartspaces.capi3.Query;
import org.mozartspaces.capi3.QueryCoordinator;
import org.mozartspaces.capi3.TypeCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class XVSMKneadRobotService extends AbstractXVSMService implements IKneadRobotService {
	private static Logger logger = LoggerFactory.getLogger(XVSMKneadRobotService.class);
	private final ContainerReference storageContainer;
	private final ContainerReference counterContainer;
	private final ContainerReference bakeroomContainer;
	private IRobotService robotService;

	public XVSMKneadRobotService() {
		super(new Capi(DefaultMzsCore.newInstance()));
		storageContainer = getContainer(XVSMConstants.STORAGE_CONTAINER_NAME);
		counterContainer = getContainer(XVSMConstants.COUNTER_CONTAINER_NAME);
		bakeroomContainer = getContainer(XVSMConstants.BAKEROOM_CONTAINER_NAME);
		this.robotService = new XVSMRobotService(capi, KneadRobot.class.getSimpleName());
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {
		Query query = new Query().filter(Property.forName("*", "type").equalTo(BakeState.DOUGH))
				.sortup(ComparableProperty.forName("*", "timestamp"));
		return read(storageContainer, null, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
				TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX));

	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock() {

		Map<IngredientType, Integer> map = new HashMap<>();
		for (IngredientType type : IngredientType.values()) {
			if (type != IngredientType.WATER) {
				if (type == IngredientType.FLOUR) {
					map.put(type, test(storageContainer, null,
							TypeCoordinator.newSelector(FlourPack.class, MzsConstants.Selecting.COUNT_MAX)));
				} else {
					Query query = new Query().filter(Property.forName("*", "type").equalTo(type));
					map.put(type,
							test(storageContainer, null,
									QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
									TypeCoordinator.newSelector(Ingredient.class, MzsConstants.Selecting.COUNT_MAX)));
				}
			}
		}
		return map;

	}

	@Override
	public Map<String, Integer> getCounterStock() {

		Map<String, Integer> map = new HashMap<>();
		for (String productName : SBCConstants.PRODUCTS_NAMES) {
			Query query = new Query().filter(Property.forName("*", "productName").equalTo(productName));
			map.put(productName,
					test(counterContainer, null, QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
							TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX)));
		}
		return map;

	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer amount, ITransaction tx) {
		Query query = new Query().filter(Property.forName("*", "type").equalTo(type)).cnt(amount);
		return take(storageContainer, tx, QueryCoordinator.newSelector(query, amount),
				TypeCoordinator.newSelector(Ingredient.class, amount));

	}

	@Override
	public boolean useWaterPipe(long time, ITransaction tx) {

		WaterPipe pipe = takeFirst(storageContainer, tx, TypeCoordinator.newSelector(WaterPipe.class));
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			return false;
		}
		write(pipe, storageContainer, tx);
		return true;

	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		return write(nextProduct, storageContainer, tx);
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		Query query = new Query().filter(Property.forName("*", "id").equalTo(id)).cnt(1);
		return takeFirst(storageContainer, tx, QueryCoordinator.newSelector(query),
				TypeCoordinator.newSelector(Product.class));
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		return write(nextProduct, bakeroomContainer, tx);
	}

	@Override
	public void startRobot() {
		robotService.startRobot();

	}

	@Override
	public void shutdownRobot() {
		robotService.shutdownRobot();

	}

	@Override
	public boolean takeFlourFromStorage(int amount, ITransaction tx) {
		FlourPack pack = null;
		while (amount > 0) {
			pack = (FlourPack) getPackFromStorage(tx);
			if (pack == null)
				return false;
			amount = pack.takeFlour(amount);
		}
		if (pack.getCurrentAmount() > 0) {
			if (!putPackInStorage(pack, tx))
				return false;
		}
		return true;
	}

	private FlourPack getPackFromStorage(ITransaction tx) {
		Query query = new Query().sortup(ComparableProperty.forName("*", "currentAmount")).cnt(1);
		return takeFirst(storageContainer, tx, QueryCoordinator.newSelector(query),
				TypeCoordinator.newSelector(Ingredient.class));

	}

	private boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		return write(pack, storageContainer, tx);
	}
}
