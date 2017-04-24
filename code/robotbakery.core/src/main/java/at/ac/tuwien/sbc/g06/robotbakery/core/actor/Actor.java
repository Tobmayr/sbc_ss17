package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.util.Random;
import java.util.UUID;

public abstract class Actor implements Runnable {

	private static final Random RANDOM = new Random();
	private final UUID id;

	public Actor() {
		id = UUID.randomUUID();
	}

	protected void sleepForSeconds(int secs) {
		try {
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			// ignore InterrupedException for now
		}
	}

	protected void sleepForSecons(int min, int max) {
		try {
			Thread.sleep(min * 1000 + RANDOM.nextInt((max - min) * 1000));
			;
		} catch (InterruptedException e) {
			// ignore InterrupedException for now
		}
	}
	public UUID getId() {
		return id;
	}

	
	

}
