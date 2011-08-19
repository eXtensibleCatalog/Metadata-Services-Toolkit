/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.dao;

/**
 * Exception that represents a exceptional condition when the
 * database connection times out.
 * 
 * @author Vinaykumar Bangera
 * 
 */
public class DBConnectionResetException extends Exception {
    /**
     * Used for serialization
     */
    private static final long serialVersionUID = 12345L;

    /**
     * Constructs a new DBConnectionResetException with null as its detail message.
     */
    public DBConnectionResetException() {
        super();
    } // end constructor()

    /**
     * Constructs a new DBConnectionResetException with the specified detail message.
     * 
     * @param detail
     *            A possibly null string containing details of the exception.
     */
    public DBConnectionResetException(String detail) {
        super(detail);
    } // end constructor(String)
}
