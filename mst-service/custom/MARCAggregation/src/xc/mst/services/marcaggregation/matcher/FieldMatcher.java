/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matcher;

import java.util.List;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.Record;

/**
 * an implementer of this interface provides operations on match-points pertaining 
 * to a particular field
 * 
 * @author Benjamin D. Anderson
 * 
 * @see <a href="http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggMatchPointsAndErrorCases">Match Points</a>
 */
public interface FieldMatcher {

    /**
     * @param ir the input record from which to retrieve the match point value
     * @return the list of record ids of other input records that match ir 
     */
    public abstract List<Long> getMatchingOutputIds(InputRecord ir);
    
    /**
     * @param r in order to perform matching, an instance of this class
     *          must preserve match point values.  This method signifies
     *          this instance to preserve the match-point value for this record
     */
    public void addRecordToMatcher(Record r);
    
    /**
     * 
     */
    public void flush();
    
    public void loadFromDB();
    
    public void unload();
    
    public List<Long> getPreviousMatchPoint(long inputId);
}
