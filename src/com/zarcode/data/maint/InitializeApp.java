package com.zarcode.data.maint;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zarcode.app.Version;
import com.zarcode.platform.dao.AppPropDao;

public class InitializeApp extends HttpServlet {

	private Logger logger = Logger.getLogger(InitializeApp.class.getName());
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	AppPropDao dao = new AppPropDao();
    	dao.addAppVersion(Version.SERVICE_VERSION);
 		resp.setContentType("text/html");
 		resp.getWriter().println("<b>Successfully initialized the app.</b><br><br><a href=\"/_admin\">Admin Console</a>");
    }
	
}