/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.PropertyConfigurator;
import org.jconfig.Configuration;
import org.jconfig.ConfigurationManager;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.record.Record;
import xc.mst.constants.Constants;
import xc.mst.dao.DataException;
import xc.mst.dao.MySqlConnectionManager;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceFactory;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.index.RecordList;

public class TestNormalizationService
{
    private static String UNNORMALIZED_RECORD =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<marc:record xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-insance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim  http://www.log.gov/standards/marcxml/schema/MARC21slim.xsd\">\n\n" +

        "<marc:leader>01056nac  a2200229n  4500</marc:leader>\n\n" +

        "<marc:controlfield tag=\"001\">1050000</marc:controlfield>\n" +
        "<marc:controlfield tag=\"003\">AOE</marc:controlfield>\n" +
        "<marc:controlfield tag=\"006\">c                      </marc:controlfield>\n" +
        "<marc:controlfield tag=\"007\">ko                     </marc:controlfield>\n" +
        "<marc:controlfield tag=\"008\">840516c19999999ab|    j   m |a aa1ajpn  </marc:controlfield>\n" +
        "<marc:controlfield tag=\"005\">19961213000000.0</marc:controlfield>\n\n" +

        "<marc:datafield tag=\"010\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">n  84032254 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"020\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">12345678(lalala)</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">123456789</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">ocm</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">(OCoLC)ocm123456789</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">ocm123456789</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"035\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"9\">ocm987654</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"040\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">DLC</marc:subfield>\n" +
        "\t<marc:subfield code=\"c\">DLC</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">DLC</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"041\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">eng</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">jpn</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"100\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">100_a</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">100_d</marc:subfield>\n" +
        "\t<marc:subfield code=\"q\">100_q</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">act</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"110\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">110_a</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">110_d</marc:subfield>\n" +
        "\t<marc:subfield code=\"q\">110_q</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">blah</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"245\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">contents of 245</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d of 245</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">d. 1388 </marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"400\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"440\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">440a</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">440n</marc:subfield>\n" +
        "\t<marc:subfield code=\"p\">440p</marc:subfield>\n" +
        "\t<marc:subfield code=\"v\">440v</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"502\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">Banana</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"600\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">good600</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">good600</marc:subfield>\n" +
        "\t<marc:subfield code=\"t\">bad600</marc:subfield>\n" +
        "\t<marc:subfield code=\"y\">bad600</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">good600</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"600\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">good600</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">good600</marc:subfield>\n" +
        "\t<marc:subfield code=\"t\">bad600</marc:subfield>\n" +
        "\t<marc:subfield code=\"y\">bad600</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">good600</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        /*"<marc:datafield tag=\"610\" ind1=\" \" ind2=\"2\">\n" +
        "\t<marc:subfield code=\"a\">bad610</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">bad610</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">bad610</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"611\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">bad</marc:subfield>\n" +
        "</marc:datafield>\n\n" +*/

        /*"<marc:datafield tag=\"655\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">bad655</marc:subfield>\n" +
        "\t<marc:subfield code=\"2\">2_655</marc:subfield>\n" +
        "\t<marc:subfield code=\"5\">NRU</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"655\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">good655</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">good655</marc:subfield>\n" +
        "\t<marc:subfield code=\"e\">good655</marc:subfield>\n" +
        "\t<marc:subfield code=\"y\">bad655</marc:subfield>\n" +
        "\t<marc:subfield code=\"v\">bad655</marc:subfield>\n" +
        "\t<marc:subfield code=\"x\">good655_x</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"656\" ind1=\"0\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">boo</marc:subfield>\n" +
        "\t<marc:subfield code=\"c\">boo</marc:subfield>\n" +
        "\t<marc:subfield code=\"8\">2\\c</marc:subfield>\n" +
        "\t<marc:subfield code=\"y\">boo</marc:subfield>\n" +
        "\t<marc:subfield code=\"v\">yay1</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"670\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">blah</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">blah</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"670\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">LC manual auth. cd.</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">blah</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"700\" ind1=\" \" ind2=\"2\">\n" +
        "\t<marc:subfield code=\"8\">2\\c</marc:subfield>\n" +
        "\t<marc:subfield code=\"a\">red</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">red</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"710\" ind1=\" \" ind2=\"2\">\n" +
        "\t<marc:subfield code=\"a\">blue</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">blue</marc:subfield>\n" +
        "\t<marc:subfield code=\"t\">blue tea</marc:subfield>\n" +
        "\t<marc:subfield code=\"0\">blue identifier of work</marc:subfield>\n" +
        "\t<marc:subfield code=\"4\">blue</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"710\" ind1=\" \" ind2=\"2\">\n" +
        "\t<marc:subfield code=\"t\">green tea</marc:subfield>\n" +
        "\t<marc:subfield code=\"0\">green identifier of work</marc:subfield>\n" +
        "\t<marc:subfield code=\"a\">green</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"711\" ind1=\" \" ind2=\"2\">\n" +
        "\t<marc:subfield code=\"t\">yellow tea</marc:subfield>\n" +
        "\t<marc:subfield code=\"a\">yellow</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"730\" ind1=\" \" ind2=\" \">\n" +
        "\t<marc:subfield code=\"a\">good</marc:subfield>\n" +
        "\t<marc:subfield code=\"b\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"d\">good</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"800\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">orange</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">orange</marc:subfield>\n" +
        "\t<marc:subfield code=\"m\">orange</marc:subfield>\n" +
        "\t<marc:subfield code=\"p\">orange</marc:subfield>\n" +
        "\t<marc:subfield code=\"t\">orange tea</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"810\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"m\">bad</marc:subfield>\n" +
        "\t<marc:subfield code=\"p\">bad</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"811\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">good</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">good</marc:subfield>\n" +
        "\t<marc:subfield code=\"m\">good</marc:subfield>\n" +
        "\t<marc:subfield code=\"t\">good</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"852\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">811a</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">sm</marc:subfield>\n" +
        "\t<marc:subfield code=\"l\">207</marc:subfield>\n" +
        "\t<marc:subfield code=\"p\">811p</marc:subfield>\n" +
        "</marc:datafield>\n\n" +

        "<marc:datafield tag=\"852\" ind1=\"1\" ind2=\"0\">\n" +
        "\t<marc:subfield code=\"a\">811a</marc:subfield>\n" +
        "\t<marc:subfield code=\"n\">amp</marc:subfield>\n" +
        "\t<marc:subfield code=\"l\">98</marc:subfield>\n" +
        "\t<marc:subfield code=\"p\">811p</marc:subfield>\n" +
        "</marc:datafield>\n\n" +*/

