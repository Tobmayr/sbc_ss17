package at.ac.tuwien.sbc.g06.robotbakery.core.transaction;

public interface ITransactionManager{

	ITransaction createTransaction();
	
	boolean commitTransaction(ITransaction transaction);
	
	boolean rollback(ITransaction transaction);

}
