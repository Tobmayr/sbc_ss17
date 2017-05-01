package at.ac.tuwien.sbc.g06.robotbakery.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Ingredient;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import static at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants.*;

import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JSMBakery extends Bakery implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(JSMBakery.class);
	private TopicConnection connection;
	private TopicSession session;

	public JSMBakery() {
		try {
			connection = JMSUtil.createAndTopicConnection();
			session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic notificationTopic = session.createTopic(JMSConstants.Topic.NOTIFICATION);
			TopicSubscriber subscriber = session.createSubscriber(notificationTopic);
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
				}
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
	}

	private void notify(Order ser, boolean removed, String originalDest) {
		if (originalDest.equals(getQueueAdress(JMSConstants.Queue.ORDER))) {
			if (!removed)
				registeredChangeListeners.forEach(ls -> ls.onOrderAddedOrUpdated((Order) ser));
		}

	}

	private void notifyListeners(Ingredient ingredient, boolean removed, String originalDest) {
		if (originalDest.equals(getQueueAdress(JMSConstants.Queue.COUNTER))) {
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

	@Override
	public void initializeStorageWaterPipe(WaterPipe waterPipe, ITransaction tx) {
		// TODO Auto-generated method stub

	}

	private String getQueueAdress(String queueName) {
		return "queue://" + queueName;
	}

}
