package com.zarcode.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeoRssUtil {
	
	private static Logger logger = Logger.getLogger(GeoRssUtil.class.getName());
	
	public static void findMatchingNodes(Node node, short nodeType, List lostAndFoundList) {
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
	
}
