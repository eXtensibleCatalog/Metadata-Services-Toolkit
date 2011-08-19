/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.util.HashMap;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Utility class to write log statements
 *
 * @author Eric Osisek
 */
public class LogWriter
{
    /**
     * Hash table mapping log file names to the Logger that writes to them
     */
    private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();

    public static void addDebug(String logFileLocation, String message)
    {
        // If we already have a logger for the file, use it
        if(loggers.containsKey(logFileLocation))
            loggers.get(logFileLocation).debug(message);
        else // Otherwise create a new logger for the file and use that
        {
            // Create a new file appender to write to the requested file
            FileAppender appender = new FileAppender();
            appender.setFile(MSTConfiguration.getUrlPath()+"/"+logFileLocation);
            appender.setName(logFileLocation);
            appender.setLayout(new PatternLayout("%d{DATE} %5p [%t] - %m%n"));
            appender.activateOptions();

            // Create a new logger for the file appender we just created
            Logger logger = Logger.getLogger(logFileLocation);
            logger.addAppender(appender);

            // Use the new logger to write the info message
            logger.debug(message);

            // Add the logger to the HashMap so we can access it next time we need it
            loggers.put(logFileLocation, logger);
        } // end else(The logger wasn't defined)
    } // end method addInfo(String, String)

    /**
     * Writes an info level message to the log file
     *
     * @param logFileLocation The file to write the message to
     * @param message The message to write
     */
    public static void addInfo(String logFileLocation, String message)
    {
        // If we already have a logger for the file, use it
        if(loggers.containsKey(logFileLocation))
            loggers.get(logFileLocation).info(message);
        else // Otherwise create a new logger for the file and use that
        {
            // Create a new file appender to write to the requested file
            FileAppender appender = new FileAppender();
            appender.setFile(MSTConfiguration.getUrlPath()+"/"+logFileLocation);
            appender.setName(logFileLocation);
            appender.setLayout(new PatternLayout("%d{DATE} %5p [%t] - %m%n"));
            appender.activateOptions();

            // Create a new logger for the file appender we just created
            Logger logger = Logger.getLogger(logFileLocation);
            logger.addAppender(appender);

            // Use the new logger to write the info message
            logger.info(message);

            // Add the logger to the HashMap so we can access it next time we need it
            loggers.put(logFileLocation, logger);
        } // end else(The logger wasn't defined)
    } // end method addInfo(String, String)

    /**
     * Writes a warning level message to the log file
     *
     * @param logFileLocation The file to write the message to
     * @param message The message to write
     */
    public static void addWarning(String logFileLocation, String message)
    {
        // If we already have a logger for the file, use it
        if(loggers.containsKey(logFileLocation))
            loggers.get(logFileLocation).warn(message);
        else // Otherwise create a new logger for the file and use that
        {
            // Create a new file appender to write to the requested file
            FileAppender appender = new FileAppender();
            appender.setFile(MSTConfiguration.getUrlPath()+"/"+logFileLocation);
            appender.setName(logFileLocation);
            appender.setLayout(new PatternLayout("%d{DATE} %5p [%t] - %m%n"));
            appender.activateOptions();

            // Create a new logger for the file appender we just created
            Logger logger = Logger.getLogger(logFileLocation);
            logger.addAppender(appender);

            // Use the new logger to write the info message
            logger.warn(message);

            // Add the logger to the HashMap so we can access it next time we need it
            loggers.put(logFileLocation, logger);
        } // end else(The logger wasn't defined)
    } // end method addWarning(String, String)

    /**
     * Writes an error level message to the log file
     *
     * @param logFileLocation The file to write the message to
     * @param message The message to write
     */
    public static void addError(String logFileLocation, String message)
    {
        // If we already have a logger for the file, use it
        if(loggers.containsKey(logFileLocation))
            loggers.get(logFileLocation).error(message);
        else // Otherwise create a new logger for the file and use that
        {
            // Create a new file appender to write to the requested file
            FileAppender appender = new FileAppender();
            appender.setFile(MSTConfiguration.getUrlPath()+"/"+logFileLocation);
            appender.setName(logFileLocation);
            appender.setLayout(new PatternLayout("%d{DATE} %5p [%t] - %m%n"));
            appender.activateOptions();

            // Create a new logger for the file appender we just created
            Logger logger = Logger.getLogger(logFileLocation);
            logger.addAppender(appender);

            // Use the new logger to write the info message
            logger.error(message);

            // Add the logger to the HashMap so we can access it next time we need it
            loggers.put(logFileLocation, logger);
        } // end else(The logger wasn't defined)
    } // end method addError(String, String)

    /**
     * Writes a fatal error level message to the log file
     *
     * @param logFileLocation The file to write the message to
     * @param message The message to write
     */
    public static void addFatalError(String logFileLocation, String message)
    {
        // If we already have a logger for the file, use it
        if(loggers.containsKey(logFileLocation))
            loggers.get(logFileLocation).fatal(message);
        else // Otherwise create a new logger for the file and use that
        {
            // Create a new file appender to write to the requested file
            FileAppender appender = new FileAppender();
            appender.setFile(MSTConfiguration.getUrlPath()+"/"+logFileLocation);
            appender.setName(logFileLocation);
            appender.setLayout(new PatternLayout("%d{DATE} %5p [%t] - %m%n"));
            appender.activateOptions();

            // Create a new logger for the file appender we just created
            Logger logger = Logger.getLogger(logFileLocation);
            logger.addAppender(appender);

            // Use the new logger to write the info message
            logger.fatal(message);

            // Add the logger to the HashMap so we can access it next time we need it
            loggers.put(logFileLocation, logger);
        } // end else(The logger wasn't defined)
    } // end method addFatalError(String, String)
} // end class LogWriter
