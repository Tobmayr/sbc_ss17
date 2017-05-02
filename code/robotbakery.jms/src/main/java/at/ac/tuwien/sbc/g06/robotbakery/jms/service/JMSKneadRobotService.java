package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

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
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class JMSKneadRobotService extends AbstractJMSService implements IKneadRobotService {
	private static Logger logger = LoggerFactory.getLogger(JMSKneadRobotService.class);
	private Queue storageQueue;
	private Queue bakeroomQueue;
	private Queue counterQueue;

	private QueueBrowser ingredientBrowser;
	private QueueBrowser productCounterBrowser;
	private QueueBrowser productStorageBrowser;

	private MessageProducer bakeroomProducer;
	private MessageProducer storageProducer;

	private MessageConsumer waterConsumer;
	private MessageConsumer flourPackConsumer;

	private Map<String, MessageConsumer> storageProductTypeConsumers;
	private Map<IngredientType, MessageConsumer> storageIngredientTypeConsumers;

	public JMSKneadRobotService() {
		try {
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			storageProducer = session.createProducer(storageQueue);
			waterConsumer = session.createConsumer(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, WaterPipe.class.getSimpleName()));
			flourPackConsumer = session.createConsumer(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.TYPE, IngredientType.FLOUR));

			productStorageBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
			ingredientBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Ingredient.class.getSimpleName()));

			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			productCounterBrowser = session.createBrowser(counterQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
			bakeroomQueue = session.createQueue(JMSConstants.Queue.BAKEROOM);

			bakeroomProducer = session.createProducer(bakeroomQueue);

			for (String name : SBCConstants.PRODUCTS_NAMES) {
				storageProductTypeConsumers.put(name,
						session.createConsumer(storageQueue,
								String.format("%s='%s' AND %s='%s ", JMSConstants.Property.CLASS,
										Product.class.getSimpleName(), JMSConstants.Property.TYPE, name)));
			}

			for (IngredientType type : IngredientType.values()) {
				storageIngredientTypeConsumers.put(type,
						session.createConsumer(storageQueue,
								String.format("%s='%s' AND %s='%s ", JMSConstants.Property.CLASS,
										Product.class.getSimpleName(), JMSConstants.Property.TYPE, type.toString())));
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {
		return JMSUtil.toList(productStorageBrowser, JMSConstants.Property.STATE, BakeState.DOUGH.toString());
	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock() {
		return JMSUtil.getBrowserContentSizeByValues(ingredientBrowser, Arrays.asList(IngredientType.values()),
				JMSConstants.Property.TYPE);
	}

	@Override
	public Map<String, Integer> getCounterStock() {
		return JMSUtil.getBrowserContentSizeByValues(productCounterBrowser, SBCConstants.PRODUCTS_NAMES,
				JMSConstants.Property.TYPE);
	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer amount, ITransaction tx) {
		return receive(storageIngredientTypeConsumers.get(type), amount);
	}

	@Override
	public boolean useWaterPipe(long time, ITransaction tx) {
		try {
			WaterPipe pipe = receive(waterConsumer);
			if (pipe != null) {
				Thread.sleep(time);
				send(storageProducer, pipe);
				return true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());

		}
		return false;
	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		return send(storageProducer, nextProduct);
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		try {
			MessageConsumer cs = session.createConsumer(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.ID, id));
			return receive(cs);
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return null;
		}
	
	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		return receive(flourPackConsumer);
	}

	@Override
	public boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		return send(storageProducer, pack);
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		return send(bakeroomProducer, nextProduct);
	}

	@Override
	public void startRobot() {
		notify(KneadRobot.class.getSimpleName(), false);

	}

	@Override
	public void shutdownRobot() {
		notify(KneadRobot.class.getSimpleName(), true);

	}

}
