package at.ac.tuwien.sbc.g06.robotbakery.core.robot;

import java.util.Random;
import java.util.UUID;

import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransaction;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionManager;
import at.ac.tuwien.sbc.g06.robotbakery.core.transaction.ITransactionalTask;

public abstract class Robot implements Runnable {

	private static final Random RANDOM = new Random();
	private final UUID id;
	private ITransactionManager transactionManager;

	public Robot(ITransactionManager transactionManager) {
		id = UUID.randomUUID();
		this.transactionManager = transactionManager;
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
