/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.dao.gui;

import java.util.List;

import xc.mst.bo.gui.Page;
import xc.mst.bo.gui.SubTab;

public class DefaultPageDAO extends PageDAO
{
	@Override
	public boolean delete(Page page)
	{
		// TODO Auto-generated method stub
		return false;
	} // end method delete(Page)

	@Override
	public List<Page> getAll()
	{
		// TODO Auto-generated method stub
		return null;
	} // end method getAll()

	@Override
	public Page getById(int pageId)
	{
		// TODO Auto-generated method stub
		return null;
	} // end method getById(int)

	@Override
	public Page getByPageName(String name)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Page> getByPageNumber(int pageNumber)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Page> getPagesForSubtab(SubTab subtab)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insert(Page page)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Page page)
	{
		// TODO Auto-generated method stub
		return false;
	}
} // end class DefaultPageDAO
