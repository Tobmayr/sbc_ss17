package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

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

	private Map<String, MessageConsumer> storageProductTypeConsumers = new HashMap<>();
	private Map<IngredientType, MessageConsumer> storageIngredientTypeConsumers = new HashMap<>();

	private MessageProducer counterProducer;
	private MessageConsumer waterConsumer;

	public JMSKneadRobotService() {
		super(true, Session.CLIENT_ACKNOWLEDGE);
		try {
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			storageProducer = session.createProducer(storageQueue);
			productStorageBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
			ingredientBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Ingredient.class.getSimpleName()));

			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			productCounterBrowser = session.createBrowser(counterQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
			bakeroomQueue = session.createQueue(JMSConstants.Queue.BAKEROOM);

			bakeroomProducer = session.createProducer(bakeroomQueue);
			counterProducer = session.createProducer(counterQueue);

			for (String name : SBCConstants.PRODUCTS_NAMES) {
				storageProductTypeConsumers.put(name,
						session.createConsumer(storageQueue,
								String.format("%s='%s' AND %s='%s'", JMSConstants.Property.CLASS,
										Product.class.getSimpleName(), JMSConstants.Property.TYPE, name)));
			}

			for (IngredientType type : IngredientType.values()) {
				storageIngredientTypeConsumers.put(type,
						session.createConsumer(storageQueue,
								String.format("%s='%s' AND %s='%s' ", JMSConstants.Property.CLASS,
										Ingredient.class.getSimpleName(), JMSConstants.Property.TYPE,
										type.toString())));
			}

			waterConsumer = session.createConsumer(counterQueue, String
					.format(String.format("%s = '%s'", JMSConstants.Property.CLASS, WaterPipe.class.getSimpleName())));

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {
		return JMSUtil.toList(productStorageBrowser, JMSConstants.Property.STATE, BakeState.DOUGH.toString(), null);
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
				send(counterProducer, pipe);
				return true;
			}
			waterConsumer.close();

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
			MessageConsumer consumer = session.createConsumer(storageQueue,
					String.format(String.format("%s = '%s'", JMSConstants.Property.ID, id.toString())));
			return receive(consumer);
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public boolean takeFlourFromStorage(int amount, ITransaction tx) {
		List<Ingredient> list = JMSUtil.toList(ingredientBrowser, JMSConstants.Property.TYPE,
				IngredientType.FLOUR.toString(), null);
		if (list.isEmpty())
			return false;
		if (list.size() > 1)
			list.sort((i, j) -> ((FlourPack) i).getCurrentAmount().compareTo(((FlourPack) j).getCurrentAmount()));
		List<String> used = new ArrayList<String>();
		FlourPack open = null;
		for (int i = 0; i < list.size(); i++) {
			FlourPack pack = (FlourPack) list.get(i);
			amount = pack.takeFlour(amount);
			if (pack.getCurrentAmount() > 0) {
				open = pack;
			} else {
				used.add(pack.getId().toString());
			}
			if (amount == 0) {
				break;
			}
		}
		return doRealDelete(used, open);

	}

	private boolean doRealDelete(List<String> used, FlourPack open) {
		MessageConsumer consumer = storageIngredientTypeConsumers.get(IngredientType.FLOUR);
		List<Ingredient> pushBack = new ArrayList<Ingredient>();
		while (!used.isEmpty()) {
			Ingredient temp = receive(consumer);
			if (temp == null)
				return false;
			if (used.remove(temp.getId().toString())) {
				pushBack.add(temp);
			}

		}
		if (open != null)
			pushBack.add(open);
		for (Ingredient i : pushBack) {
			if (!send(storageProducer, i))
				return false;
		}

		return true;
	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		List<Ingredient> list = JMSUtil.toList(ingredientBrowser, JMSConstants.Property.TYPE,
				IngredientType.FLOUR.toString(), null);
		if (list.isEmpty())
			return null;
		if (list.size() > 1)
			list.sort((i, j) -> ((FlourPack) i).getCurrentAmount().compareTo(((FlourPack) j).getCurrentAmount()));
		Ingredient element = list.get(0);
		List<Ingredient> pushBack = new ArrayList<Ingredient>();
		MessageConsumer consumer = storageIngredientTypeConsumers.get(IngredientType.FLOUR);
		Ingredient temp = receive(consumer);
		while (temp != null) {
			String s = temp.getId().toString();
			String b = element.getId().toString();
			if (s.equals(b))
				break;

			pushBack.add(temp);
			temp = receive(consumer);
		}

		for (Ingredient i : pushBack) {
			if (!send(storageProducer, i))
				;
			return null;
		}
		return (FlourPack) element;
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
		notify(KneadRobot.class.getSimpleName(), false, storageQueue);

	}

	@Override
	public void shutdownRobot() {
		notify(KneadRobot.class.getSimpleName(), true, storageQueue);

	}

}
