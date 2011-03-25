package com.zarcode.data.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zarcode.common.Util;
import com.zarcode.data.dao.ReportDao;
import com.zarcode.data.exception.WebCrawlException;
import com.zarcode.data.model.ReportDO;

public abstract class WebCrawler  {
	
	private Logger logger = Logger.getLogger(WebCrawler.class.getName());

    public void doCrawl(HttpServletRequest req) throws WebCrawlException {
    }
    
    public boolean readyToCrawl() {
    	logger.severe("* THIS METHOD SHOULD NEVER BE EXECUTED *");
    	return false;
    }
    
    protected void printDocument(Document doc, Logger log) throws IOException {
    	try {
    		TransformerFactory tf = TransformerFactory.newInstance();
        	Transformer transformer = tf.newTransformer();
        	transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        	transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        	transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        	transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        	transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        	StringWriter sw = new StringWriter();
        	transformer.transform(new DOMSource(doc), new StreamResult(sw));
        	log.info("printDocument():\n\n" + sw.getBuffer().toString());
    	}
    	catch (Exception e) {
    		logger.severe("printDocument(): EXCEPTION -- " + Util.getStackTrace(e));
    	}
    }
    
	protected void findMatchingNodes(Node node, short nodeType, List lostAndFoundList) {
		String res = null;
		Node n = null;
		int i = 0;
		// pattern for matching whitespace
		Pattern p = Pattern.compile("[\\s]+");
		
		NodeList children = node.getChildNodes();
		if (children != null && children.getLength() > 0) {
			for (i=0; i<children.getLength(); i++) {
				n = children.item(i);
				if (n.getNodeName().equalsIgnoreCase("#comment")) {
					continue;
				}
				if (n.getNodeType() == nodeType) {
					res = n.getNodeValue();
					Matcher whitespace = p.matcher(res);
					if (res != null && !whitespace.matches()) {
						logger.fine(" --> Adding node value of node=" + n.getNodeName());
						lostAndFoundList.add(res);
					}
				}
				else {
					findMatchingNodes(n, nodeType, lostAndFoundList);
					/*
					if (res != null && !res.equalsIgnoreCase(" ") && !res.equalsIgnoreCase("\n") && !res.equalsIgnoreCase("\r") && !res.equalsIgnoreCase("\t")) {
						logger.fine(" --> Adding node value of node=" + n.getNodeName());
						lostAndFoundList.add(res);
					}
					*/
				}
			}
		}
	}
	
	private String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} 
		catch (TransformerException te) {
		   logger.severe("nodeToString Transformer Exception");
		}
		return sw.toString();
	}
	
	
	protected  String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	    	line = line.replace("<p>", "");
	    	line = line.replace("</p>", "");
	    	sb.append(line + "\n");
	    }
	    is.close();
	    return sb.toString();
	}
	
	
	
	protected String getNodeContents(Node node) {
		String res = null;
		Node n = null;
		int childNodeCount = 0;
		int i = 0;
		
		StringBuilder sb = null;
		NodeList children = node.getChildNodes();
		if (children != null && children.getLength() > 0) {
			childNodeCount = children.getLength();
			logger.info("Node=[" + node.getNodeName() + "] -- # of children: " + childNodeCount);
			for (i=0; i<childNodeCount; i++) {
				n = children.item(i);
				if (n != null) {
					if (n.getNodeName() == null) {
						String str = node.getNodeValue();
						logger.warning("**** Child NODE's Nodename is NULL for index=" + i + " --- Converting whole node to a string: " + str);
						if (sb == null) {
							sb = new StringBuilder();
						}
						sb.append(str);
						break;
					}
					if (n.getNodeName().equalsIgnoreCase("#comment")) {
						continue;
					}
					if (sb == null) {
						sb = new StringBuilder();
					}
					sb.append(nodeToString(n));
				}
				else {
					logger.warning("**** Child NODE is NULL for index=" + i + " --- Converting whole node to a string: " + node);
					String str = node.getTextContent();
					if (sb == null) {
						sb = new StringBuilder();
					}
					sb.append(str);
					break;
				}
			}
		}
		else {
			logger.warning("Unable to find any children in this node=" + node.getNodeName());
		}
		return (sb == null ? null : sb.toString());
	}
    
    protected boolean isFeedUpdated(String state) {
    	final boolean UPDATED = true;
    	final boolean NOT_UPDATED = false;
    	//
		// check to see if feed has been updated lately.  If so, 
		// don't crawl
		//
		ReportDao reportDao = new ReportDao();
		List<ReportDO> reportList = reportDao.getReportsByState(state);
		if (reportList != null && reportList.size() > 0) {
			Date d = new Date();
			ReportDO rep = reportList.get(0);
			Date lastUpdate = rep.getLastUpdated();
			Long diff = d.getTime() - lastUpdate.getTime();
			// in seconds
			diff = (diff/1000);
			// in mins
			diff = (diff/60);
			// in hours
			diff = (diff/60);
			// in days
			diff = (diff/24);
			if (diff < 3) {
				return UPDATED;
			}
		}
		return NOT_UPDATED;
    }
    
}
