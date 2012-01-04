/**
  * Copyright (c) 2011 eXtensible Catalog Organization
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */
package xc.mst.services.marcaggregation.matcher;

import java.util.List;

import xc.mst.bo.record.SaxMarcXmlRecord;
import xc.mst.manager.BaseService;

/**
 * This abstract class ties the interface into the MST by making use of the platform
 * infrastucture.  (by subclassing BaseService)
 *
 * @author Benjamin D. Anderson
 *
 */
public abstract class FieldMatcherService extends BaseService implements FieldMatcher {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMatchingOutputIds(SaxMarcXmlRecord ir, List<Long> filterBy) {
        return null;
    }

}
