/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.repository;

import java.util.List;

import xc.mst.bo.provider.Set;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Service to access the sets
 *
 * @author Sharmila Ranganathan
 *
 */
public interface SetService {

    /**
     * Get set having the specified set id
     *
     * @param setId Id of the set
     * @return Set if exist else null
     * @throws DatabaseConfigException
     */
    public Set getSetById(int setId) throws DatabaseConfigException;

    /**
     * Returns a Set object based on the value of the setSpec supplied.
     *
     * @param setSpec The set Specification value
     * @return Set object
     * @throws DatabaseConfigException
     */
    public Set getSetBySetSpec(String setSpec) throws DatabaseConfigException;

    /**
     * Delete Set
     *
     * @param set set to be deleted
     * @throws DataException Thrown when problem in deleting the set
     */
    public void deleteSet(Set set) throws DataException;

    /**
     * Add a set
     *
     * @param set set to inserted into the database
     */
    public void insertSet(Set set) throws DataException;

    /**
     * Get all sets
     *
     * @return all sets
     * @throws DatabaseConfigException
     */
    public List<Set> getAllSets() throws DatabaseConfigException;

}
