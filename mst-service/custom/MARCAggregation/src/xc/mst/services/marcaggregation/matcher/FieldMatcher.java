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
 * An implementer of this interface provides operations on match-points pertaining 
 * to a particular field.  State is obviously important as the matching being done
 * is dependent on the prior records that have come through the service.
 * 
 * @author Benjamin D. Anderson
 * 
 * @see <a href="http://code.google.com/p/xcmetadataservicestoolkit/wiki/MarcAggMatchPointsAndErrorCases">Match Points</a>
 */
public interface FieldMatcher {

    /**
     * Discovers whether or not there are records that have been previously
     * processed that match this record for a particular field. 
     * 
     * @param ir the input record from which to retrieve the match point value
     * @return the list of record ids of other input records that match ir 
     */
    public abstract List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir);
    
    /**
     * In order to perform matching, an instance of this class
     * must preserve match point values.  This method signifies
     * this instance to preserve the match-point value for this record.
     * This could be incorporated as part of the {@link #getMatchingOutputIds(xc.mst.bo.record.InputRecord)}
     * implementation, but having it separate allows for the ability to call 
     * getMatchingOutputIds without effecting the state of the system.
     *          
     * @param r The record to preserve 
     */
    public void addRecordToMatcher(SaxMarcXmlRecord r);

    /**
     * Since this class preserves state, we need to know when that state changes.
     * Before calling {@link #getMatchingOutputIds(xc.mst.bo.record.InputRecord)},
     * this method is called to get the "before" state (ie which records matched previously).
     * If any records matched previously that don't match now, then they will be un-merged. 
     * 
     * @param inputId
     * @return the ids of the records that previously matched this record on this field
     */
    public List<Long> getPreviousMatchPoint(long inputId);

    /**
     * 
     * @param inputId
     * @return the matched String representation if match found else null
     *         EXAMPLE:  (NRU)123   // exmple string returned for found match for SystemControlNumberMatcher
     */
    public String getMatchPointValue(long inputId);
}
