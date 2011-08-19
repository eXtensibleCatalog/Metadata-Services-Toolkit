/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.bo.gui;

public class Page {
    /**
     * The page's ID
     */
    private int id = -1;

    /**
     * The page's page number
     */
    private int pageNumber = -1;

    /**
     * The page's name
     */
    private String name = null;

    /**
     * The page's title
     */
    private String title = null;

    /**
     * Gets the page's ID
     * 
     * @return The page's ID
     */
    public int getId() {
        return id;
    } // end method getId()

    /**
     * Sets page's ID
     * 
     * @param id
     *            The page's new ID
     */
    public void setId(int id) {
        this.id = id;
    } // end method setId(int)

    /**
     * Gets the page's number
     * 
     * @return The page's number
     */
    public int getPageNumber() {
        return pageNumber;
    } // end method getPageNumber()

    /**
     * Sets the page's page number
     * 
     * @param pageNumber
     *            The page's new page number
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    } // end method setPageNumber(int)

    /**
     * Gets the page's name
     * 
     * @return The page's name
     */
    public String getName() {
        return name;
    } // end method getName()

    /**
     * Sets the page's name
     * 
     * @param name
     *            The page's new name
     */
    public void setName(String name) {
        this.name = name;
    } // end method setName(String)

    /**
     * Gets the page's title
     * 
     * @return The page's title
     */
    public String getTitle() {
        return title;
    } // end method getTitle()

    /**
     * Sets the page's title
     * 
     * @param title
     *            The page's new title
     */
    public void setTitle(String title) {
        this.title = title;
    } // end method setTitle(String)
} // end class Page
