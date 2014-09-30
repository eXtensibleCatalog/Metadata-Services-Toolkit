/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matcher;

import java.util.Collection;
import java.util.List;

import xc.mst.bo.record.InputRecord;
import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.services.marcaggregation.MarcAggregationService;

/**
 * An implementer of this interface provides operations on match-points pertaining
 * to a particular field.  State is obviously important as the matching being done
 * is dependent on the prior records that have come through the service.  There should
 * be only one instance per service instance.
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
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir);
    public List<Long> getMatchingInputIds(SaxMarcXmlRecord ir, List<Long> filterBy);

    /**
     * In order to perform matching, an instance of this class
     * must preserve match point values.  This method signifies
     * this instance to preserve the match-point value for this record.
     * This could be incorporated as part of the {@link #getMatchingInputIds(xc.mst.bo.record.InputRecord)}
     * implementation, but having it separate allows for the ability to call
     * getMatchingOutputIds without effecting the state of the system.
     *
     * @param r The record to preserve
     * @param ir In order to log errors, must pass along the InputRecord to associate the errors with.
     */
    public void addRecordToMatcher(SaxMarcXmlRecord r, InputRecord ir);
    
    /**
     * During an update, for performance reasons, it's important to know if a record's matchpoints have changed.
     * If they have *not* changed, then we can assume an update will not affect its current matchet
     * 
     * @param r The record to test
     * @param ir The record to test
     * @return true if the previous matchpoints are the same, otherwise false
     */
    public boolean matchpointsHaveChanged(SaxMarcXmlRecord r, InputRecord ir);

    /**
     * @param r The record to remove
     */
    public void removeRecordFromMatcher(InputRecord ir);

    /**
     * A matcher should do as much as it can in memory without the need to do a lookup on disk.  For this
     * reason, this load method should do what it can to load as much as it can to get the data from disk
     * into memory.  This method should be called once per service processing.
     */
    public void load(boolean initialLoad);

    /**
     * For testing.
     * @return
     */
    public int getNumRecordIdsInMatcher();
    public Collection<Long> getRecordIdsInMatcher();

    /**
     * For testing.
     * @return
     */
    public int getNumMatchPointsInMatcher();

    public String getName();
    public void setName(String name);

    //TODO maybe, in case the below starts to look like a workaround, it is because tried to get the MAS as a bean,
    //     but it seemed to be a different instance, even though since it is of type BaseManager it should be
    //     config'd via spring as a singleton.  So instead of getting it via bean, just keep a pointer to the instance.
    //
    public MarcAggregationService getMarcAggregationService();
    public void setMarcAggregationService(MarcAggregationService mas);

    /**
     * flush is called periodically (every x records processed) to persist to disk the updates made during
     * record processing.
     *
     * @param freeUpMemory this tells whether or not the service is done processing and memory should be
     *        freed up.  A service instance stays loaded in the jvm forever, so it's memory needs to be
     *        rescued.
     */
    public void flush(boolean freeUpMemory);
    

}
