/**
  * Copyright (c) 2009 University of Rochester
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
import xc.mst.dao.provider.DefaultSetDAO;
import xc.mst.dao.provider.SetDAO;

/**
 * Service to access the sets
 *
 * @author Sharmila Ranganathan
 *
 */
public class DefaultSetService implements SetService {

    /** Set DAP object */
	private SetDAO setDAO = new DefaultSetDAO();

	/**
	 * Get set having the specified set id
	 *
	 * @param setId Id of the set
	 * @return Set if exist else null
	 * @throws DatabaseConfigException 
	 */
	public Set getSetById(int setId) throws DatabaseConfigException {
		return setDAO.getById(setId);
	}

	/**
	 * Delete Set
	 *
	 * @param set set to be deleted
	 * @throws DataException Thrown when problem in deleting the set
	 */
	public void deleteSet(Set set) throws DataException {
		setDAO.delete(set);
	}

    /**
     * Add a set
     *
     * @param set set to inserted into the database
     */
    public void insertSet(Set set) throws DataException {
    	setDAO.insert(set);
    }

    /**
     * Get all sets
     *
     * @return all sets
     * @throws DatabaseConfigException 
     */
    public List<Set> getAllSets() throws DatabaseConfigException {
    	return setDAO.getAll();
    }

    /**
     * Returns a Set object based on a setSpec value provided
     * 
     * @param setSpec The set specification value
     * @return Set Object
     * @throws DatabaseConfigException 
     */
    public Set getSetBySetSpec(String setSpec) throws DatabaseConfigException
    {
        return setDAO.getBySetSpec(setSpec);
    }

}
