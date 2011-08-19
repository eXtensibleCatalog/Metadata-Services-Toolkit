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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xc.mst.bo.record.Record;
import xc.mst.dao.DataException;
import xc.mst.dao.provider.DefaultFormatDAO;
import xc.mst.dao.provider.DefaultProviderDAO;
import xc.mst.dao.provider.FormatDAO;
import xc.mst.dao.provider.ProviderDAO;
import xc.mst.dao.service.DefaultServiceDAO;
import xc.mst.dao.service.ServiceDAO;
import xc.mst.manager.IndexException;
import xc.mst.manager.record.DefaultRecordService;
import xc.mst.manager.record.RecordService;
import xc.mst.services.MetadataService;
import xc.mst.services.MetadataServiceFactory;
import xc.mst.utils.MSTConfiguration;
import xc.mst.utils.index.SolrIndexManager;

public class TestServices {
    /**
     * An Object used to read properties from the configuration file for the Metadata Services Toolkit
     */
    protected static ApplicationContext applicationContext = null;
    protected static boolean IN_USE = false;

    static {
        if (IN_USE)
            applicationContext = new ClassPathXmlApplicationContext(new String[] { "spring-mst.xml" });
    }

    private static File unprocessedRecordsDir = new File("C:\\NormalizationTestData\\input");
    private static File processedRecordsDir = new File("C:\\NormalizationTestData\\output");

    private static int serviceId = 1;

    /**
     * Builds the XML Document based on the record's OAI XML
     */
    private static SAXBuilder builder = new SAXBuilder();

    /**
     * Used to format timestamps for the results of the tests
     */
    private static DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    public static void main(String[] args) throws DataException, IOException, JDOMException, IndexException {
        RecordService recordService = (RecordService) MSTConfiguration.getInstance().getBean("RecordService");
        SolrIndexManager solrIndexManager = (SolrIndexManager) MSTConfiguration.getInstance().getBean("SolrIndexManager");

        try {
            addUnprocessedRecordFromFiles(unprocessedRecordsDir);
            // addUnprocessedRecordFromFilesForAggregation(unprocessedRecordsDir);
            Thread.sleep(2000);
            solrIndexManager.commitIndex();
            Thread.sleep(2000);
            System.out.println(formatter.format(new Date()));
            MetadataServiceFactory sf = new MetadataServiceFactory();
            MetadataService ms = sf.getService(serviceId);
            ms.runService(serviceId, -1);
            System.out.println(formatter.format(new Date()));
            Thread.sleep(2000);
            solrIndexManager.commitIndex();
            Thread.sleep(2000);
            // RecordList records = recordService.getAll();
            List<Record> records = recordService.getByServiceId(serviceId);
            System.out.println("O/P records =" + records);
            for (Record record : records) {
                saveRecordToFile(processedRecordsDir, record);

                recordService.delete(record);
            }
        } catch (Exception e) {
            e.printStackTrace();

            // RecordList records = recordService.getAll();
            // for(Record record: records)
            // {
            // if(record.getService().getId() == serviceId)
            // saveRecordToFile(processedRecordsDir, record);
            // recordService.delete(record);
            // }
        } finally {
            // try
            // {
            // Thread.sleep(2000);
            // SolrIndexManager.getInstance().commitIndex();
            // Thread.sleep(2000);
            // }
            // catch (InterruptedException e)
            // {
            // e.printStackTrace();
            // }
        }
    }

    public static void addUnprocessedRecordFromFiles(File inputDirectory) throws DataException, IOException, IndexException {
        ProviderDAO providerDao = new DefaultProviderDAO();
        FormatDAO formatDao = new DefaultFormatDAO();
        ServiceDAO serviceDao = new DefaultServiceDAO();
        RecordService recordService = new DefaultRecordService();

        File[] testRecords = inputDirectory.listFiles();

        for (int counter = 0; counter < testRecords.length; counter++) {
            File currentRecord = testRecords[counter];

            Record record = new Record();

            record.setOaiXml(readUnicodeFile(currentRecord));
            // record.setOaiIdentifier(currentRecord.getName().substring(0, currentRecord.getName().lastIndexOf('.')).replaceAll(" ", "/").replaceAll("-", ":"));
            record.setFormat(formatDao.getById(3)); // For Normalization & transformation
            record.setProvider(providerDao.getById(1));
            record.addInputForService(serviceDao.getById(serviceId));
            System.out.println("currentRecord.getName()=" + currentRecord.getName());

            // record.setDeleted(true);

            if (recordService.insert(record) == false)
                System.out.println("FAIL! " + currentRecord.getAbsolutePath());
        }
    }

