/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.bo.provider;

/**
 * Represents a metadata format
 * 
 * @author Eric Osisek
 */
public class Format {
    /**
     * The format's ID
     */
    private int id = -1;

    /**
     * The format's name
     */
    private String name = null;

    /**
     * The format's namespace
     */
    private String namespace = null;

    /**
     * The format's schema location
     */
    private String schemaLocation = null;

    /**
     * Gets the format's ID
     * 
     * @return The format's ID
     */
    public int getId() {
        return id;
    } // end method getId()

    /**
     * Sets the format's ID
     * 
     * @param id
     *            The format's new ID
     */
    public void setId(int id) {
        this.id = id;
    } // end method setId(int)

    /**
     * Gets the format's name
     * 
     * @return The format's name
     */
    public String getName() {
        return name;
    } // end method getName()

    /**
     * Sets the format's name
     * 
     * @param name
     *            The format's new name
     */
    public void setName(String name) {
        this.name = name;
    } // end method setName(String)

    /**
     * Gets the format's namespace
     * 
     * @return The format's namespace
     */
    public String getNamespace() {
        return namespace;
    } // end method getNamespace()

    /**
     * Sets the format's namespace
     * 
     * @param namespace
     *            The format's new namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    } // end method setNamespace(String)

    /**
     * Gets the format's schema location
     * 
     * @return The format's schema location
     */
    public String getSchemaLocation() {
        return schemaLocation;
    } // end method getSchemaLocation()

    /**
     * Sets the format's schema location
     * 
     * @param schemaLocation
     *            The format's new schema location
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    } // end method setSchemaLocation(String)

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Format))
            return false;

        final Format other = (Format) o;

        if ((name != null && !name.equals(other.getName())) ||
                (name == null && other.getName() != null))
            return false;

        if ((namespace != null && !namespace.equals(other.getNamespace())) ||
                (namespace == null && other.getNamespace() != null))
            return false;

        if ((schemaLocation != null && !schemaLocation.equals(other.getSchemaLocation())) ||
                (schemaLocation == null && other.getSchemaLocation() != null))
            return false;

        return true;

    } // end method equals(Object)

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int value = 0;
        value += name == null ? 0 : name.hashCode();
        value += namespace == null ? 0 : namespace.hashCode();
        value += schemaLocation == null ? 0 : schemaLocation.hashCode();
        return value;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(" [Id=" + id);
        b.append(" name=" + name);
        b.append(" namespace=" + namespace);
        b.append(" schemaLocation=" + schemaLocation + "]");

        return b.toString();
    }
} // end class Format
