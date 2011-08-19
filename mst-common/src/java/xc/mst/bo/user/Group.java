/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group which a user belongs to
 *
 * @author Eric Osisek
 */
public class Group
{
    /**
     * The group's ID
     */
    private int id = -1;

    /**
     * The group's name
     */
    private String name = null;

    /**
     * A description of the group
     */
    private String description = null;

    /**
     * A list of permissions belonging to the group
     */
    private List<Permission> permissions = new ArrayList<Permission>();

    /**
     * The number of members belonging to the group
     */
    private int memberCount;

    /**
     * Default group name Administrator
     */
    public static final String ADMINISTRATOR = "Administrator";

    /**
     * Gets the group's ID
     *
     * @return The group's ID
     */
    public int getId()
    {
        return id;
    } // end method getId

    /**
     * Sets the group's ID
     *
     * @param id The group's new ID
     */
    public void setId(int id)
    {
        this.id = id;
    } // end method setId(int)

    /**
     * Gets the group's name
     *
     * @return The group's name
     */
    public String getName()
    {
        return name;
    } // end method getName()

    /**
     * Sets the group's name
     *
     * @param name The group's new name
     */
    public void setName(String name)
    {
        this.name = name;
    } // end method setName(String)

    /**
     * Gets the group's description
     *
     * @return The group's description
     */
    public String getDescription()
    {
        return description;
    } // end method getDescription()

    /**
     * Sets the group's description
     *
     * @param description The group's new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    } // end method setDescription(String)

    /**
     * Gets the permissions belonging to the group
     *
     * @return The group's permissions
     */
    public List<Permission> getPermissions()
    {
        return permissions;
    } // end method getPermissions()

    /**
     * Sets the permissions belonging to the group
     *
     * @param permissions A list of permissions for the group
     */
    public void setPermissions(List<Permission> permissions)
    {
        this.permissions = permissions;
    } // end method setPermissions(List<Permission>)

    /**
     * Adds a permission to the list of permissions belonging to the group
     *
     * @param permission The permission to add
     */
    public void addPermission(Permission permission)
    {
        if(!permissions.contains(permission))
            permissions.add(permission);
    } // end method addPermission

    /**
     * Removes a permission from the list of permissions belonging to the group
     *
     * @param permission The permission to remove
     */
    public void removePermission(Permission permission)
    {
        if(permissions.contains(permission))
            permissions.remove(permission);
    } // end method removePermission

    /**
     * removes all the permissions associated with a group
     */
    public void removeAllPermissions()
    {
        permissions.clear();
    }
    /**
     * sets the count for the number of members in a group
     *
     * @param memberCount member count
     */
    public void setMemberCount(int memberCount)
    {
        this.memberCount = memberCount;
    }

    public int getMemberCount()
    {
        return this.memberCount;
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Group))
            return false;

        Group other = (Group)o;

        return other.name.equals(this.name);
    } // end method equals(Object)
} // end class Group
