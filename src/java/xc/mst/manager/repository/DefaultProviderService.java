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
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.ProviderDAO;

/**
 * Service class that is used to Add/Delete/Update a Provider/Repository
 *
 * @author Tejaswi Haramurali
 */
public class DefaultProviderService implements ProviderService{

    ProviderDAO providerDao = new DefaultProviderDAO();

    public Provider getProviderByName(String providerName){
       return providerDao.getByName(providerName);
    }

    public Provider getProviderById(int providerId){
       return providerDao.getById(providerId);
    }

    public Provider getProviderByURL(String providerUrl) {
        return providerDao.getByURL(providerUrl);
    }

    public void insertProvider(Provider provider) throws DataException{
        providerDao.insert(provider);
    }

    public void deleteProvider(Provider provider) throws DataException{
        providerDao.delete(provider);
    }

    public void updateProvider(Provider provider) throws DataException{
        providerDao.update(provider);
    }
    public List<Provider> getAllProviders()
    {
        return providerDao.getAll();
    }

    /**
     * returns a list of all providers sorted by their names
     * @param sort boolean field that determines whether the provider names should be sorted in ascending or descending order
     * @return list of providers
     */
    public List<Provider> getAllProvidersSortedByName(boolean sort)
    {
        return providerDao.getSortedByName(sort);
    }
}
