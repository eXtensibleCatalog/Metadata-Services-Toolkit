package xc.mst.dao;

public class DBConnectionResetException extends Exception {
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 12345L;

	/**
	 * Constructs a new DataException with null as its detail message.
	 */
	public DBConnectionResetException()
	{
		super();
	} // end constructor()

	/**
	 * Constructs a new DataException with the specified detail message.
	 *
	 * @param detail A possibly null string containing details of the exception.
	 */
	public DBConnectionResetException(String detail)
	{
		super(detail);
	} // end constructor(String)
}
