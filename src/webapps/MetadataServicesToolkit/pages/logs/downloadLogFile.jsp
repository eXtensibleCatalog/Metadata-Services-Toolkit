<!--
  * Copyright (c) 2009 University of Rochester
  *
  * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the  
  * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
  * website http://www.extensiblecatalog.org/. 
  *
  -->

<%@page import="java.io.*" %>
<%@page import="xc.mst.manager.processingDirective.*" %>
<%@page import="xc.mst.manager.repository.*" %>
<%@page import="xc.mst.manager.logs.*" %>
<%@page import="xc.mst.bo.log.Log" %>
<%@page import="xc.mst.bo.service.*" %>
<%@page import="xc.mst.bo.provider.*" %>
<%
        String filename = "";
        String fullpath = "";
        String id = request.getParameter("id");
        String logType = request.getParameter("logType");
        if(logType.equalsIgnoreCase("Service"))
        {
            ServicesService servicesService = new DefaultServicesService();
            Service service = servicesService.getServiceById(Integer.parseInt(id));
            fullpath = service.getServicesLogFileName();
            filename = service.getName()+"ServiceLog";
        }
        else if(logType.equalsIgnoreCase("HarvestOut"))
        {
            ServicesService servicesService = new DefaultServicesService();
            Service service = servicesService.getServiceById(Integer.parseInt(id));
            fullpath = service.getHarvestOutLogFileName();
            filename = service.getName()+"HarvestOutLog";
        }
        else if(logType.equalsIgnoreCase("HarvestIn"))
        {
            ProviderService providerService = new DefaultProviderService();
            Provider provider = providerService.getProviderById(Integer.parseInt(id));
            fullpath = provider.getLogFileName();
            filename = provider.getName();

        }
        else
        {
            LogService logService = new DefaultLogService();
            Log log = logService.getById(Integer.parseInt(id));
            fullpath = log.getLogFileLocation();
            filename = log.getLogFileName();
        }

        response.setContentType ("application/txt");
        //set the header and also the Name by which user will be prompted to save
        response.setHeader("Content-Disposition","attachment;filename="+filename);
        FileInputStream FIS = new FileInputStream(fullpath);
        ServletOutputStream SOS = response.getOutputStream();
        byte[] b = new byte[1];
        while(FIS.read(b)!=-1)
            {
                SOS.write(b);
            }
        SOS.flush();
        SOS.close();
        FIS.close();
%>