package at.ac.tuwien.sbc.g06.robotbakery.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.core.listener.BakeryUIChangeNotifier;

public class JMSBakeryUIChangeNotifier extends BakeryUIChangeNotifier implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(JMSBakeryUIChangeNotifier.class);

	@Override
	public void onMessage(Message message) {
		

	}



}
