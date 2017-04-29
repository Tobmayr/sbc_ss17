package at.ac.tuwien.sbc.g06.robotbakery.core.transaction;

public interface ITransactionalTask {
	
	boolean execute(ITransaction transaction);

}
