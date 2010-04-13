
/**
  * Copyright (c) 2009 University of Rochester
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
import xc.mst.dao.emailconfig.DefaultEmailConfigDAO;
import xc.mst.dao.emailconfig.EmailConfigDAO;

/**
 *  The service method that is used to set the details of an Email server
 *
 * @author Tejaswi Haramurali
 */
public class DefaultEmailConfigService implements EmailConfigService
{
    /** The email config DAO object */
    private EmailConfigDAO emailConfigDao;

    public DefaultEmailConfigService()
    {
        emailConfigDao = new DefaultEmailConfigDAO();
    }
    /**
	 * Gets the Email Configuration
	 *
	 * @return The email configuration, or null if there was no email configuration in the database.
     * @throws DatabaseConfigException 
	 */
	public EmailConfig getEmailConfiguration() throws DatabaseConfigException
    {
        return emailConfigDao.getConfiguration();
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
        this.emailConfigDao.setConfiguration(emailconfig);
    }
}
