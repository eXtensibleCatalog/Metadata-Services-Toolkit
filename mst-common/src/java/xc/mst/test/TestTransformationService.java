/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.bo.service.Service;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataServiceFactory;
import xc.mst.services.MetadataService;
import xc.mst.utils.index.RecordList;

public class TestTransformationService
{
	private static String UNNORMALIZED_RECORD =
		"<marc:record xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-insance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim  http://www.log.gov/standards/marcxml/schema/MARC21slim.xsd\">\n" +

		"<marc:leader>01056naca a2200229n  4500</marc:leader>\n" +

		"<marc:controlfield tag=\"001\">1050000</marc:controlfield>\n" +
		"<marc:controlfield tag=\"007\">ko                     </marc:controlfield>\n" +
		"<marc:controlfield tag=\"008\">840516r19992008ab|    j   m |a aa1ajpn  </marc:controlfield>\n" +

		"<marc:controlfield tag=\"005\">19961213000000.0</marc:controlfield>\n" +

		"<marc:datafield tag=\"969\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">aaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"v\">yyy</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)23456</marc:subfield>\n" +
		"\t<marc:subfield code=\"5\">NRU</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"969\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">aaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"v\">yyy</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(NRU)12345</marc:subfield>\n" +
		"\t<marc:subfield code=\"5\">NRU</marc:subfield>\n" +
		"</marc:datafield>\n" +

		/*"<marc:datafield tag=\"730\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">aaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"y\">wtf?!</marc:subfield>\n" +
		"\t<marc:subfield code=\"d\">ddd</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">66612345</marc:subfield>\n" +
		"\t<marc:subfield code=\"x\">ISBN12345</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)12345</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"730\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">A_creator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">A_creator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">A_title1</marc:subfield>\n" +
		"\t<marc:subfield code=\"t\">A_title2</marc:subfield>\n" +
		"\t<marc:subfield code=\"e\">A_creator3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">66612345</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)12345</marc:subfield>\n" +
		"</marc:datafield>\n" +*/

		/*"<marc:datafield tag=\"711\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">taaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"s\">tsss</marc:subfield>\n" +
		"\t<marc:subfield code=\"v\">twtf?!</marc:subfield>\n" +
		"\t<marc:subfield code=\"t\">tttt</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)23456</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">B_creator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">B_creator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">B_title1</marc:subfield>\n" +
		"\t<marc:subfield code=\"t\">B_title2</marc:subfield>\n" +
		"\t<marc:subfield code=\"e\">B_creator3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)23456</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">aaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"s\">wtf?!</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">444</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(DLC)23456</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">taaa</marc:subfield>\n" +
		"\t<marc:subfield code=\"v\">twtf?!</marc:subfield>\n" +
		"\t<marc:subfield code=\"t\">tttt</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">(NRU)12345</marc:subfield>\n" +
		"</marc:datafield>\n" +*/

		/*"<marc:datafield tag=\"711\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">bluecreator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">bluecreator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"c\">bluecreator3 tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">bluetitle1</marc:subfield>\n" +
		"\t<marc:subfield code=\"l\">bluetitle2</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">bluetitle3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">blue agent</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">greencreator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">greencreator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"c\">greencreator3 tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">greentitle1</marc:subfield>\n" +
		"\t<marc:subfield code=\"l\">greentitle2</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">greentitle3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">green agent</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">yellowcreator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">yellowcreator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"c\">yellowcreator3 tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">yellowtitle1</marc:subfield>\n" +
		"\t<marc:subfield code=\"l\">yellowtitle2</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">yellowtitle3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">yellow agent</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"711\" ind1=\" \" ind2=\" \">\n" +
		"\t<marc:subfield code=\"a\">redcreator1</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">redcreator2</marc:subfield>\n" +
		"\t<marc:subfield code=\"c\">redcreator3 tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"k\">redtitle1</marc:subfield>\n" +
		"\t<marc:subfield code=\"l\">redtitle2</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">redtitle3</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">red agent</marc:subfield>\n" +
		"</marc:datafield>\n" +*/

		/*"<marc:datafield tag=\"710\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"t\">blue tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">blue identifier of work</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">1\\c</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">5\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"710\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"t\">green tea</marc:subfield>\n" +
		"\t<marc:subfield code=\"a\">green</marc:subfield>\n" +
		"\t<marc:subfield code=\"0\">green identifier of work</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">3\\c</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">6\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"959\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"b\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"4\">blue</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">5\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"959\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">green</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">6\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"959\" ind1=\" \" ind2=\"2\">\n" +
		"\t<marc:subfield code=\"a\">yellow</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">7\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"959\" ind1=\"1\" ind2=\"0\">\n" +
		"\t<marc:subfield code=\"a\">orange</marc:subfield>\n" +
		"\t<marc:subfield code=\"n\">orange</marc:subfield>\n" +
		"\t<marc:subfield code=\"m\">orange</marc:subfield>\n" +
		"\t<marc:subfield code=\"p\">orange</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">8\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +

		"<marc:datafield tag=\"959\" ind1=\"1\" ind2=\"0\">\n" +
		"\t<marc:subfield code=\"a\">good</marc:subfield>\n" +
		"\t<marc:subfield code=\"n\">good</marc:subfield>\n" +
		"\t<marc:subfield code=\"m\">good</marc:subfield>\n" +
		"\t<marc:subfield code=\"8\">9\\c</marc:subfield>\n" +
		"</marc:datafield>\n" +*/

		"</marc:record>";

	/**
	 * An Object used to read properties from the configuration file for the Metadata Services Toolkit
	 */
	protected static final Configuration configuration = ConfigurationManager.getConfiguration();

	/**
	 * Manager for getting, inserting and updating records
	 */
	private static RecordService recordService = new DefaultRecordService();

	// Set the location of the logger's configuration file
	static
	{
		PropertyConfigurator.configure(configuration.getProperty(Constants.CONFIG_LOGGER_CONFIG_FILE_LOCATION));
	}

	public static void main(String[] args) throws DataException, IndexException
	{
		try
		{
			addUnprocessedRecord();
			// TODO removed IndexManager
			// IndexManager.getInstance().maybeReOpen();

			System.out.println(System.currentTimeMillis());
			MetadataServiceFactory sf = new MetadataServiceFactory();
			MetadataService ms = sf.getService(2);
			ms.runService(2, -1);
			System.out.println(System.currentTimeMillis());

			// TODO removed IndexManager
			// IndexManager.getInstance().maybeReOpen();

			List<Record> records = recordService.getAll();
			for(Record record: records)
			{
				System.out.println("Found record:\n" + record.getOaiXml() + "\n\n\n\n");
				recordService.delete(record);
			}

			//System.out.println(System.getProperty("user.dir"));
		}
		catch(Exception e)
		{
			e.printStackTrace();

			List<Record> records = recordService.getAll();
			for(Record record: records)
			{
				System.out.println("Found record:\n" + record.getOaiXml() + "\n\n\n\n");
				recordService.delete(record);
			}
		}
		finally
		{
			// TODO removed IndexManager
			//IndexManager.closeIndex();
		}
	}

	public static void addUnprocessedRecord() throws DataException, IndexException
	{
		Record record = new Record();

		record.setOaiXml(UNNORMALIZED_RECORD);
		record.setFormat(new Format());
		record.getFormat().setId(1);
		record.setProvider(new Provider());
		record.getProvider().setId(1);
		record.setOaiIdentifier("12345");
		record.setService(new Service());
		record.getService().setId(1);
		record.addInputForService(new Service());
		record.getInputForServices().get(0).setId(2);
		recordService.insert(record);
	}
}
