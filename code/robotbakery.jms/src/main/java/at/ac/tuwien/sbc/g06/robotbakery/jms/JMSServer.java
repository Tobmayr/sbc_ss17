package at.ac.tuwien.sbc.g06.robotbakery.jms;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSServer  {
	private static Logger logger = LoggerFactory.getLogger(JMSServer.class);
	private BrokerService broker;

	public JMSServer() throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				broker.stop();
			} catch (Exception e) {
				// ignore as we are in shudown mode
			}
		}));
	}

	public boolean startUp(String address) {
		try {
			broker = new BrokerService();
			broker.addConnector(address);
			broker.setPersistent(false);
			// Clear potential old messages
			broker.deleteAllMessages();
			broker.start();

			System.out.println(
					"Started JMS-Server at: " + broker.getTransportConnectorByScheme("tcp").getUri().toString());
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}

	}
	
	
}
