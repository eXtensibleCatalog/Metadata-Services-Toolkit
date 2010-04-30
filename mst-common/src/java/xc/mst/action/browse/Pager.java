/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  */

package xc.mst.action.browse;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This is an abstract class for Pager.
 * 
 * Number of pages to be displayed and number of rows in a page can be customized
 * by overriding these parameters in the extending class.
 * 
 * @author Sharmila Ranganathan
 *
 */
public abstract class Pager extends ActionSupport {

	/** Eclipse generated Id */
	private static final long serialVersionUID = -3953265958290535965L;

	/** Starting row number to get the result */
	protected int rowStart = 0;
	
	/** number of results to show per page */
	protected int numberOfResultsToShow = 20;
	
	/** number of pages to show  */
	protected int numberOfPagesToShow = 10;
	
	/** Current page number this is displayed */
	protected int currentPageNumber = 1;
	
	/** The page number to start the display */
	protected int startPageNumber = 1;

	/**
	 *  Get the total number of rows.
	 *  This method has to be implemented.
	 *  
	 * @return
	 */
	public abstract long getTotalHits() ;
	
	/**
	 * Get the start row number to get the data
	 *  
	 * @return
	 */
	public int getRowStart() {
		return rowStart;
	}

	/**
	 * Set the start row number to get the data
	 * 
	 * @param rowStart
	 */
	public void setRowStart(int rowStart) {
		this.rowStart = rowStart;
	}

	/**
	 * Get the number of results to show per page
	 * 
	 * @return
	 */
	public int getNumberOfResultsToShow() {
		return numberOfResultsToShow;
	}

	/**
	 * Get the number of pages to show
	 * 
	 * @return
	 */
	public int getNumberOfPagesToShow() {
		return numberOfPagesToShow;
	}

	/**
	 * Get current page number
	 * 
	 * @return
	 */
	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	/**
	 * Set current page number
	 * 
	 * @param currentPageNumber
	 */
	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	/**
	 * Get page number to start the display with
	 * 
	 * @return
	 */
	public int getStartPageNumber() {
		return startPageNumber;
	}

	/**
	 * Set page number to start the display with
	 * 
	 * @param startPageNumber
	 */
	public void setStartPageNumber(int startPageNumber) {
		this.startPageNumber = startPageNumber;
	}

	
	
}
