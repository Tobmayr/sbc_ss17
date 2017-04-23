package at.ac.tuwien.sbc.g06.robotbakery.xvsm.startup;

import java.io.IOException;

import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Actor;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.BakeRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.Customer;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.KneadRobot;
import at.ac.tuwien.sbc.g06.robotbakery.core.actor.ServiceRobot;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMCustomerService;
import at.ac.tuwien.sbc.g06.robotbakery.xvsm.service.XVSMServiceRobotService;

public class XVSMActorStartUp {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Usage: ActorStartUp TYPE (customer|service|knead|bake)");
		}
		switch (args[0]) {
		case "customer":
			startActor(new Customer(new XVSMCustomerService()));
			break;
		case "service":
			startActor(new ServiceRobot(new XVSMServiceRobotService()));
			break;
		case "knead":
			startActor(new KneadRobot());
			break;
		case "bake":
			startActor(new BakeRobot());
			break;

		default:
			break;
		}

	}

	private static void startActor(Actor actor) throws IOException {
		Thread t = new Thread(actor);
		t.start();
		System.out.println(String.format("Starting actor of type \"%s\" with id \"%s\")",
				actor.getClass().getSimpleName(), actor.getId()));
		System.out.println("Press CTRL+C to exit...");
		while (System.in.read() != -1) {
			// Idling
		}
		t.interrupt();
	}

}
