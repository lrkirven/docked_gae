<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.zarcode.data.model.UserDO" %>
<%@ page import="com.zarcode.data.dao.UserDao" %>
<%
	String redirectURL = "http://maps.google.com/";
	String lat = request.getParameter("lat");
	String lng = request.getParameter("lng");
	pageContext.setAttribute("lat", lat);
	pageContext.setAttribute("lng", lng);
	pageContext.setAttribute("draggable", "true");	
%>

<html>
<head>
<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true"></script> 
<script src="mapiconmaker.js" type="text/javascript"></script> 
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script> 
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script> 
<script type="text/javascript" src="jquery-1.4.2.min.js"></script>
<script type="text/javascript"> 
      var script = '<script type="text/javascript" src="markerclusterer';
      if (document.location.search.indexOf('packed') !== -1) {
        script += '_packed';
      }
      if (document.location.search.indexOf('compiled') !== -1) {
        script += '_compiled';
      }
      script += '.js"><' + '/script>';
      document.write(script);
</script> 
<script type="text/javascript">
	// map
	var map;
	
	// info win
	var infowindow;
	
	// cluster
	var mc;
	
	// weather overlay
	var weatherOverlay;
	
	var boundsChangedFlag;
	
	// var iconOptions = {};
	// iconOptions.width = 32;
	// iconOptions.height = 32;
	// iconOptions.primaryColor = "#99FF99";
	// iconOptions.label = "23";
	// iconOptions.labelSize = 15;
	// iconOptions.labelColor = "#000000";
	// iconOptions.shape = "circle";
	// var icon = MapIconMaker.createFlatIcon(iconOptions);

	

	// initial entry
	$(document).ready(function() {
		setTimeout("location.reload(true);", 180000);
		initializeMap();
		refreshMap();
		console.log("Contacting Ti that the map is loaded");
		// weatherOverlay = new WeatherLoader(map);	
 	});

 	function detectBrowser() {
 		var useragent = navigator.userAgent;
 		var mapdiv = document.getElementById("map_canvas");

 		if (useragent.indexOf('iPhone') != -1 || useragent.indexOf('Android') != -1 ) {
 			mapdiv.style.width = '100%';
 			mapdiv.style.height = '100%';
 		}
 		else {
 			mapdiv.style.width = '600px';
 			mapdiv.style.height = '800px';
 		}
 	}; 
 	
 	/**
 	 * Initializes my map
 	 */ 	
	function initializeMap() {
		var latlng = new google.maps.LatLng(<%= pageContext.getAttribute("lat") %>, <%= pageContext.getAttribute("lng") %> );
		var minZoomLevel = 12;
 		var myOptions = {
   	  		zoom: minZoomLevel,
   	  		navigationControl: true,
     		center: latlng,
     		draggable: <%= pageContext.getAttribute("draggable") %>,
     		mapTypeId: google.maps.MapTypeId.ROADMAP
    	};
    	map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
    	mc = new MarkerClusterer(map);
    	
    	google.maps.event.addListener(map, 'bounds_changed', function() {
    		boundsChangedFlag = true;
		});
		google.maps.event.addListener(map, 'zoom_changed', function() {
     		if (map.getZoom() < minZoomLevel) { 
     			map.setZoom(minZoomLevel);
     		}
   		});
		google.maps.event.addListener(map, 'idle', function() {
			if (boundsChangedFlag) {
				boundsChangedFlag = false;
    			refreshMap();
    		}
		});
    	
    	/*	
  		var weatherCtrlDiv = document.createElement('DIV');
  		var weatherControl = new WeatherControl(weatherCtrlDiv, map);
  		homeControlDiv.index = 1;
  		map.controls[google.maps.ControlPosition.TOP_RIGHT].push(homeControlDiv);
  		*/
	};   

	function createMarker(text, latlng) {
 		var image = 'images/ChatBubble2.png';
    	var marker = new google.maps.Marker({
      		position: latlng, 
      		map: map,
      		icon:image
      	});                     
 
 
    	google.maps.event.addListener(marker, "click", function() {
    
    	  	if (infowindow) {
     	 		infowindow.close();
      		}
      
      		infowindow = new google.maps.InfoWindow({content: text});
      		infowindow.open(map, marker);
      		var panoramaOptions = {position: marker.position};
      	
 			/*      
      		google.maps.event.addListener(infowindow, 'domready', function() {       
        	  	$(".tabs").tabs();
         	 	$('#SV').click(function() {
          	  		var panorama = new google.maps.StreetViewPanorama(document.getElementById("pano"),panoramaOptions);  
            		map.setStreetView(panorama);
        		});
      		}); 
      		*/
      
    	});
    	
    	return marker;
    };

	function refreshMap() {
 		if (mc != null) {
 			mc.clearMarkers();
 		}
 		
    	console.log("CLIENT: Trying to retrieve buzz msgs ...");
    	
		var foundUser = false;
 		var markers = [];
 		var bounds = new google.maps.LatLngBounds();
 		var center = map.getCenter();
 			
 		$.ajax({
 			type: "GET",
  			url: "resources/buzz/bylatlng?lat=" + center.lat() + "&lng=" + center.lng(),
  			dataType: "json",
  			success: function(json) {
    			console.log("Got response from service: " + json);
    			var list = json;
    			console.log("# of messages: " + list.length);
    			var i = 0;
    			var m = null;
    			
    			for (i=0; i<list.length; i++) {
    				m = list[i];	
    				var lat = parseFloat(m.lat);
    				var lng = parseFloat(m.lng);
 					var latLng = new google.maps.LatLng(lat, lng);
 					console.log("Adding message=" + m.messageData);
 					var msg = "<table cellpadding=\"5\" bgcolor=\"#CCCCCC\" border=\"0\"><tr>";
 					
 					if (m.photoUrl != null) {
 						msg += "<td><img src=\"" + m.photoUrl + "\" width=\"50\" height=\"50\" /></td>";
 					}
 					else if (m.profileUrl != null) {
 						msg += "<td><img src=\"" + m.profileUrl + "\" width=\"50\" height=\"50\" /></td>";
 					}
 					
 					msg += "<td width=\"10\" />";
 					// message
 					msg += "<td align=\"left\">";
 					msg += "<b>" + m.username + "</b><br><font color=\"#000000\">" + m.messageData + "</font>";
 					msg += "</td>";
 						
 					msg += "</tr></table>";
 					var marker = createMarker(msg, latLng);
 					markers.push(marker);
    			}
    		
    			//	
    			// if found some users
    			//
 				if (markers.length > 0) {
					// var mcOptions = {gridSize: 50, maxZoom: 20};
					var mcOptions = { maxZoom:20 };
 					console.log("Adding marker cluster for group of users -- " + markers.length);
 					mc = new MarkerClusterer(map, markers, mcOptions);
 					// setTimeout('refreshMap()', 180000);
 				}	
 				else {
 					console.log("Unable to find any active users or XML response is BAD.");
 				}	
    			
  			},
  			error: function(XMLHttpRequest, textStatus, errorThrown) {
    			console.log("SERVER: Got error: " + textStatus, errorThrown);
  			}
		});
		
		
	}; 
	
</script>
</head>
<body>
	<div id="map_canvas" style="width:100%; height:100%"></div>
</body>
</html>
