package at.ac.tuwien.sbc.g06.robotbakery.jms;

import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.TabletUIChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;

/**
 * Created by Matthias Höllthaler on 07.06.2017.
 */
public class JMSDeliveryTabletUIChangeNotifier extends TabletUIChangeNotifer implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(JMSTabletUIChangeNotifier.class);
    private TopicConnection connection;
    private TopicSession session;

    public JMSDeliveryTabletUIChangeNotifier() {
        try {
            int port = 0;
            for(port=45555; port<46666; port++) {
                if(JMSUtil.available(port)) break;
            }

            connection = JMSUtil.createAndTopicConnection(JMSConstants.DELIVERY_ADDRESS+port);
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
