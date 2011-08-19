/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

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
