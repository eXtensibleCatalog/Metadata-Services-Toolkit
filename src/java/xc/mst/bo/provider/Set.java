/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.provider;

/**
 * Represents a set
 *
 * @author Eric Osisek
 */
public class Set
{
	/**
	 * The set's ID
	 */
	private int id = -1;

	/**
	 * The set's display name
	 */
	private String displayName = null;

	/**
	 * The set's description
	 */
	private String description = null;

	/**
	 * The set's setSpec
	 */
	private String setSpec = null;

	/**
	 * True if the set belongs to a provider, false otherwise
	 */
	private boolean providerSet = false;

	/**
	 * True if the set was created by the MST for records it outputs, false otherwise
	 */
	private boolean recordSet = false;

	/**
	 * Gets the set's ID
	 *
	 * @return The set's ID
	 */
	public int getId()
	{
		return id;
	} // end method getId()

	/**
	 * Sets the set's ID
	 *
	 * @param id The set's new ID
	 */
	public void setId(int id)
	{
		this.id = id;
	} // end method setId(int)

	/**
	 * Gets the set's display name
	 *
	 * @return The set's display name
	 */
	public String getDisplayName()
	{
		return displayName;
	} // end method getDisplayName()

	/**
	 * Sets the set's display name
	 *
	 * @param displayName The set's new display name
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	} // end method setDisplayName(String)

	/**
	 * Gets the set's description
	 *
	 * @return The set's description
	 */
	public String getDescription()
	{
		return description;
	} // end method getDescription()

	/**
	 * Sets the set's description
	 *
	 * @param description The set's new description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	} // end method setDescription(String)

	/**
	 * Gets the set's setSpec
	 *
	 * @return The set's setSpec
	 */
	public String getSetSpec()
	{
		return setSpec;
	} // end method getSetSpec()

	/**
	 * Sets the set's setSpec
	 *
	 * @param setSpec The set's new setSpec
	 */
	public void setSetSpec(String setSpec)
	{
		this.setSpec = setSpec;
	} // end method setSetSpec(String)

	/**
	 * Checks whether or not the set belongs to a provider
	 *
	 * @return True if the set belongs to a provider, false otherwise
	 */
	public boolean getIsProviderSet()
	{
		return providerSet;
	} // end method getIsProviderSet()

	/**
	 * Sets whether or not the set belongs to a provider
	 *
	 * @param providerSet True if the set belongs to a provider, false otherwise
	 */
	public void setIsProviderSet(boolean providerSet)
	{
		this.providerSet = providerSet;
	} // end method setIsProviderSet(boolean)

	/**
	 * Checks whether or not the set was created by the MST for records it outputs
	 *
	 * @return True if the set was created by the
	 *         MST for records it outputs, false otherwise
	 */
	public boolean getIsRecordSet()
	{
		return recordSet;
	} // end method getIsRecordSet()

	/**
	 * Sets whether or not the set was created by the MST for records it outputs
	 *
	 * @param recordSet True if the set was created by the
	 *                  MST for records it outputs, false otherwise
	 */
	public void setIsRecordSet(boolean recordSet)
	{
		this.recordSet = recordSet;
	} // end method setIsRecordSet(boolean)

	@Override
	public boolean equals(Object o)
	{
		if(o == null || !(o instanceof Set))
			return false;

		Set other = (Set)o;

		return other.setSpec.equals(this.setSpec);
	} // end method equals(Object)
} // end class Set
