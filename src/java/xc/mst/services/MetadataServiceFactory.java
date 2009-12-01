package xc.mst.services;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.DatabaseConfigException;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.utils.LogWriter;

/**
 * Factory class that creates a new instance of a Metadata Service.
 * @author vinaykumarb
 *
 */
public class MetadataServiceFactory {
	
	/**
	 * The logger object
	 */
	protected static Logger log = Logger.getLogger(Constants.LOGGER_PROCESSING);
	

	/**
	 * Data access object for getting services
	 */
	private static ServiceDAO serviceDao = new DefaultServiceDAO();
	
	/**
	 * Runs the service with the passed ID
	 *
	 * @param serviceId The ID of the MetadataService to run
	 * @param outputSetId The ID of the set that records processed by this service should be added to
	 */
	public static MetadataService getService(int serviceId )
	{
		if(log.isDebugEnabled())
			log.debug("EnteringServiceFactoryegetnService for the service with ID " + serviceId + ".");

		// Get the service
		Service service = null;
		MetadataService serviceInstance = null;
		// Get the class for the service specified in the configuration file
		try {
		
			// Get the service details from the DB
			service = serviceDao.getById(serviceId);
			
			// The name of the class for the service specified in the configuration file.
			String targetClassName = service.getClassName();
			
			if(log.isDebugEnabled())
				log.debug("Trying to get the MetadataService class named " + targetClassName);

			// Get the class specified in the configuration file
			// The class loader for the MetadataService class
			ClassLoader serviceLoader = MetadataService.class.getClassLoader();

			// Load the class from the .jar file
			// TODO: Don't reload the class file each time.  Instead, load it into
			//       Tomcat once when the MST is started or the service is added/updated.
			//       This requires more research into Tomcat's class loaders
			URLClassLoader loader = new URLClassLoader(new URL[] { new File(service.getServiceJar()).toURI().toURL() }, serviceLoader);
			Class<?> clazz = loader.loadClass(targetClassName);
			serviceInstance = (MetadataService)clazz.newInstance();

			if(log.isDebugEnabled())
				log.debug("Found the MetadataService class named " + targetClassName + ", getting its constructor.");

			return serviceInstance;
			
		}// end try(run the service through reflection)
		catch (DatabaseConfigException e) {
			log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e);
			serviceInstance.sendReportEmail("Cannot connect to the database with the parameters supplied in the configuration file.");
			return null;
		}
		catch(ClassNotFoundException e)
		{
			log.error("Could not find class " + service.getClassName(), e);
			// Update database with status of service
			service.setStatus(Constants.STATUS_SERVICE_ERROR);
			
			// TODO runningService will be null. Use different send email for this			serviceInstance.sendReportEmail("The java class " + service.getClassName() + " could not be found.");

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + service.getClassName() + " could not be found.");

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}
			return null;

		} // end if(service is not user defined
		catch(NoClassDefFoundError e)
		{
			log.error("Could not find class " + service.getClassName(), e);

			// Update database with status of service
			service.setStatus(Constants.STATUS_SERVICE_ERROR);
			// TODO runningService will be null. Use different send email for this
			serviceInstance.sendReportEmail("The java class " + service.getClassName() + " could not be found.");

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + service.getClassName() + " could not be found.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return null;
			}

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			return null;
		} // end catch(NoClassDefFoundError)
		catch(IllegalAccessException e)
		{
			log.error("IllegalAccessException occurred while invoking the service's processRecords method.", e);

			// Update database with status of service
			service.setStatus(Constants.STATUS_SERVICE_ERROR);
			// TODO runningService will be null. Use different send email for this
			serviceInstance.sendReportEmail("The java class " + service.getClassName() + "'s processRecords method could not be accessed.");

			LogWriter.addError(service.getServicesLogFileName(), "Tried to start the " + service.getName() + " Service, but the java class " + service.getClassName() + "'s processRecords method could not be accessed.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return null;
			}

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			return null;
		} // end catch(IllegalAccessException)
		catch(Exception e)
		{
			log.error("Exception occurred while invoking the service's processRecords method.", e);

			// Update database with status of service
			service.setStatus(Constants.STATUS_SERVICE_ERROR);
			// TODO runningService will be null. Use different send email for this
			serviceInstance.sendReportEmail("Exception occurred while invoking the service's processRecords method.");
			
			LogWriter.addError(service.getServicesLogFileName(), "An internal error occurred while trying to start the " + service.getName() + " Service.");

			// Load the provider again in case it was updated during the harvest
			try
			{
				service = serviceDao.getById(service.getId());
			}
			catch (DatabaseConfigException e1)
			{
				log.error("Cannot connect to the database with the parameters supplied in the configuration file.", e1);

				return null;
			}

			// Increase the warning and error counts as appropriate, then update the provider
			service.setServicesErrors(service.getServicesErrors() + 1);

			try
			{
				serviceDao.update(service);
			}
			catch (DataException e2)
			{
				log.warn("Unable to update the service's warning and error counts due to a Data Exception.", e2);
			}

			return null;
		} // end catch(Exception)
	} // end method runService(int, int)

}
