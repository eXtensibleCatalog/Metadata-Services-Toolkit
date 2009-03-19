
/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.action.user;

import com.opensymphony.xwork2.ActionSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import xc.mst.bo.user.Group;
import xc.mst.constants.Constants;
import xc.mst.manager.user.DefaultGroupService;
import xc.mst.manager.user.DefaultUserGroupUtilService;
import xc.mst.manager.user.GroupService;
import xc.mst.manager.user.UserGroupUtilService;

/**
 * This action method diplays all groups
 *
 * @author Tejaswi Haramurali
 */
public class AllGroups extends ActionSupport
{
    /** A reference to the logger for this class */
    static Logger log = Logger.getLogger(Constants.LOGGER_GENERAL);

    /** The list of groups that a user can belong to */
    private List<Group> groupList;

    /**
     * assigns the list of groups that a user can belong to
     * @param groupList list of groups
     */
    public void setGroupList(List<Group> groupList)
    {
        this.groupList = groupList;
    }

    /**
     * returns a list of groups that a user can belong to
     * @return list of groups
     */
    public List<Group> getGroupList()
    {
        return groupList;
    }

     /**
     * Overrides default implementation to view the all groups page.
     * @return {@link #SUCCESS}
     */
    @Override
    public String execute()
    {
        try
        {
            GroupService groupService = new DefaultGroupService();
            UserGroupUtilService UGUtilService = new DefaultUserGroupUtilService();
            List<Group> tempList = groupService.getAllGroups();
            List<Group> finalList = new ArrayList();

            Iterator iter = tempList.iterator();
            while(iter.hasNext())
            {
                Group group = (Group)iter.next();
                group.setMemberCount(UGUtilService.getUsersForGroupId(group.getId()).size());
                finalList.add(group);
            }
            setGroupList(finalList);
            return SUCCESS;
        }
        catch(Exception e)
        {
            log.debug(e);
            e.printStackTrace();
            this.addFieldError("allGroupsError", "Error : Groups not displayed correctly");
            return SUCCESS;
        }

    }
}
