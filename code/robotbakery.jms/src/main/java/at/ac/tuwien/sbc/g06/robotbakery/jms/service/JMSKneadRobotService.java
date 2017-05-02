package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.FlourPack;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Recipe.IngredientType;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

/**
 * 
 * @author Tobias Ortmayr, 1026279
 *
 */
public class JMSKneadRobotService extends AbstractJMSService implements IKneadRobotService {
	private static Logger logger = LoggerFactory.getLogger(JMSKneadRobotService.class);
	private Queue storageQueue;
	private QueueBrowser ingredientQueueBrowser;
	private QueueBrowser productQueueBrowser;
	private Queue counterQueue;

	public JMSKneadRobotService() {
		try {
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			counterQueue = session.createQueue(JMSConstants.Queue.COUNTER);
			ingredientQueueBrowser = session.createBrowser(storageQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Ingredient.class.getSimpleName()));
			productQueueBrowser = session.createBrowser(counterQueue,
					String.format("%s = '%s'", JMSConstants.Property.CLASS, Product.class.getSimpleName()));
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Product> checkBaseDoughsInStorage() {

		return null;
	}

	@Override
	public Map<IngredientType, Integer> getIngredientStock() {
		try {
			Map<IngredientType, Integer> map = new HashMap<>();
			for (IngredientType type : IngredientType.values()) {
				if (type != IngredientType.WATER) {
					map.put(type, size(ingredientQueueBrowser, JMSConstants.Property.TYPE, type.toString()));
				}
			}
			return map;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return null;
		}

	}

	@Override
	public Map<String, Integer> getCounterStock() {
		try {
			Map<String, Integer> map = new HashMap<String, Integer>();
			for (String product : SBCConstants.PRODUCTS_NAMES) {
				map.put(product, size(productQueueBrowser, JMSConstants.Property.TYPE, product));
			}
			return map;
		} catch (JMSException e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<Ingredient> getIngredientsFromStorage(IngredientType type, Integer integer, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WaterPipe useWaterPipe(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putBaseDoughInStorage(Product nextProduct, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Product getProductFromStorage(UUID id, ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlourPack getPackFromStorage(ITransaction tx) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putPackInStorage(FlourPack pack, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean putDoughInBakeroom(Product nextProduct, ITransaction tx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRobot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdownRobot() {
		// TODO Auto-generated method stub
		
	}

}
