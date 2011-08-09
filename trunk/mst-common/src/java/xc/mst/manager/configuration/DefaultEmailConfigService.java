
/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.configuration;

import xc.mst.bo.emailconfig.EmailConfig;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.manager.BaseService;

/**
 *  The service method that is used to set the details of an Email server
 *
 * @author Tejaswi Haramurali
 */
public class DefaultEmailConfigService extends BaseService implements EmailConfigService
{

    /**
	 * Gets the Email Configuration
	 *
	 * @return The email configuration, or null if there was no email configuration in the database.
     * @throws DatabaseConfigException 
	 */
	public EmailConfig getEmailConfiguration() throws DatabaseConfigException
    {
        return getEmailConfigDAO().getConfiguration();
    }

	/**
	 * Updates the email configuration
	 *
	 * @param emailconfig The new email configuration
	 * @return True on success, false on failure
	 * @throws DataException if the passed email configuration was not valid
	 */
	public void setEmailConfiguration(EmailConfig emailconfig) throws DataException
    {
        getEmailConfigDAO().setConfiguration(emailconfig);
    }
}
