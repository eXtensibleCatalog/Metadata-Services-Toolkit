/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.manager.harvest;

import java.util.List;
import xc.mst.bo.harvest.Harvest;
import xc.mst.dao.harvest.HarvestDAO;

/**
 * Provides the implementation for the
 *
 * @author Tejaswi Haramurali
 */
public class DefaultHarvestService implements HarvestService
{
    private HarvestDAO harvestDao;
    /**
	 * Gets all harvests in the database
	 *
	 * @return A list containing all harvests in the database
	 */
	public List<Harvest> getAllHarvests()
    {
        return harvestDao.getAll();
    }

	/**
	 * Gets a harvest by it's ID
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest with the passed ID, or null if there was no harvest with that ID.
	 */
	public Harvest getHarvestById(int harvestId)
    {
        return harvestDao.getById(harvestId);
    }

	/**
	 * Gets a harvest by it's ID without getting extra information
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest  with the passed ID, or null if there was no harvest with that ID.
	 */
	public Harvest loadBasicHarvest(int harvestId)
    {
        return harvestDao.loadBasicHarvest(harvestId);
    }

	/**
	 * Gets all harvests which were run by a harvest schedule
	 *
	 * @param harvestScheduleId The ID of the harvest schedule whose harvests we should get
	 * @return A list all harvests which were run by the harvest schedule with the passed ID
	 */
	public List<Harvest> getHarvestsForSchedule(int harvestScheduleId)
    {
        return harvestDao.getHarvestsForSchedule(harvestScheduleId);
    }
}
