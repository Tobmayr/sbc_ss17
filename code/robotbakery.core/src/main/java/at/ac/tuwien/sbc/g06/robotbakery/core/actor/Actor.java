package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

public abstract class Actor implements Serializable,Runnable {

	private UUID id;
	private static final Random RANDOM = new Random();

	public Actor() {
		this.id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
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

}
