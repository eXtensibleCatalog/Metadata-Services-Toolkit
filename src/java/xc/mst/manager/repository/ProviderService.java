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
import xc.mst.bo.provider.Provider;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;

/**
 * Provider interface used add/delete/update a Repository/Provider
 *
 * @author Tejaswi Haramurali
 */
public interface ProviderService {

     /**
     * Returns a Provider Object based on the Name of the provider
      *
     * @param providerName The Name of the Provider
     * @return Provider Object
     * @throws DatabaseConfigException 
     */
    public Provider getProviderByName(String providerName) throws DatabaseConfigException;

    /**
     * Returns a Provider object based on the ID
     *
     * @param providerId The ID of the provider object to be returned
     * @return Provider Object
     * @throws DatabaseConfigException 
     */
    public Provider getProviderById(int providerId) throws DatabaseConfigException;

    /**
     * Returns a Provider Object based on the URL of the provider
     *
     * @param providerURL The URL of the Provider
     * @return Provider Object
     * @throws DatabaseConfigException 
     */
    public Provider getProviderByURL(String providerUrl) throws DatabaseConfigException;

    /**
     * Inserts a Provider
     *
     * @param provider
     */
    public void insertProvider(Provider provider) throws DataException;

    /**
     * Deletes a provider
     *
     * @param provider The provider object to be inserted
     */
    public void deleteProvider(Provider provider) throws DataException;

    /**
     * Updates the details of a provider object
     *
     * @param provider The Provider Object to be Updated
     */
    public void updateProvider(Provider provider) throws DataException;

    /**
     * This method is used to return a list of all the providers
     *
     * @return returns a list of providers
     * @throws DatabaseConfigException 
     */
    public List<Provider> getAllProviders() throws DatabaseConfigException;

  /**
   * Returns a list of all providers sorted
   *
   * @param sort determines if the rows are to be sorted in ascending or descending order
   * @param columnSorted column on which the sorting is done
   * @return list of providers
 * @throws DatabaseConfigException 
   */
    public List<Provider> getAllProvidersSorted(boolean sort,String columnSorted) throws DatabaseConfigException;
}