        "</marc:record>";

    /**
     * An Object used to read properties from the configuration file for the Metadata Services Toolkit
     */
    protected static final Configuration configuration = ConfigurationManager.getConfiguration();

    /**
     * Manager for getting, inserting and updating records
     */
    private static RecordService recordService = new DefaultRecordService();

    /**
     * Used to format timestamps for the results of the tests
     */
    private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    // Set the location of the logger's configuration file
    static
    {
        PropertyConfigurator.configure(configuration.getProperty(Constants.CONFIG_LOGGER_CONFIG_FILE_LOCATION));
    }

    public static void main(String[] args) throws DataException, IOException, IndexException
    {
        try
        {

            System.out.println(formatter.format(System.currentTimeMillis()));
            MetadataServiceFactory sf = new MetadataServiceFactory();
            MetadataService ms = sf.getService(1);
            ms.runService(1, -1);
            System.out.println(formatter.format(System.currentTimeMillis()));

            RecordService recordService = (RecordService)MSTConfiguration.getInstance().getBean("RecordService");
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
            MySqlConnectionManager.getInstance().closeDbConnection();
        }
    }

    public static void addUnprocessedRecordFromFiles(File inputDirectory) throws DataException, IOException, IndexException
    {
        File[] testRecords = inputDirectory.listFiles();

        for(int counter = 0; counter < testRecords.length; counter++)
        {
            File currentRecord = testRecords[counter];


            Record record = new Record();

            record.setOaiXml(readUnicodeFile(currentRecord));
            record.setFormat(new Format());
            record.getFormat().setId(1);
            record.setProvider(new Provider());
            record.getProvider().setId(1);
            recordService.insert(record);
        }
    }

    /**
     * The readInputFile method did not handle unicode correctly.  I found this method at
     * http://www.devarticles.com/c/a/Java/J2ME-and-Unicode/3/ which should work with unicode
     * characters.
     *
     * Reads the input file into a String and return it
     *
     * @param file The file to read
     * @return The contents of the file
     */
    private static String readUnicodeFile(File file)
    {
         StringBuffer buffer = null;

         InputStreamReader isr = null;
         try {
           isr = new InputStreamReader(new FileInputStream(file), "UTF-8");

           buffer = new StringBuffer();
           int ch;
           while ((ch = isr.read()) > -1) {
             buffer.append((char)ch);
           }
           if (isr != null)
             isr.close();
         } catch (Exception ex) {
           System.out.println(ex);
         }
         return buffer.toString();
       }

    public static void saveRecordToFile(File directory, Record record) throws IOException
    {
        String xml = record.getOaiXml();
        OutputStreamWriter writer = null;
        File outfile = null;

        String controlNumber = runRegex(xml, "<marc:controlfield tag=\"001\">(\\d*)</marc:controlfield>", 1);

        if(controlNumber == null)
            return;

        try
        {
            outfile = new File(directory.getAbsolutePath() + MSTConfiguration.FILE_SEPARATOR + controlNumber + ".xml");
            outfile.createNewFile();

            writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)),"UTF-8");

            writer.write(xml);
        }
        finally
        {
            if(writer != null)
                writer.close();
        }
    }

    //**********************************************************
    // Private methods to run regular expressions
    //**********************************************************

    /**
     * A helper method to run a regular expression against a String.  If the
     * regex matches the text, the content of the requested group will be
     * returned, otherwise this method will return null.
     *
     * @param text The text to run the regex against.
     * @param regex A regular expression with at least one group.
     * @param groupNum The group to return.
     * @return The contents of the first group in the first match, or null if there
     *         were no matches.
     */
    private static String runRegex(String text, String regex, int groupNum)
    {
        // Create a Pattern based on the passed regex string
        Pattern regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // Run the Pattern against the passed text string
        Matcher regexMatcher = regexPattern.matcher(text);

        // Return the first group if any matches were found,
        // otherwise return null
        if(regexMatcher.find())
            return regexMatcher.group(groupNum);
        else
            return null;
    }
}

