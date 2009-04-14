/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action;

/**
 * A class that implements this interface would have the option to obtain the current process being executed by the MST
 *
 * @author Tejaswi Haramurali
 */
public interface HarvestStatusAware
{
    /**
     * This method sets the name of the current process being executed by the MST
     *
     * @param currentProcess current process being executed
     */
    public void setCurrentProcess(String currentProcess);
}
