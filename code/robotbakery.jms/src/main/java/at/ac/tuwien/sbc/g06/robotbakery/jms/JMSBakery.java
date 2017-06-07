package at.ac.tuwien.sbc.g06.robotbakery.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TopicConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.WaterPipe;
import at.ac.tuwien.sbc.g06.robotbakery.core.notifier.Bakery;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;
import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSUtil;

public class JMSBakery extends Bakery {
	private static Logger logger = LoggerFactory.getLogger(JMSBakery.class);
	private TopicConnection connection;
	private MessageProducer counterQueueMessageProducer;
	private Session queueSession;

	public JMSBakery() {
		super(new JMSBakeryChangeNotifer());
		try {
			connection = JMSUtil.createAndTopicConnection(JMSConstants.SERVER_ADDRESS);
			queueSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue counterQueue = queueSession.createQueue(JMSConstants.Queue.COUNTER);
			counterQueueMessageProducer = queueSession.createProducer(counterQueue);
			connection.start();
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
