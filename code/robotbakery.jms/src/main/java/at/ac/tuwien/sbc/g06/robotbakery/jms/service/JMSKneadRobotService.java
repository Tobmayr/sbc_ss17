package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product.BakeState;
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
	private QueueBrowser ingredientQueueBrowser;
	private Queue bakeroomQueue;

	private QueueBrowser productCounterQueueBrowser;
	private Queue counterQueue;
	private MessageProducer bakeroomQueueProducer;
	private MessageProducer storageQueueProducer;
	private MessageConsumer storageQueueConsumer;
	private QueueBrowser productStorageQueueBrowser;

	public JMSKneadRobotService() {
		try {
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			storageQueueProducer = session.createProducer(storageQueue);
			storageQueueConsumer = session.createConsumer(storageQueue);
			productStorageQueueBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
			ingredientQueueBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Ingredient.class.getSimpleName()));

			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);

			bakeroomQueue = session.createQueue(JMSConstants.Queue.BAKEROOM);

			bakeroomQueueProducer = session.createProducer(bakeroomQueue);

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {
		return JMSUtil.toList(productStorageQueueBrowser, JMSConstants.Property.STATE, BakeState.DOUGH.toString());
	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock() {
		return JMSUtil.getBrowserContentSizeByValues(ingredientQueueBrowser, Arrays.asList(IngredientType.values()),
				JMSConstants.Property.TYPE);
	}

	@Override
	public Map<String, Integer> getCounterStock() {
		return JMSUtil.getBrowserContentSizeByValues(productCounterQueueBrowser, SBCConstants.PRODUCTS_NAMES,
				JMSConstants.Property.TYPE);
	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer integer, ITransaction tx) {
		return null;
	}

	@Override
	public boolean useWaterPipe(long time, ITransaction tx) {
		try {
			WaterPipe pipe = receive(storageQueueConsumer);
			if (pipe != null) {
				Thread.sleep(time);
				send(storageQueueProducer, pipe);
				return true;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());

		}
		return false;
	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		return send(storageQueueProducer, nextProduct);
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		return receive(storageQueueConsumer);
	}

	@Override
	public boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		return send(storageQueueProducer, pack);
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		return send(bakeroomQueueProducer, nextProduct);
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
