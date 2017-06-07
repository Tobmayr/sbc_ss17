package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import java.io.IOException;

import org.mozartspaces.core.Capi;
import org.mozartspaces.core.DefaultMzsCore;

import at.ac.tuwien.sbc.g06.robotbakery.core.robot.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.DeliveryRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.Robot;
import at.ac.tuwien.sbc.g06.robotbakery.core.robot.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.notifier.XVSMBakeryChangeNotifer;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMDeliveryRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMKneadRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMServiceRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.transaction.XVSMTransactionManager;

public class XVSMRobotStartUp {

	public static void main(String[] args) throws IOException {
		if (args.length < 1 || args.length > 2) {
			throw new IllegalArgumentException("Usage: RobotStartUp TYPE (service|knead|bake|deliver) (ID)");
		}
		String id = null;
		if (args.length == 2)
			id = args[1];
		switch (args[0]) {
		case "service":
			startRobot(new ServiceRobot(new XVSMServiceRobotService(),
					new XVSMBakeryChangeNotifer(new Capi(DefaultMzsCore.newInstance())), new XVSMTransactionManager(),
					id));
			break;
		case "knead":
			startRobot(new KneadRobot(new XVSMKneadRobotService(),
					new XVSMBakeryChangeNotifer(new Capi(DefaultMzsCore.newInstance())), new XVSMTransactionManager(),
					id));
			break;
		case "bake":
			startRobot(new BakeRobot(new XVSMBakeRobotService(),
					new XVSMBakeryChangeNotifer(new Capi(DefaultMzsCore.newInstance())), new XVSMTransactionManager(),
					id));
			break;
		case "deliver":
			startRobot(new DeliveryRobot(new XVSMDeliveryRobotService(),
					new XVSMBakeryChangeNotifer(new Capi(DefaultMzsCore.newInstance())), new XVSMTransactionManager(),
					id));
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
