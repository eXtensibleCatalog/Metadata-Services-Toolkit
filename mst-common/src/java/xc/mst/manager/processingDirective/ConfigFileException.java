package xc.mst.manager.processingDirective;

/**
 * This Exception gets thrown when the user attempts to add a service by submiting an invalid configuration file
 * 
 * @author Eric Osisek
 */
public class ConfigFileException extends Exception 
{
	/**
	 * Used for serialization
	 */
	private static final long serialVersionUID = 12345L;

	/**
	 * Constructs a new DataException with null as its detail message.
	 */
	public ConfigFileException()
	{
		super();
	} // end constructor()

	/**
	 * Constructs a new DataException with the specified detail message.
	 *
	 * @param detail A possibly null string containing details of the exception.
	 */
	public ConfigFileException(String detail)
	{
		super(detail);
	} // end constructor(String)
}
