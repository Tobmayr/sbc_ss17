import java.sql.Timestamp;

import javax.jms.Connection;
import javax.jms.Session;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Order;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.IServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.service.ITabletUIService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSTabletUIService;

public class Test {

	public static void main(String[] args) throws Exception {
		Order order = new Order();
		order.setTimestamp(new Timestamp(System.currentTimeMillis()));
		order.setTotalSum(5000D);
		ITabletUIService tabletService= new JMSTabletUIService();
		IServiceRobotService serviceRobotService= new JMSServiceRobotService();
		tabletService.addOrderToCounter(order);
		serviceRobotService.getNextOrder(null);

	}
}
