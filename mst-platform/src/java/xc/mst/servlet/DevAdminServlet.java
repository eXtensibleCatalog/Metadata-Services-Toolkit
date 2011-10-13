/**
 * Copyright (c) 2009 eXtensible Catalog Organization
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the MIT/X11 license. The text of the
 * license can be found at http://www.opensource.org/licenses/mit-license.php and copy of the license can be found on the project
 * website http://www.extensiblecatalog.org/.
 *
 */

package xc.mst.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import xc.mst.manager.record.MSTSolrService;
import xc.mst.scheduling.Scheduler;
import xc.mst.utils.MSTConfiguration;

public class DevAdminServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(DevAdminServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();

        String op = req.getParameter("op");
        if ("props".equals(op)) {
            Properties props = MSTConfiguration.getInstance().getProperties();
            pw.println("<table>");
            for (Map.Entry me : props.entrySet()) {
                pw.println("<tr>");
                pw.println("<td>" + me.getKey() + "</td>");
                pw.println("<td>" + me.getValue() + "</td>");
                pw.println("</tr>");
            }
            pw.println("</table>");
        } else if ("refreshSolr".equals(op)) {
            ((MSTSolrService) MSTConfiguration.getInstance().getBean("MSTSolrService")).refreshServer();
        } else if ("setProperty".equals(op)) {
            MSTConfiguration.getInstance().setProperty(req.getParameter("key"), req.getParameter("value"));
        } else {
            pw.println("add ?op=[props|refreshSolr|setProperty] to the url");
        }
    }

    @Override
    public void destroy() {
        LOG.debug("destroy()");
        Scheduler scheduler = (Scheduler) MSTConfiguration.getInstance().getBean("Scheduler");
        scheduler.kill();
        LOG.debug("scheduler.killed");
    }

}
