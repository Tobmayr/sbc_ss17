package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import java.io.IOException;

import at.ac.tuwien.sbc.g06.robotbakery.core.robot.*;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.transaction.JMSTransactionManager;

public class JMSRobotStartUp {

	public static void main(String[] args) throws IOException {
		if (args.length < 1 || args.length > 2) {
			throw new IllegalArgumentException("Usage: RobotStartUp TYPE (service|knead|bake)");
		}
		String id = null;
		if(args.length==2) id = args[1];
		switch (args[0]) {
		case "service":
			JMSServiceRobotService service = new JMSServiceRobotService();
			startRobot(new ServiceRobot(service,new JMSBakeryChangeNotifer(), new JMSTransactionManager(service.getSession()), id));
			break;
		case "knead":
			JMSKneadRobotService service1 = new JMSKneadRobotService();
			startRobot(new KneadRobot(service1, new JMSBakeryChangeNotifer(),new JMSTransactionManager(service1.getSession()), id));
			break;
		case "bake":
			JMSBakeRobotService service2 = new JMSBakeRobotService();
			startRobot(new BakeRobot(service2, new JMSBakeryChangeNotifer(),new JMSTransactionManager(service2.getSession()), id));
			break;
			case "deliver":
				JMSDeliveryRobotService service3 = new JMSDeliveryRobotService();
				startRobot(new DeliveryRobot(service3,new JMSBakeryChangeNotifer(), new JMSTransactionManager(service3.getSession()), id));
				break;

		default:
			break;
		}

	}

	private static void startRobot(Robot robot) throws IOException {
		Thread t = new Thread(robot);
		t.start();
		System.out.println(String.format("Starting robot of type \"%s\" with id \"%s\")",
				robot.getClass().getSimpleName(), robot.getId()));
		System.out.println("Press CTRL+C to exit...");
		while (System.in.read() != -1) {
			// Idling
		}
		t.interrupt();
	}

}
