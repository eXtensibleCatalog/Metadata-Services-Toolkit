 /*
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/.
  *
  */

 function serviceAd()
            {
                try
                {
                    document.getElementById("selectedLocation").value = document.getElementById("fileLocation").value;
                    document.addService.action = "addService.action";
                    document.addService.submit();
                }
                catch(err)
                {
                    alert(err);
                }
                
            }

 function cancel()
    {
        document.addService.action = "listServices.action";
        document.addService.submit();
    }
