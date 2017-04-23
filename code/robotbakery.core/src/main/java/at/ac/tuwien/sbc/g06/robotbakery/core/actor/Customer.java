package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import at.ac.tuwien.sbc.g06.robotbakery.core.service.ICounterService;

public class Customer extends Actor {

	private ICounterService counterService;

	public Customer(ICounterService counterService) {
		this.counterService=counterService;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
