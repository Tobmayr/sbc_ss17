package at.ac.tuwien.sbc.g06.robotbakery.xvsm.service;

import java.util.Arrays;
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
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCoreException;
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
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMConstants;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.util.XVSMUtil;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class XVSMKneadRobotService implements IKneadRobotService {
	private static Logger logger = LoggerFactory.getLogger(XVSMKneadRobotService.class);
	private final ContainerReference storageContainer;
	private final ContainerReference counterContainer;
	private final ContainerReference bakeroomContainer;
	private Capi capi;
	private IRobotService robotService;

	public XVSMKneadRobotService() {
		this.capi = new Capi(DefaultMzsCore.newInstance());
		storageContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.STORAGE_CONTAINER_NAME);
		counterContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.COUNTER_CONTAINER_NAME);
		bakeroomContainer = XVSMUtil.getOrCreateContainer(capi, XVSMConstants.BAKEROOM_CONTAINER_NAME);
		this.robotService = new XVSMRobotService(capi, KneadRobot.class.getSimpleName());
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {
		try {
			Query query = new Query().filter(Property.forName("*", "type").equalTo(BakeState.DOUGH))
					.sortup(ComparableProperty.forName("*", "timestamp"));
			return capi.read(storageContainer,
					Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
							TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX)),
					MzsConstants.RequestTimeout.TRY_ONCE, null);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock() {
		try {
			Map<IngredientType, Integer> map = new HashMap<>();
			for (IngredientType type : IngredientType.values()) {
				if (type != IngredientType.WATER) {
					if (type == IngredientType.FLOUR) {
						map.put(type,
								capi.test(storageContainer,
										TypeCoordinator.newSelector(FlourPack.class, MzsConstants.Selecting.COUNT_MAX),
										MzsConstants.RequestTimeout.TRY_ONCE, null));
					} else {
						Query query = new Query().filter(Property.forName("*", "type").equalTo(type));
						map.put(type, capi.test(storageContainer,
								Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
										TypeCoordinator.newSelector(Ingredient.class,
												MzsConstants.Selecting.COUNT_MAX)),
								MzsConstants.RequestTimeout.TRY_ONCE, null));
					}
				}
			}
			return map;

		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public Map<String, Integer> getCounterStock() {
		try {
			Map<String, Integer> map = new HashMap<>();
			for (String productName : SBCConstants.PRODUCTS_NAMES) {
				Query query = new Query().filter(Property.forName("*", "productName").equalTo(productName));
				map.put(productName,
						capi.test(counterContainer,
								Arrays.asList(QueryCoordinator.newSelector(query, MzsConstants.Selecting.COUNT_MAX),
										TypeCoordinator.newSelector(Product.class, MzsConstants.Selecting.COUNT_MAX)),
								MzsConstants.RequestTimeout.TRY_ONCE, null));
			}
			return map;
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer amount, ITransaction tx) {
		try {
			Query query = new Query().filter(Property.forName("*", "type").equalTo(type)).cnt(amount);
			return capi.take(storageContainer,
					Arrays.asList(QueryCoordinator.newSelector(query, amount),
							TypeCoordinator.newSelector(Ingredient.class, amount)),
					MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean useWaterPipe(long time, ITransaction tx) {
		try {
			WaterPipe pipe = (WaterPipe) capi.take(storageContainer, TypeCoordinator.newSelector(WaterPipe.class),2000, XVSMUtil.unwrap(tx)).get(0);
			Thread.sleep(time);
			capi.write(storageContainer, new Entry(pipe));
			return true;
		} catch (MzsCoreException | InterruptedException e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		try {
			Entry entry = new Entry(nextProduct);
			capi.write(entry, storageContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		try {
			Query query = new Query().filter(Property.forName("*", "id").equalTo(id)).cnt(1);

			return (Product) capi.take(storageContainer,
					Arrays.asList(QueryCoordinator.newSelector(query), TypeCoordinator.newSelector(Product.class)),
					MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)).get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		try {
			Query query = new Query().sortup(ComparableProperty.forName("*", "currentAmount")).cnt(1);
			return (FlourPack) capi.take(storageContainer,
					Arrays.asList(QueryCoordinator.newSelector(query), TypeCoordinator.newSelector(Ingredient.class)),
					MzsConstants.RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx)).get(0);
		} catch (MzsCoreException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		try {
			Entry entry = new Entry(pack);
			capi.write(entry, storageContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		try {
			Entry entry = new Entry(nextProduct);
			capi.write(entry, bakeroomContainer, RequestTimeout.TRY_ONCE, XVSMUtil.unwrap(tx));
			return true;
		} catch (MzsCoreException ex) {
			logger.error(ex.getMessage());
			return false;
		}
	}

	@Override
	public void startRobot() {
		robotService.startRobot();

	}

	@Override
	public void shutdownRobot() {
		robotService.shutdownRobot();

	}

}
