
package xc.mst.manager.repository;

import java.util.List;
import xc.mst.bo.provider.Provider;
import xc.mst.dao.DataException;

/**
 * Provider interface used add/delete/update a Repository/Provider
 *
 * @author Tejaswi Haramurali
 */
public interface ProviderService {

     /**
     * Returns a Provider Object based on the Name of the provider
     * @param providerName The Name of the Provider
     * @return Provider Object
     */
    public Provider getProviderByName(String providerName);

    /**
     * Returns a Provider object based on the ID
     * @param providerId The ID of the provider object to be returned
     * @return Provider Object
     */
    public Provider getProviderById(int providerId);

    /**
     * Returns a Provider Object based on the URL of the provider
     * @param providerURL The URL of the Provider
     * @return Provider Object
     */
    public Provider getProviderByURL(String providerUrl);

    /**
     * Inserts a Provider
     * @param provider
     */
    public void insertProvider(Provider provider) throws DataException;

    /**
     * Deletes a provider
     * @param provider The provider object to be inserted
     */
    public void deleteProvider(Provider provider) throws DataException;

    /**
     * Updates the details of a provider object
     * @param provider The Provider Object to be Updated
     */
    public void updateProvider(Provider provider) throws DataException;

    /**
     * This method is used to return a list of all the providers
     * @return returns a list of providers
     */
    public List<Provider> getAllProviders();

    /**
     * returns a list of all providers sorted by their names
     * @param sort boolean field that determines whether the provider names should be sorted in ascending or descending order
     * @return list of providers
     */
    public List<Provider> getAllProvidersSortedByName(boolean sort);
}
