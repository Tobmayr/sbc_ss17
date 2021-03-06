package at.ac.tuwien.sbc.g06.robotbakery.jms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSBakeRobotService extends AbstractJMSService implements IBakeRobotService {

	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIService.class);
	private Queue bakeroomQueue;
	private Queue storageQueue;
	private MessageProducer storageQueueProducer;
	private MessageConsumer bakeroomQueueConsumer;
	private QueueBrowser bakeroomQueueBrowser;

	public JMSBakeRobotService() {
		super(false, Session.AUTO_ACKNOWLEDGE, JMSConstants.SERVER_ADDRESS);

		try {
			bakeroomQueue = session.createQueue(JMSConstants.Queue.BAKEROOM);
			storageQueue = session.createQueue(JMSConstants.Queue.STORAGE);
			storageQueueProducer = session.createProducer(storageQueue);
			bakeroomQueueBrowser = session.createBrowser(bakeroomQueue);
			bakeroomQueueConsumer = session.createConsumer(bakeroomQueue);
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<Product> getUnbakedProducts(ITransaction tx) {
		List<Product> products = new ArrayList<Product>();
		Product firstProduct = receive(bakeroomQueueConsumer);
		if (firstProduct != null) {
			products.add(firstProduct);
			long t = System.currentTimeMillis();
			long end = t + SBCConstants.BAKE_WAIT;
			while (System.currentTimeMillis() < end) {
				Product nextProduct = receive(bakeroomQueueConsumer);
				if (nextProduct != null) {
					products.add(nextProduct);
					if (products.size() == 5)
						break;
				}
			}

		}
		return products;
	}

	@Override
	public boolean putBakedProductsInStorage(List<Product> products, ITransaction tx) {
		for (Product product : products) {
			if (!send(storageQueueProducer, product))
				return false;
		}
		return true;
	}

	@Override
	public Map<String, Boolean> getInitialState() {
		Map<String, Boolean> map = new HashMap<>();
		map.put(SBCConstants.NotificationKeys.IS_BAKEROOM_EMPTY,
				JMSUtil.test(bakeroomQueueBrowser, JMSConstants.Property.CLASS, Product.class.getSimpleName()) == 0);
		return map;
	}

}
