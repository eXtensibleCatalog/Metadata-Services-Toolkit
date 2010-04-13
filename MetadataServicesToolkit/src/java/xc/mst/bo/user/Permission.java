/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.user;

/**
 * Represents a top level tab which a group has permission to access
 *
 * @author Eric Osisek
 */
public class Permission
{
	/**
	 * The ID of the top level tab that the user can access
	 */
	private int tabId = -1;

	/**
	 * The name of the top level tab that the user can access
	 */
	private String tabName = null;

	/**
	 * The oders of the top level tab 
	 */
	private int tabOrder;
	
	/**
	 * Gets the value of the Top Level Tab ID
	 *
	 * @return The Top Level Tab ID
	 */
	public int getTabId()
	{
		return tabId;
	} // end method getTabId()

	/**
	 * Sets the value of the Top Level Tab ID
	 *
	 * @param topLevelTabId The new value for the Top Level Tab ID
	 */
	public void setTabId(int topLevelTabId)
	{
		this.tabId = topLevelTabId;
	} // end method setTabId(int)

	/**
	 * Gets the value of the Tab Name
	 *
	 * @return The tab name
	 */
	public String getTabName()
	{
		return tabName;
	} // end method getTabName()

	/**
	 * Sets the value of the Tab name
	 *
	 * @param tabName The new value for the tab name
	 */
	public void setTabName(String tabName)
	{
		this.tabName = tabName;
	} // end method setTabName(String)

	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Permission))
			return false;

		Permission other = (Permission)o;

		return (other.tabId == this.tabId && ((other.tabName == null && this.tabName == null) || other.tabName.equals(this.tabName)));
	} // end method equals(Object)

	public int getTabOrder() {
		return tabOrder;
	}

	public void setTabOrder(int tabOrder) {
		this.tabOrder = tabOrder;
	}
} // end class Permission
