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

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSTabletUIChangeNotifier extends TabletUIChangeNotifer implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(JMSTabletUIChangeNotifier.class);
	private TopicConnection connection;
	private TopicSession session;

	public JMSTabletUIChangeNotifier() {
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
				String coordinationRoom = JMSUtil
						.getCoordinationRoom(message.getStringProperty(JMSConstants.Property.ORIGINAL_DESTINATION));
				registeredChangeListeners.forEach(ls -> ls.onObjectChanged(ser, coordinationRoom, !removed));
			}

		} catch (JMSException e) {
			logger.error(e.getMessage());
		}

	}

}