    public static void addUnprocessedRecordFromFilesForAggregation(File inputDirectory) throws DataException, IOException, IndexException {
        ProviderDAO providerDao = new DefaultProviderDAO();
        FormatDAO formatDao = new DefaultFormatDAO();
        ServiceDAO serviceDao = new DefaultServiceDAO();
        RecordService recordService = new DefaultRecordService();

        File[] testRecords = inputDirectory.listFiles();

        for (int counter = 0; counter < testRecords.length; counter++) {
            File currentRecord = testRecords[counter];

            Record record = new Record();

            record.setOaiXml(readUnicodeFile(currentRecord));
            record.setFormat(formatDao.getById(6)); // For aggregation
            record.setProvider(providerDao.getById(1));
            record.addInputForService(serviceDao.getById(serviceId));
            System.out.println("currentRecord.getName()=" + currentRecord.getName());

            /*
            if (currentRecord.getName().equals("1.xml")) {
                record.setType("XC-Work");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10000");

            }
            if (currentRecord.getName().equals("2.xml")) {
                record.setType("XC-Expression");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10001");
                record.addUpLink("oai:mst.rochester.edu:MST/MARCToXCTransformation/10000");
            }
            if (currentRecord.getName().equals("3.xml")) {
                record.setType("XC-Manifestation");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10002");
                record.addUpLink("oai:mst.rochester.edu:MST/MARCToXCTransformation/10001");
            }
            if (currentRecord.getName().equals("4.xml")) {
                record.setType("XC-Work");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10003");
            }
            if (currentRecord.getName().equals("5.xml")) {
                record.setType("XC-Expression");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10004");
                record.addUpLink("oai:mst.rochester.edu:MST/MARCToXCTransformation/10003");
            }
            if (currentRecord.getName().equals("6.xml")) {
                record.setType("XC-Manifestation");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10005");
                record.addUpLink("oai:mst.rochester.edu:MST/MARCToXCTransformation/10004");
            }
            if (currentRecord.getName().equals("7.xml")) {
                record.setType("XC-Holding");
                record.setOaiIdentifier("oai:mst.rochester.edu:MST/MARCToXCTransformation/10006");
                record.addUpLink("oai:mst.rochester.edu:MST/MARCToXCTransformation/10005");
            }
            */
            // record.setDeleted(true);

            if (recordService.insert(record) == false)
                System.out.println("FAIL! " + currentRecord.getAbsolutePath());
        }
    }

    /**
     * The readInputFile method did not handle unicode correctly. I found this method at
     * http://www.devarticles.com/c/a/Java/J2ME-and-Unicode/3/ which should work with unicode
     * characters.
     * 
     * Reads the input file into a String and return it
     * 
     * @param file
     *            The file to read
     * @return The contents of the file
     */
    private static String readUnicodeFile(File file) {
        StringBuffer buffer = null;

        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(file), "UTF-8");

            buffer = new StringBuffer();
            int ch;
            while ((ch = isr.read()) > -1) {
                buffer.append((char) ch);
            }
            if (isr != null)
                isr.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return buffer.toString();
    }

    static int counter = 0;

    @SuppressWarnings("unchecked")
    private static void saveRecordToFile(File directory, Record record) throws IOException, JDOMException {
        String xml = record.getOaiXml();
        OutputStreamWriter writer = null;
        File outfile = null;

        // String controlNumber = runRegex(xml, "<marc:controlfield tag=\"001\">(\\d*)</marc:controlfield>", 1);
        //
        // if(controlNumber == null)
        // {
        // // An XPATH expression to get the recordIDs
        // XPath xpath = XPath.newInstance("//xc:entity[@type='manifestation']/xc:recordID[@type='NRU']");
        //
        // List<Element> elements = xpath.selectNodes(builder.build(new InputSource(new StringReader(record.getOaiXml()))));
        //
        // if(elements == null || elements.size() == 0)
        // controlNumber = "" + ++counter;
        // else
        // {
        // for(Element element : elements)
        // {
        // if(controlNumber == null)
        // controlNumber = element.getText();
        // else
        // controlNumber = controlNumber + "_" + element.getText();
        // }
        // }
        // }

        try {
            String fileName = directory.getAbsolutePath() + "\\" + record.getOaiIdentifier() + ".xml";
            fileName = fileName.replaceAll("/", " ");
            fileName = fileName.replaceAll(":", "-");
            fileName = fileName.replaceAll("-\\\\", ":\\\\");
            outfile = new File(fileName);
            outfile.createNewFile();

            writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)), "UTF-8");

            writer.write(xml);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    // **********************************************************
    // Private methods to run regular expressions
    // **********************************************************

    /**
     * A helper method to run a regular expression against a String. If the
     * regex matches the text, the content of the requested group will be
     * returned, otherwise this method will return null.
     * 
     * @param text
     *            The text to run the regex against.
     * @param regex
     *            A regular expression with at least one group.
     * @param groupNum
     *            The group to return.
     * @return The contents of the first group in the first match, or null if there
     *         were no matches.
     */
    private static String runRegex(String text, String regex, int groupNum) {
        // Create a Pattern based on the passed regex string
        Pattern regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // Run the Pattern against the passed text string
        Matcher regexMatcher = regexPattern.matcher(text);

        StringBuilder result = new StringBuilder();

        // Return the first group if any matches were found,
        // otherwise return null
        while (regexMatcher.find())
            result.append(regexMatcher.group(groupNum)).append('_');

        if (result.length() == 0)
            return null;

        return (result.substring(0, result.length() - 1));
    }
}
