package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.IBakeRobotService;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;

public class BakeRobot extends Robot {

	private IBakeRobotService service;

	public BakeRobot(IBakeRobotService service, ITransactionManager transactionManager) {
		super(transactionManager);
		this.service=service;

	};

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	

}
