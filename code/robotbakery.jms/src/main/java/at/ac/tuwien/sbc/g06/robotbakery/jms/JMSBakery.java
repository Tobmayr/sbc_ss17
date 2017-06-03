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

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.core.util.SBCConstants;
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

				String coordinationRoom = JMSUtil
						.getCoordinationRoom(message.getStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION));

				registeredChangeListeners.forEach(ls -> ls.onObjectChanged(ser, coordinationRoom, !removed));

			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}
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


}
