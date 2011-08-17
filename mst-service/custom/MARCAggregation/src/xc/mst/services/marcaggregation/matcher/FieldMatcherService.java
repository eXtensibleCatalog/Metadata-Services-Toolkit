/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matcher;

import xc.mst.manager.BaseService;

/**
 * This abstract class ties the interface into the MST by making use of the platform
 * infrastucture.
 * 
 * @author Benjamin D. Anderson
 *
 */
public abstract class FieldMatcherService extends BaseService implements FieldMatcher {
    
    
    /**
     * The state of the matcher needs to be flushed periodically.  
     */
    abstract public void flush();
    
    /**
     * The state of the matcher is loaded entirely from the database before 
     * the <a href="http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggArchitecture#MAS"MAS</a> runs.
     */
    abstract public void loadFromDB();
    
    /**
     * When the <a href="http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggArchitecture#MAS"MAS</a>
     * is finished processing, it needs to free up memory.
     */
    abstract public void unload();

}
