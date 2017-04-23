package at.ac.tuwien.sbc.g06.robotbakery.core.actor;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.model.Entity;

public abstract class Actor extends Entity implements Runnable {

	private static final Random RANDOM = new Random();

	public Actor() {
		super();
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

	public static Random getRandom() {
		return RANDOM;
	}

}
