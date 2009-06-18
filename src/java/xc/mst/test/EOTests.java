/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.log.Log;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.bo.user.Server;
import xc.mst.bo.user.User;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.harvest.DefaultHarvestDAO;
import xc.mst.dao.log.DefaultLogDAO;
import xc.mst.dao.user.DefaultServerDAO;
import xc.mst.dao.user.DefaultUserDAO;
import xc.mst.dao.user.ServerDAO;
import xc.mst.dao.user.UserDAO;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.harvester.HarvestRunner;
import xc.mst.manager.processingDirective.DefaultServicesService;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.scheduling.SchedulingException;
import xc.mst.services.MetadataService;
import xc.mst.utils.LogWriter;
import xc.mst.utils.index.RecordList;
import xc.mst.utils.index.SolrIndexManager;

public class EOTests
{
	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration;

	/**
	 * Used to format timestamps for the results of the tests
	 */
	private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	static
	{
		// Load the configuration file
		configuration = ConfigurationManager.getConfiguration();

		// Configure the log file location as the value found in the configuration file.
		String logConfigFileLocation = configuration.getProperty(Constants.CONFIG_LOGGER_CONFIG_FILE_LOCATION);
		if(logConfigFileLocation != null)
			PropertyConfigurator.configure(logConfigFileLocation);

		// Abort if we could not find the configuration file
		else
		{
			System.err.println("The configuration file was invalid or did not exist.");
			System.exit(1);
		}

		MSTSolrServer.getInstance();
	}

