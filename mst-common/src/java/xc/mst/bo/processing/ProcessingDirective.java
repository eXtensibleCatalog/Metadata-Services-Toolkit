/**
  * Copyright (c) 2009 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.bo.processing;

import java.util.ArrayList;
import java.util.List;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.service.Service;

/**
 * Represents a processing directive
 *
 * @author Eric Osisek
 */
public class ProcessingDirective
{
    /**
     * The processing directive's id
     */
    private int id = -1;

    /**
     * The provider that can trigger the processing directive
     */
    private Provider sourceProvider = null;

    /**
     * The service that can trigger the processing directive
     */
    private Service sourceService = null;

    /**
     * The service_id column
     */
    private Service service = null;

    /**
     * The output_set_id column
     */
    private Set outputSet = null;

    /**
     * The maintain_source_sets column
     */
    private boolean maintainSourceSets = false;

    /**
     * A list of sets that should trigger this processing directive
     */
    private List<Set> triggeringSets = new ArrayList<Set>();

    /**
     * A list of formats that should trigger this processing directive
     */
    private List<Format> triggeringFormats = new ArrayList<Format>();

    /**
     * Gets the processing directive's ID
     *
     * @return The processing directive's ID
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets the value of the processing directive ID
     *
     * @param processingDirectiveId The new value for the processing directive ID
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Gets the provider which can trigger the processing directive
     *
     * @return The provider which can trigger the processing directive
     */
    public Provider getSourceProvider()
    {
        return sourceProvider;
    } // end method getSourceProvider()

    /**
     * Sets the provider which can trigger the processing directive
     *
     * @param sourceProvider The new provider which can trigger the processing directive
     */
    public void setSourceProvider(Provider sourceProvider)
    {
        this.sourceProvider = sourceProvider;
    } // end method setSourceProvider(Provider)

    /**
     * Gets the service which can trigger the processing directive
     *
     * @return The service which can trigger the processing directive
     */
    public Service getSourceService()
    {
        return sourceService;
    } // end method getSourceService()

    /**
     * Sets the service which can trigger the processing directive
     *
     * @param sourceService The new service which can trigger the processing directive
     */
    public void setSourceService(Service sourceService)
    {
        this.sourceService = sourceService;
    } // end method setSourceService(Service)

    /**
     * Gets the value of the service ID
     *
     * @return The service which will be run on records triggering the
     *         processing directive
     */
    public Service getService()
    {
        return service;
    } // end method getService()

    /**
     * Sets the service which will be run on records triggering the
     * processing directive
     *
     * @param service The new service which will be run on records triggering the
     *                processing directive
     */
    public void setService(Service service)
    {
        this.service = service;
    } // end method setServiceId(Service)

    /**
     * Gets the set to which records processed by the service from the
     * processing directive will be added
     *
     * @return The set to which records processed by the service from the
     *         processing directive will be added
     */
    public Set getOutputSet()
    {
        return outputSet;
    } // end method getOutputSet()

    /**
     * Sets the set to which records processed by the service from the
     * processing directive will be added
     *
     * @param outputSet The new set to which records processed by the service from the
     *                  processing directive will be added
     */
    public void setOutputSet(Set outputSet)
    {
        this.outputSet = outputSet;
    } // end method setOutputSet(Set)

    /**
     * Gets whether or not the source sets should be maintained by records processed
     * by the service from the processing directive.
     *
     * @return True if the source sets should be maintained by records processed by
     *         the service from the processing directive, false otherwise
     */
    public boolean getMaintainSourceSets()
    {
        return maintainSourceSets;
    } // end method getMaintainSourceSets()

    /**
     * Sets whether or not the source sets should be maintained by records processed
     * by the service from the processing directive.
     *
     * @param maintainSourceSets True if the source sets should be maintained by records processed
     *                           by the service from the processing directive, false otherwise
     */
    public void setMaintainSourceSets(boolean maintainSourceSets)
    {
        this.maintainSourceSets = maintainSourceSets;
    } // end method setMaintainSourceSets(boolean)

    /**
     * Gets the formats that trigger the processing directive
     *
     * @return The formats that trigger the processing directive
     */
    public List<Format> getTriggeringFormats()
    {
        return triggeringFormats;
    } // end method getTriggeringFormats()

    /**
     * Sets the formats that trigger the processing directive
     *
     * @param triggeringFormats A list formats that trigger the processing directive
     */
    public void setTriggeringFormats(List<Format> triggeringFormats)
    {
        this.triggeringFormats = triggeringFormats;
    } // end method setTriggeringFormats(List<Format>)

    /**
     * Adds a format to the list of formats that trigger the processing directive
     *
     * @param triggeringFormat The format to add to the list of formats that trigger the processing directive
     */
    public void addTriggeringFormat(Format triggeringFormat)
    {
        if(triggeringFormat != null && !triggeringFormats.contains(triggeringFormat))
            triggeringFormats.add(triggeringFormat);
    } // end method addTriggeringFormat(Format)

    /**
     * Removes a format from the list of formats that trigger the processing directive
     *
     * @param triggeringFormat The format to remove from the list of formats that trigger the processing directive
     */
    public void removeTriggeringFormat(Format triggeringFormat)
    {
        if(triggeringFormat != null && triggeringFormats.contains(triggeringFormat))
            triggeringFormats.remove(triggeringFormat);
    } // end method removeTriggeringFormat(Format)

    /**
     * Gets the sets that trigger the processing directive
     *
     * @return The sets that trigger the processing directive
     */
    public List<Set> getTriggeringSets()
    {
        return triggeringSets;
    } // end method getTriggeringSets()

    /**
     * Sets the sets that trigger the processing directive
     *
     * @param triggeringSets A list sets that trigger the processing directive
     */
    public void setTriggeringSets(List<Set> triggeringSets)
    {
        this.triggeringSets = triggeringSets;
    } // end method setTriggeringSets(List<Set>)

    /**
     * Adds a set to the list of sets that trigger the processing directive
     *
     * @param triggeringSet The set to add to the list of sets that trigger the processing directive
     */
    public void addTriggeringSet(Set triggeringSet)
    {
        if(triggeringSet != null && !triggeringSets.contains(triggeringSet))
            triggeringSets.add(triggeringSet);
    } // end method addTriggeringSet(Set)

    /**
     * Removes a set from the list of sets that trigger the processing directive
     *
     * @param triggeringSet The set to remove from the list of sets that trigger the processing directive
     */
    public void removeTriggeringSet(Set triggeringSet)
    {
        if(triggeringSet != null && triggeringSets.contains(triggeringSet))
            triggeringSets.remove(triggeringSet);
    } // end method removeTriggeringSet(Set)

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[Id="+id);
        sb.append(" source service= "+ sourceService);
        sb.append(" source provider= "+ sourceProvider);
        sb.append(" service= "+ service+"]");

        return sb.toString();
    }

} // end class ProcessingDirective
