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

/**
 * The Service class for interacting with the individual harvests
 *
 * @author Tejaswi Haramurali
 */
public interface HarvestService
{

    /**
	 * Gets all harvests in the database
	 *
	 * @return A list containing all harvests in the database
	 */
	public abstract List<Harvest> getAllHarvests();

	/**
	 * Gets a harvest by it's ID
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest with the passed ID, or null if there was no harvest with that ID.
	 */
	public abstract Harvest getHarvestById(int harvestId);

	/**
	 * Gets a harvest by it's ID without getting extra information
	 *
	 * @param harvestId The ID of the harvest to get
	 * @return The harvest  with the passed ID, or null if there was no harvest with that ID.
	 */
	public abstract Harvest loadBasicHarvest(int harvestId);

	/**
	 * Gets all harvests which were run by a harvest schedule
	 *
	 * @param harvestScheduleId The ID of the harvest schedule whose harvests we should get
	 * @return A list all harvests which were run by the harvest schedule with the passed ID
	 */
	public abstract List<Harvest> getHarvestsForSchedule(int harvestScheduleId);
}
