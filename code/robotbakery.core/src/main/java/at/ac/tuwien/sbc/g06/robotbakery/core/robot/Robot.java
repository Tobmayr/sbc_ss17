package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

/**
 * Abstract class for robots
 */
public abstract class Robot implements Runnable {

	private final UUID id;
	private ITransactionManager transactionManager;

	public Robot(ITransactionManager transactionManager, String id) {
		if(id != null) this.id = UUID.fromString(id);
		else this.id = UUID.randomUUID();
		this.transactionManager = transactionManager;
	}

	/**
	 * simulate working with fixed time
	 * @param millis working time in milliseconds
	 */
	protected void sleepFor(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// ignore InterrupedException for now
		}
	}

	/**
	 * simulate working with random time
	 * @param min minimum working time in milliseconds
	 * @param max maximum working time in milliseconds
	 */
	protected void sleepFor(long min, long max) {
		try {
			Thread.sleep(ThreadLocalRandom.current().nextLong(min, max));
		} catch (InterruptedException e) {
			// ignore InterrupedException for now
		}
	}

	public UUID getId() {
		return id;
	}

	/**
	 * commit or rollback transaction
	 * @param task transactional task
	 * @return true for commit and false for rollback
	 */
	protected boolean doTask(ITransactionalTask task) {
		ITransaction transaction = transactionManager.createTransaction();
		if (task.execute(transaction)) {
			transactionManager.commitTransaction(transaction);
			return true;
		} else {
			transactionManager.rollback(transaction);
			return false;
		}

	}

}
