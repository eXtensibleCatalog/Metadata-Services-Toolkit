/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.test;

import java.sql.Date;

import org.testng.annotations.Test;

import xc.mst.bo.harvest.HarvestSchedule;
import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Provider;
import xc.mst.bo.provider.Set;
import xc.mst.bo.user.User;
import xc.mst.dao.DataException;
import xc.mst.manager.harvest.DefaultScheduleService;
import xc.mst.manager.harvest.ScheduleService;
import xc.mst.manager.record.MSTSolrServer;
import xc.mst.manager.repository.DefaultFormatService;
import xc.mst.manager.repository.DefaultProviderService;
import xc.mst.manager.repository.DefaultSetService;
import xc.mst.manager.repository.FormatService;
import xc.mst.manager.repository.ProviderService;
import xc.mst.manager.repository.SetService;
import xc.mst.manager.user.DefaultServerService;
import xc.mst.manager.user.DefaultUserService;
import xc.mst.manager.user.ServerService;
import xc.mst.manager.user.UserService;


/**
 * Test for schedule
 *
 * @author Sharmila Ranganathan
 *
 */
@Test(groups = { "baseTests" }, enabled = true)
public class ScheduleServiceTest {

 ProviderService providerService = new DefaultProviderService();

 ScheduleService scheduleService = new DefaultScheduleService();

 FormatService formatService = new DefaultFormatService();

 SetService setService = new DefaultSetService();

 UserService userService = new DefaultUserService();

 /**
  * Test creating schedule
  *
  * @throws DataException
  */
 public void addScheduleTest() throws DataException
 {

  try
        {
	  	    ServerService serverService = new DefaultServerService();
            User user = new DefaultUserService().getUserByUserName("admin", serverService.getServerByName("Local"));

            Provider provider = new Provider();

            provider.setName("name");
            provider.setDescription("description");
            provider.setOaiProviderUrl("http://oaitoolkit.com");
            provider.setCreatedAt(new Date(new java.util.Date().getTime()));
            provider.setUser(user);
            providerService.insertProvider(provider);

            Format format = new Format();
            format.setName("name");
            format.setNamespace("namespace");
            format.setSchemaLocation("schemaLocation");
            formatService.insertFormat(format);

            Set set = new Set();
            set.setDisplayName("displayName");
            set.setSetSpec("setSpec");
            setService.insertSet(set);


            HarvestSchedule schedule = new HarvestSchedule();
            schedule.setScheduleName("scheduleName");
            schedule.setDayOfWeek(1);
            schedule.setEndDate(new Date(new java.util.Date().getTime()));
            schedule.addFormat(format);
            schedule.addSet(set);
            schedule.setHour(5);
            schedule.setId(111);
            schedule.setMinute(5);
            schedule.setNotifyEmail("email@yahoo.com");
            schedule.setProvider(provider);
            schedule.setRecurrence("Daily");
            schedule.setStartDate(new Date(new java.util.Date().getTime()));

            scheduleService.insertSchedule(schedule);

            HarvestSchedule scheduleCopy = scheduleService.getScheduleById(schedule.getId());
            assert scheduleCopy.getScheduleName().equals(schedule.getScheduleName()) : "Name should be scheduleName";
            assert scheduleCopy.getDayOfWeek() == schedule.getDayOfWeek() : "Day of week should be 1";
            assert scheduleCopy.getEndDate() != null: "End date does not match";
            assert scheduleCopy.getFormats().get(0).equals(format) : "Format does not match";
            assert scheduleCopy.getSets().get(0).equals(set) : "Set does not match";
            assert scheduleCopy.getHour() == schedule.getHour() : "Hour should be 5";
            assert scheduleCopy.getId() == schedule.getId() : "Id should be 111";
            assert scheduleCopy.getMinute() == schedule.getMinute() : "Minute should be 5";
            assert scheduleCopy.getNotifyEmail().equals(schedule.getNotifyEmail()) : "Email should be email@yahoo.com";
            assert scheduleCopy.getProvider().getId()== provider.getId() : "Provider should be same" ;
            assert scheduleCopy.getStartDate() != null : "Start date does not match";
            assert scheduleCopy.getRecurrence().equals(schedule.getRecurrence()) : "Recurrence should be Daily";

            scheduleService.deleteSchedule(schedule);
            providerService.deleteProvider(provider);
            formatService.deleteFormat(format);
            setService.deleteSet(set);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
 }

}