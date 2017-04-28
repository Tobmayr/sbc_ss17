package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;

public class BakeRobot extends Actor {

	private IBakeRobotService service;

	public BakeRobot(IBakeRobotService service) {
		super();
		this.service=service;

	};

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	

}
