/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains a single main method which parses a marc:collection from a file and
 * writes each marc:record in the collection to a seperate xml file in the output directory.
 *
 * DISCLAIMER: I wrote this for my own use.  You're welcome to use it, but it's not production
 *             quality.  It contains potentially slow String manipulation and inserts whitespace
 *             based on the input I'm dealing with, which might be different from yours.  Furthermore,
 *             everything is static.  I'm not responsible for anything that happens if you use this
 *             program, up to and including your computer bursting into flames.
 *
 * @author Eric Osisek
 */
public class MarcXmlCollectionToFiles
{
    // The file containing the marc:collection
    private final String INPUT_FILE = "C:\\marcxmlUnsplit\\bibs_w_NRU.xml";

    // The directory to which the marcxml records should be written
    private final String OUTPUT_DIR = "C:\\marcxmlSplit";

    // The delimeter we use to mark where one marc:record ends and another begins
    private final String SPLIT_DELIMETER = "~SPLITSPLITSPLIT~";

    public static void main(String[] args) throws IOException
    {
        new MarcXmlCollectionToFiles();
    }

    public MarcXmlCollectionToFiles() throws IOException
    {
        // Read the marc:collection from the input file
        String marcCollection = readUnicodeFile();

        // Add whitespace to the marcCollection as appropriate
        // Also add delimeters between the records so we can split on them
        marcCollection = makePretty(marcCollection);

        // Split the collection into individual records
        String[] records = marcCollection.split(SPLIT_DELIMETER);

        // The directory to write the files to
        File directory = new File(OUTPUT_DIR);

        // Save each record
        for(String record : records)
            saveRecordToFile(directory, record);
    }

    /**
     * The readInputFile method did not handle unicode correctly.  I found this method at
     * http://www.devarticles.com/c/a/Java/J2ME-and-Unicode/3/ which should work with unicode
     * characters.
     *
     * Reads the input file into a String and return it
     *
     * @return The contents of the file
     */
    private String readUnicodeFile()
    {
        File currentRecord = new File(INPUT_FILE);

         StringBuffer buffer = null;

         InputStreamReader isr = null;
         try {
           isr = new InputStreamReader(new FileInputStream(currentRecord), "UTF-8");

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

    // Add whitespace to the marcCollection as appropriate
    // Also add delimeters between the records so we can split on them
    private String makePretty(String marcCollection)
    {
        // Get rid of the opening marc:collection element
        marcCollection = marcCollection.replaceAll("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><marc:collection xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">\n", "");

        // Get rid of the closing marc:collection element
        marcCollection = marcCollection.replaceAll("</marc:collection>", "");

        // Replace every opening marc:record tag with an XML header and an opening tag that defines all the namespaces used
        marcCollection = marcCollection.replaceAll("\n<marc:record", "<marc:record");
        marcCollection = marcCollection.replaceAll("<marc:record>", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n<marc:record xmlns:marc=\"http://www.loc.gov/MARC21/slim\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/MARC21/slim http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\">");

        // Replace every closing marc:record tag with a closing tag, following by a linebreak, followed by the split delimeter
        marcCollection = marcCollection.replaceAll("</marc:record>", "\n\n</marc:record>" + SPLIT_DELIMETER);

        // Add a tab before the marc leader
        marcCollection = marcCollection.replaceAll("<marc:leader>", "\n\n\t<marc:leader>");

        // Replace every opening  control field with a tab, followed by the tag
        marcCollection = marcCollection.replaceAll("\n<marc:controlfield", "<marc:controlfield");
        marcCollection = marcCollection.replaceAll("<marc:controlfield", "\n\t<marc:controlfield");

        // Add 2 line breaks and a tab before each datafield
        marcCollection = marcCollection.replaceAll("<marc:datafield", "\n\n\t<marc:datafield");

        // Add a line break and a tab before each datafield
        marcCollection = marcCollection.replaceAll("</marc:datafield", "\n\t</marc:datafield");

        // Add a line break and two tabs before each subfield
        marcCollection = marcCollection.replaceAll("<marc:subfield", "\n\t\t<marc:subfield");

        return marcCollection;
    }

    // Writes a marc record to a file
    private void saveRecordToFile(File directory, String xml) throws IOException
    {
        //BufferedWriter writer = null;
        OutputStreamWriter writer = null;
        File outfile = null;

        String controlNumber = runRegex(xml, "<marc:controlfield tag=\"001\">(\\d*)</marc:controlfield>", 1);

        if(controlNumber == null)
            return;

        try
        {
            outfile = new File(directory.getAbsolutePath() + "\\" + controlNumber + ".xml");
            outfile.createNewFile();

            writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outfile)),"UTF-8");

            writer.write(xml);

            //writer = new BufferedWriter(new FileWriter(outfile));
            //writer.write(xml);
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
    private String runRegex(String text, String regex, int groupNum)
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

