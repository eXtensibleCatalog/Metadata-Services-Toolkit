
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

/**
 * This action method is used to configure the details of the EMail server
 *
 * @author Tejaswi Haramurali
 */
public interface EmailConfigService
{
    /**
	 * Gets the Email Configuration
	 *
	 * @return The email configuration, or null if there was no email configuration in the database.
     * @throws DatabaseConfigException 
	 */
	public abstract EmailConfig getEmailConfiguration() throws DatabaseConfigException;

	/**
	 * Updates the email configuration
	 *
	 * @param emailconfig The new email configuration
	 * @return True on success, false on failure
	 * @throws DataException if the passed email configuration was not valid
	 */
	public abstract void setEmailConfiguration(EmailConfig emailconfig) throws DataException;
}
