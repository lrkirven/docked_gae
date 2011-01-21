package com.zarcode.data.email;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Session; 
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart;

import javax.servlet.http.*;

import org.w3c.dom.Document;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import com.zarcode.platform.loader.JDOLoaderServlet;

public class MailHandlerServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(MailHandlerServlet.class.getName());
	
	 public void doPost(HttpServletRequest req,  HttpServletResponse resp)  
	 	throws IOException {  
		 
		 	logger.info("doPost(): Got email!");
		 	
		 	Properties props = new Properties();  
		 	Session session = Session.getDefaultInstance(props, null); 
		 	try {
		 		MimeMessage message = new MimeMessage(session, req.getInputStream());
		 		
		 		Address from = null;
		 		Address[] fromList = message.getFrom();
		 		if (fromList != null && fromList.length > 0) {
		 			from = fromList[0];
		 			logger.info("doPost(): Email is from: " + from);
		 		}
		 		String type = message.getContentType();
		 		
		 		Pattern typePat = Pattern.compile("multipart.*");
		 	    Matcher contentMatcher = typePat.matcher(type);

		 	    if (contentMatcher.matches()) {
		 	    	logger.info("doPost(): Content Type: " + type);
		 	    	MimeMultipart part = (MimeMultipart)message.getContent();
		 	    	logger.info("doPart(): # of part(s): " + part.getCount());
		 	    	MimeBodyPart body = (MimeBodyPart)part.getBodyPart(1);
		 	    	
		 	    	BufferedReader reader = new BufferedReader(new InputStreamReader(body.getInputStream()));
		 	    	StringBuilder sb = new StringBuilder();
		 	    	String line = null;
		 	    	while ((line = reader.readLine()) != null) {
		 	    		sb.append(line + "\n");
		 	    	}
		 	    	logger.info("doPost(): Part 0 Content: " + sb.toString());
		 	       
		 			// logger.info("doPost(): Content: " + str);
		 			Tidy tidy = new Tidy();
		 			tidy.setMakeClean(true);
		 			tidy.setXmlOut(true);
		 			tidy.setDocType("strict");
		 			tidy.setDropFontTags(true);
		 			tidy.setXHTML(true);
		 	 	   	tidy.setRawOut(true);
		 	 	   	tidy.setSmartIndent(true);
		 	 	   	tidy.setWord2000(true);
		 	 	   	tidy.setDropEmptyParas(true);
		 	 	   	tidy.setShowWarnings(false);
		 	 	   	tidy.setFixComments(true);
		 			// InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
		 			OutputStream os = null;
		 			Document doc = tidy.parseDOM(body.getInputStream(), os);
		 			// log message
		 			logger.info("doPost(): Result XML DOM: " + doc.toString());
		 	    }
		 	}
		 	catch (Exception e) {
		 		logger.severe("doPost(): [EXCEPTION]\n" + getStackTrace(e));
		 	}
	 }
	 
	    private String getStackTrace(Exception e) {
			StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw);
	        e.printStackTrace(pw);
	        String str = "\n" + sw.toString();
	        return str;
		}
}
