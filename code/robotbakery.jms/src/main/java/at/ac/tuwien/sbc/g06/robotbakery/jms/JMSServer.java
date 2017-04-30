package at.ac.tuwien.sbc.g06.robotbakery.jms;

import org.apache.activemq.broker.BrokerService;

import at.ac.tuwien.sbc.g06.robotbakery.jms.util.JMSConstants;

public class JMSServer {

	public JMSServer() throws Exception {
		BrokerService broker = new BrokerService();
		broker.addConnector(JMSConstants.SERVER_ADDRESS);
		broker.setPersistent(false);
		// Clear potential old messages
		broker.deleteAllMessages();
		broker.start();

		System.out.println("Started JMS-Server at: " + broker.getTransportConnectorByScheme("tcp").getUri().toString());
		System.out.println("Press CTRL+C to shutdown the server...");
		while (System.in.read() != -1)
			;

	}
	
	public static void main(String[] args) throws Exception {
		new JMSServer();
	}
}
