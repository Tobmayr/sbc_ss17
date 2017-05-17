package at.ac.tuwien.sbc.g06.robotbakery.jms;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.PackedOrder;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSBakery extends Bakery implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(JMSBakery.class);
	private TopicConnection connection;
	private TopicSession session;
	private MessageProducer counterQueueMessageProducer;
	private Session queueSession;

	public JMSBakery() {
		try {
			connection = JMSUtil.createAndTopicConnection();
			queueSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic notificationTopic = session.createTopic(JMSConstants.Topic.NOTIFICATION);
			TopicSubscriber subscriber = session.createSubscriber(notificationTopic);
			Queue counterQueue = queueSession.createQueue(JMSConstants.Queue.COUNTER);
			counterQueueMessageProducer = queueSession.createProducer(counterQueue);
			subscriber.setMessageListener(this);
			connection.start();
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof ObjectMessage) {
				Serializable ser = ((ObjectMessage) message).getObject();
				boolean removed = message.getBooleanProperty(JMSConstants.Property.REMOVED);
				String originalDest = message.getStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION);
				if (ser instanceof Order) {
					notify((Order) ser, removed, originalDest);
				} else if (ser instanceof Product) {
					notifyListeners((Product) ser, removed, originalDest);
				} else if (ser instanceof Ingredient) {
					notifyListeners((Ingredient) ser, removed, originalDest);
				} else if (ser instanceof String) {
					notifyListeners((String) ser, removed);
				}
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	private void notify(Order ser, boolean removed, String originalDest) {
		if (removed)
			return;
		registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated(ser));
		if (ser instanceof PackedOrder) {
			((PackedOrder) ser).getProducts().forEach(p -> registeredChangeListeners.forEach(ls -> {
				if (originalDest.equals(getQueueAdress(JMSConstants.Queue.TERMINAL))) {
					ls.onProductRemovedFromCounter(p);
					ls.onProductAddedToTerminal(p);
				} else if (originalDest.equals(getQueueAdress(JMSConstants.Queue.ORDER))) {
					ls.onProductRemovedFromTerminal(p);
				}
			}));
		}

	}

	private void notifyListeners(Ingredient ingredient, boolean removed, String originalDest) {
		if (originalDest.equals(getQueueAdress(JMSConstants.Queue.STORAGE))) {
			if (removed)
				registeredChangeListeners.forEach(ls -> ls.onIngredientRemovedFromStorage(ingredient));
			else
				registeredChangeListeners.forEach(ls -> ls.onIngredientAddedToStorage(ingredient));
		}

	}

	private void notifyListeners(Product product, boolean removed, String originalDest) {
		if (originalDest.equals(getQueueAdress(JMSConstants.Queue.STORAGE))) {
			if (removed)
				registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromStorage(product));
			else
				registeredChangeListeners.forEach(ls -> ls.onProductAddedToStorage(product));
		} else if (originalDest.equals(getQueueAdress(JMSConstants.Queue.COUNTER))) {
			if (removed)
				registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromCounter(product));
			else
				registeredChangeListeners.forEach(ls -> ls.onProductAddedToCounter(product));
		} else if (originalDest.equals(getQueueAdress(JMSConstants.Queue.BAKEROOM))) {
			if (removed)
				registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromBakeroom(product));
			else
				registeredChangeListeners.forEach(ls -> ls.onProductAddedToBakeroom(product));
		} else if (originalDest.equals(getQueueAdress(JMSConstants.Queue.TERMINAL))) {
			if (removed)
				registeredChangeListeners.forEach(ls -> ls.onProductRemovedFromTerminal(product));
			else
				registeredChangeListeners.forEach(ls -> ls.onProductAddedToTerminal(product));

		}

	}

	private void notifyListeners(String msg, boolean removed) {
		final Class<? extends Robot> clazz;
		if (msg.equals(ServiceRobot.class.getSimpleName())) {
			clazz = ServiceRobot.class;
		} else if (msg.equals(KneadRobot.class.getSimpleName())) {
			clazz = KneadRobot.class;
		} else if (msg.equals(BakeRobot.class.getSimpleName())) {
			clazz = BakeRobot.class;
		} else {
			clazz = null;
		}

		if (!removed)
			registeredChangeListeners.forEach(ls -> ls.onRobotStart(clazz));
		else
			registeredChangeListeners.forEach(ls -> ls.onRobotShutdown(clazz));

	}

	@Override
	public void init() {
		try {
			Message msg = queueSession.createObjectMessage(new WaterPipe());
			msg.setStringProperty(JMSConstants.Property.CLASS, WaterPipe.class.getSimpleName());
			counterQueueMessageProducer.send(msg);
		} catch (JMSException e) {
			logger.error(e.getMessage());
		}

	}

	private String getQueueAdress(String queueName) {
		return "queue://" + queueName;
	}

	@Override
	public void addItemsToStorage(List<Serializable> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProductsToCounter(List<Product> products) {
		// TODO Auto-generated method stub
		
	}

}
