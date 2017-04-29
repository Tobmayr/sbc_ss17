package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;

public class BakeRobot extends Robot {

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
