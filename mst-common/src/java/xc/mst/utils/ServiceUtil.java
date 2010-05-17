/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import xc.mst.bo.service.Service;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.email.Emailer;
import xc.mst.manager.record.RecordService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserService;
import xc.mst.services.GenericMetadataService;
import xc.mst.utils.index.RecordList;

/**
 * Utility class for generic functions performed by services.
 * @author vinaykumarb
 *
 */
public class ServiceUtil {

	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);

	/**
	 * Data access object for getting services
	 */
	private ServiceDAO serviceDao = (ServiceDAO)MSTConfiguration.getBean("ServiceDAO");

	/**
	 * Manager for getting, inserting and updating records
	 */
	private RecordService recordService = (RecordService)MSTConfiguration.getBean("RecordService");

	/**
	 * Used to send email reports
	 */
	private Emailer mailer = new Emailer();
	
	/** Singleton instance */
	private static ServiceUtil instance;
	
	/**
	 * Get instance of ServiceUtil
	 * 
	 * @return
	 */
	public static ServiceUtil getInstance() {
		if (instance == null) {
			instance = new ServiceUtil();
		}
		
		return instance;
	}
	
	/**
	 * Validates the service with the passed ID
	 *
	 * @param serviceId The ID of the MetadataService to run
	 * @param successStatus The status of the MetadataService is the validation was successful
	 * @param testSolr True to test the connection to the index, false otherwise
	 */
	public void checkService(int serviceId, String successStatus, boolean testSolr){
	}
	
	/**
	 * Checks whether or not the service is able to be run.  If the service is
	 * not runnable, logs the reason as an error in the service's log file and
	 * sets the service's status to "error".  Otherwise sets the service's status to
	 * the passed status.
	 *
	 * @param statusForSuccess The status of the service if it is runnable
	 * @param testSolr True to verify access to the Solr index, false otherwise
	 * @return True iff the service is runnable
	 */
	public boolean checkService(Service service, String statusForSuccess, boolean testSolr) {

		if(testSolr)
		{
			// Check that we can access the Solr index
			try
			{
				RecordList test = recordService.getInputForService(service.getId());
				if(test == null)
				{
					LogWriter.addError(service.getServicesLogFileName(), "Cannot run the service because we cannot access the Solr index.");
					service.setServicesErrors(service.getServicesErrors()+1);

					try
					{
						serviceDao.update(service);
					}
					catch(DataException e)
					{
						log.error("An error occurred while updating the service's error count.", e);
					}

					return false;
				}
			}
			catch(Exception e)
			{
				LogWriter.addError(service.getServicesLogFileName(), "Cannot run the service because we cannot access the Solr index.");
				service.setServicesErrors(service.getServicesErrors()+1);

				try
				{
					serviceDao.update(service);
				}
				catch(DataException e1)
				{
					log.error("An error occurred while updating the service's error count.", e1);
				}

				return false;
			}
		}

		return true;
	
	}
	
	public void sendEmail(String message, String serviceName) {

		try {
			UserService userService = (UserService)MSTConfiguration.getBean("UserService");
			GroupService groupService = (GroupService)MSTConfiguration.getBean("GroupService");

			if (mailer.isConfigured()) {
				
				// The email's subject
				InetAddress addr = null;
				addr = InetAddress.getLocalHost();

				String subject = null;
				if ( serviceName != null) {
					subject = "Results of processing " + serviceName +" by MST Server on " + addr.getHostName();
				} else {
					subject = "Error message from MST Server " + addr.getHostName();
				}
		
				// The email's body
				StringBuilder body = new StringBuilder();
		
				// First report any problems which prevented the harvest from finishing
				if(message != null)
					body.append("The service failed for the following reason: ").append(message).append("\n\n");

				// Send email to every admin user
				for (User user : userService.getUsersForGroup(groupService.getGroupByName(Constants.ADMINSTRATOR_GROUP).getId())) {
					mailer.sendEmail(user.getEmail(), subject, body.toString());
				}
			} 

		} catch (UnknownHostException exp) {
			log.error("Host name query failed. Error sending notification email.",exp);
		} catch (DatabaseConfigException e) {
			log.error("Database connection exception. Error sending notification email.");
		} catch (Exception e) {
			log.error("Error sending notification email.");
		}

	}
}
