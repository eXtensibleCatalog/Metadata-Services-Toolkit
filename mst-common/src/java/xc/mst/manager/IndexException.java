/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.manager;

/**
 * This exception is thrown when problem in accessing Solr server, or indexing
 * and searching the Solr index
 *
 * @author Sharmila Ranganathan
 *
 */
public class IndexException extends Exception {

    /** Serial id*/
    private static final long serialVersionUID = -6996237342238178175L;

    /**
     * Constructs a new IndexException with null as its detail message.
     */
    public IndexException()
    {
        super();
    }

    /**
     * Constructs a new IndexException with the specified detail message.
     *
     * @param detail A possibly null string containing details of the exception.
     */
    public IndexException(String detail)
    {
        super(detail);
    }
}
