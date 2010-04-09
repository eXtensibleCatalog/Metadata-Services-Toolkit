package xc.mst.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xc.mst.utils.MSTConfiguration;

public class DevAdminServlet extends HttpServlet {
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter pw = resp.getWriter();
		
		String op = req.getParameter("op");
		if ("props".equals(op)) {
			Properties props = MSTConfiguration.getInstance().getProperties();
			pw.println("<table>");
			for (Map.Entry me: props.entrySet()) {
				pw.println("<tr>");
				pw.println("<td>"+me.getKey()+"</td>");
				pw.println("<td>"+me.getValue()+"</td>");
				pw.println("</tr>");
			}
			pw.println("</table>");
		}
	}

}
