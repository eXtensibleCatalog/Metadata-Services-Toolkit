/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.record;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a record in the "Item" bucket used by the Aggregation Service
 *
 * @author Eric Osisek
 */
public class Item extends Record
{
    /**
     * The type of indexed Object this is
     */
    public static String indexedObjectType = "item";

    /**
     * The trait for the holdingsExemplified element in the XC schema
     */
    public final static String TRAIT_HOLDINGS_EXEMPLIFIED = "holdingsExemplified";

    /**
     * Get's the indexed object type of this class.  This is used to differentiate between
     * different types of objects stored in the index.
     *
     * @return The type of indexed object for this Object
     */
    public String getType()
    {
        return Item.indexedObjectType;
    } // end method getIndexedObjectType()

    /**
     * Adds an holdingsExemplified trait to the item
     *
     * @param type The type attribute of the holdingsExemplified element
     * @param value The value of the holdingsExemplified element
     */
    public void addHoldingsExemplified(String type, String value)
    {
        addTrait(TRAIT_HOLDINGS_EXEMPLIFIED + ":(" + type + ")" + value);
    } // end method addHoldingsExemplified(String, String)

    /**
     * Adds an holdingsExemplified trait to the item
     *
     * @param holdingsExemplified The holdingsExemplified element in the format (<type>)<value>
     */
    public void addHoldingsExemplified(String holdingsExemplified)
    {
        addTrait(TRAIT_HOLDINGS_EXEMPLIFIED + ":" + holdingsExemplified);
    } // end method addHoldingsExemplified(String)

    /**
     * Removes an holdingsExemplified trait from the item
     *
     * @param type The type attribute of the holdingsExemplified element
     * @param value The value of the holdingsExemplified element
     */
    public void removeHoldingsExemplified(String type, String value)
    {
        removeTrait(TRAIT_HOLDINGS_EXEMPLIFIED + ":(" + type + ")" + value);
    } // end method removeHoldingsExemplified(String, String)

    /**
     * Removes an holdingsExemplified trait from the item
     *
     * @param holdingsExemplified The holdingsExemplified element in the format (<type>)<value>
     */
    public void removeHoldingsExemplified(String holdingsExemplified)
    {
        removeTrait(TRAIT_HOLDINGS_EXEMPLIFIED + ":" + holdingsExemplified);
    } // end method removeHoldingsExemplified(String)

    /**
     * Gets a list of holdingsExemplified traits on the work
     *
     * @returns A list of xc:holdingsExemplified elements for this work
     */
    public List<String> getHoldingsExemplified()
    {
        List<String> results = new ArrayList<String>();

        for(String trait : getTraits())
            if(trait.startsWith(TRAIT_HOLDINGS_EXEMPLIFIED))
                results.add(trait.substring(trait.indexOf(':')+1));

        return results;
    } // end method getHoldingsExemplified()

    /**
     * Adds an up link to the passed holdings element
     *
     * @param holdings The holdings element to link to
     */
    public void addLinkToHoldings(Holdings holdings)
    {
//		addUpLink(holdings);
    } // end method addLinkToHoldings(Holdings)

    /**
     * Removes an up link to the passed holdings element
     *
     * @param expression The holdings element to remove the link to
     */
    public void removeLinkToHoldings(Holdings holdings)
    {
//		removeUpLink(holdings);
    } // end method removeLinkToHoldings(Holdings)

    /**
     * Builds an Item with the same fields as the passed Record
     *
     * @param record The Record to copy
     * @return An item with the same fields as the passed Record
     */
    public static Item buildItemFromRecord(Record record)
    {
        Item item = new Item();

        item.setCreatedAt(record.getCreatedAt());
        item.setDeleted(record.getDeleted());
        item.setMessages(record.getMessages());
        item.setFormat(record.getFormat());
        item.setHarvest(record.getHarvest());
        item.setId(record.getId());
        item.setInputForServices(record.getInputForServices());
        item.setOaiDatestamp(record.getOaiDatestamp());
        item.setOaiHeader(record.getOaiHeader());
        item.setOaiXml(record.getOaiXml());
        item.setProcessedByServices(record.getProcessedByServices());
        item.setProcessedFrom(record.getProcessedFrom());
        item.setProvider(record.getProvider());
        item.setService(record.getService());
        item.setSets(record.getSets());
        item.setTraits(record.getTraits());
        item.setUpdatedAt(record.getUpdatedAt());
        item.setUpLinks(record.getUpLinks());

        return item;
    } // end method buildItemFromRecord(Record)
} // end class Item
