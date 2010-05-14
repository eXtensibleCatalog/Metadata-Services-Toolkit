/**
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

package xc.mst.services;

import xc.mst.bo.provider.Format;
import xc.mst.bo.provider.Set;
import xc.mst.repo.Repository;

public interface MetadataService {
	
	public void install();
	
	public void uninstall();
	
	public void update();
	
	public void process();
	
	public void getRepository();
	
	public void process(Repository repo, Format format, Set set);

}