	/**
	 * Tests the classes in the data project
	 *
	 * @param args Ignored
	 */
	public static void main(String[] args) throws DataException, SchedulingException
	{
		try
		{
			Record record = new Record();
			record.setCreatedAt(new Date());
			record.setDeleted(false);
			record.setFormat(new DefaultFormatDAO().getById(1));
			record.setHarvest(new DefaultHarvestDAO().getById(1));
			record.setOaiDatestamp("test");
			record.setOaiHeader("test");
			record.setOaiIdentifier("test");
			record.setOaiXml("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
					"\n\t<marc:record xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">" +
					"\n\t<marc:leader>04245ccm a22006850  45 A</marc:leader>" +
					"\n\t<marc:controlfield tag=\"001\">852960</marc:controlfield>" +
					"\n\t<marc:controlfield tag=\"003\">NRU</marc:controlfield>" +
					"\n\t<marc:controlfield tag=\"005\">20050824135454.0</marc:controlfield>" +
					"\n\t<marc:controlfield tag=\"008\">860910m1960    au zzm         n    N/A  </marc:controlfield>" +
					"\n\t<marc:datafield tag=\"010\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">m  61000295 /M/r86</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"028\" ind1=\"2\" ind2=\"0\">" +
					"\n\t<marc:subfield code=\"a\">U.E. 2931</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">Universal Edition</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"028\" ind1=\"2\" ind2=\"0\">" +
					"\n\t<marc:subfield code=\"a\">CFK 9256</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">C.F. Kahnt</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"b\">ocm</marc:subfield>" +
					"\n\t<marc:subfield code=\"a\">15082670</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"9\">00792471</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"040\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">DLC</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">DLC</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">m/c</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">RES</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"049\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">[Closed] [Shelf] RESM [over]</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"050\" ind1=\"0\" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">M3.</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">M155</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"090\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">M3</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">.M214</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"099\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">M3 .M214</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"100\" ind1=\"1\" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Mahler, Gustav,</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">1860-1911.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"240\" ind1=\"1\" ind2=\"0\">" +
					"\n\t<marc:subfield code=\"a\">Works.</marc:subfield>" +
					"\n\t<marc:subfield code=\"f\">1960</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"245\" ind1=\"0\" ind2=\"0\">" +
					"\n\t<marc:subfield code=\"a\">SaÌˆmtliche Werke :</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">kritische Gesamtausgabe /</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">Gustav Mahler ; herausgegeben von der Internationalen Gustav Mahler Gesellschaft, Wien.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"260\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Wien :</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">Universal Edition,</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">c1960-</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"300\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">v. ;</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">28 cm.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"500\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Publisher varies.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"500\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Pl. no.: U.E. 2931/</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"505\" ind1=\"0\" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Bd. 1. Symphonie Nr. 1 -- Bd. 2. Symphonie Nr. 2 -- Bd. 3. Symphonie Nr. 3 -- Bd. 4. Symphonie Nr. 4 in vier SaÌˆtzen -- Bd. 5. Symphonie Nr. 5 -- Bd. 6. Symphonie Nr. 6 -- Bd. 7. Symphonie Nr. 7 -- Bd. 8. Symphonie Nr. 8 -- Bd. 9. Das Lied von der Erde -- Bd. 10. Symphony Nr. 9 -- Bd. 11a. Adagio aus der Symphonie Nr. 10 -- Bd. 12. Das klagende Lied -- Bd. 13. Teilbd. 1. Lieder eines fahrenden Gesellen : fuÌˆr eine Singstimme mit Klavier ; Teilbd. 2a. Neun Lieder und GesaÌˆnge aus &quot;Des Knaben Wunderhorn&quot; : fuÌˆr Singstimme (hoch) und Klavier ; Teilbd. 2a. Lieder eines fahrenden Gesellen : fuÌˆr Singstimme (tief) und Klavier ; Teilbd. 2b. FuÌˆnfzehn Lieder, Humoresken und Balladen aus Des Knaben Wunderhorn fuÌˆr Singstimme und Klavier ; Teilbd. 3. Kindertotenlieder : fuÌˆr eine Singstimme mit Klavier ; Teilbd. 4. Lieder nach Texten von Friedrich RuÌˆckert : fuÌˆr eine Singstimme mit Klavier ; Teilbd. 5. Verschiedene Lieder -- Bd. 14. Teilbd. 1. Lieder eines fahrenden Gesellen : fuÌˆr eine Singstimme mit Orchester ; Teilbd. 2. Des Knaben Wunderhorn ; Teilbd. 3. Kindertotenlieder : fuÌˆr eine Singstimme mit Orchester ; Teilbd. 4. Lieder nach Texten von Friedrich RuÌˆckert : fuÌˆr eine Singstimme mit Orchester.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"710\" ind1=\"2\" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">Internationale Gustav Mahler Gesellschaft.</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"910\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">950805</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"911\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"a\">p</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 1</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087014310488</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 2</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612603</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 3</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612611</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 4</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612629</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 5</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612637</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 6</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612645</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 7</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612652</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 8</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612660</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 9</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612678</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 10</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612686</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 11a</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612694</marc:subfield>" +
					"\n\t</marc:datafield>" +
					"\n\t<marc:datafield tag=\"966\" ind1=\" \" ind2=\" \">" +
					"\n\t<marc:subfield code=\"l\">/Csstk</marc:subfield>" +
					"\n\t<marc:subfield code=\"m\">SCOLL</marc:subfield>" +
					"\n\t<marc:subfield code=\"s\">M3 .M214</marc:subfield>" +
					"\n\t<marc:subfield code=\"c\">1</marc:subfield>" +
					"\n\t<marc:subfield code=\"d\">v. 12</marc:subfield>" +
					"\n\t<marc:subfield code=\"b\">39087013612702</marc:subfield>" +
					"\n\t</marc:datafield>");

			record.setProvider(new DefaultProviderDAO().getById(1));

			DefaultRecordService rs = new DefaultRecordService();

			System.out.println(formatter.format(new Date()) + " Begining insetion of 100,000 records.");

			for(int i = 0; i < 100000; i++)
			{
				rs.insert(record);
			}

			System.out.println(formatter.format(new Date()) + " Finished insetion of 100,000 records.");

			SolrIndexManager.getInstance().commitIndex();

			if(true) return;

			//File file = new File("C:\\AllXcProjects\\MetadataServicesToolkit\\serviceConfig\\DefaultNormalizationServiceConfig.xccfg");
			//new DefaultServicesService().addNewService(file);

			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			//SolrIndexManager.getInstance().commitIndex();
		}
	}
}
