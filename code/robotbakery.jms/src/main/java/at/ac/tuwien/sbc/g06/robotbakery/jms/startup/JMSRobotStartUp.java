package at.ac.tuwien.sbc.g06.robotbakery.jms.startup;

import java.io.IOException;

import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.service.JMSServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.jms.transaction.JMSTransactionManager;

public class JMSRobotStartUp {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Usage: RobotStartUp TYPE (service|knead|bake)");
		}
		switch (args[0]) {
		case "service":
			startRobot(new ServiceRobot(new JMSServiceRobotService(), new JMSTransactionManager()));
			break;
		case "knead":
			startRobot(new KneadRobot(new JMSKneadRobotService(), new JMSTransactionManager()));
			break;
		case "bake":
			startRobot(new BakeRobot(new JMSBakeRobotService(), new JMSTransactionManager()));
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
