import java.rmi.server.ServerCloneException;
import java.sql.Timestamp;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.Session;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.model.Product;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSServer;
import at.ac.tuwien.sbc.g06.robotbakery.jms.JMSTabletUIChangeNotifier;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSTabletUIService;

public class Test {

	public static void main(String[] args) throws Exception {
		JMSServer server = new JMSServer();
		server.startUp();
		JMSTabletUIChangeNotifier notifier = new JMSTabletUIChangeNotifier();
		Order order = new Order();
		order.setTimestamp(new Timestamp(System.currentTimeMillis()));
		order.setTotalSum(5000D);
		JMSTabletUIService tabletService = new JMSTabletUIService();
		tabletService.initialize(UUID.randomUUID(), UUID.randomUUID());
		
		IServiceRobotService serviceRobotService = new JMSServiceRobotService();

		tabletService.addOrderToCounter(order);
		serviceRobotService.getNextOrder(null);
		serviceRobotService.getNextOrder(null);
		serviceRobotService.getNextOrder(null);
		serviceRobotService.getNextOrder(null);
		serviceRobotService.getNextOrder(null);
	}
}
