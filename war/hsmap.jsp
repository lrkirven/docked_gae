<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false" language="java" %>
<%@ page import="java.net.URLDecoder" %>
<%
	String redirectURL = "http://maps.google.com/";
	String hotSpotId = request.getParameter("id");
	pageContext.setAttribute("hotSpotId", hotSpotId);
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
	

	$(document).ready(function() {
		loadMap();
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
	function initializeMap(latlng) {
		var minZoomLevel = 15;
 		var myOptions = {
   	  		zoom: minZoomLevel,
   	  		navigationControl: true,
     		center: latlng,
     		draggable: true,
     		mapTypeId: google.maps.MapTypeId.ROADMAP
    	};
    	var m = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
    	
    	/*
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
    			loadMap();
    		}
		});
		*/
		 
		return m;
	};   

	function createMarker(text, latlng) {
 		var image = 'images/Marker.png';
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
      		// var panoramaOptions = {position: marker.position};
    	});
    	return marker;
    };

	/**
	 * Load the details from service then initialize and display map.
	 * @return 
	 * @type 
	 */
	function loadMap() {
 		if (mc != null) {
 			mc.clearMarkers();
 		}
 		
    	console.log("CLIENT: Trying to retrieve hotSpots ...");
    	
		var foundUser = false;
 		var markers = [];
 		var bounds = new google.maps.LatLngBounds();
 		var hotSpotId = '<%= pageContext.getAttribute("hotSpotId") %>';
 			
 		$.ajax({
 			type: "GET",
  			url: "resources/hotspots/" + hotSpotId,
  			dataType: "json",
  			success: function(json) {
    			console.log("Got response from service: " + json);
    			var hotSpot = json;
    			var lat = parseFloat(hotSpot.lat);
    			var lng = parseFloat(hotSpot.lng);
 				var latLng = new google.maps.LatLng(lat, lng);
 				var msg = "<table cellpadding=\"5\" bgcolor=\"#CCCCCC\" border=\"0\"><tr>";
 					
 				msg += "<td width=\"10\" />";
 				// message
 				msg += "<td align=\"left\">";
 				msg += "<b>" + hotSpot.location + "</b><br><font color=\"#000000\">" + hotSpot.desc + "</font>";
 				msg += "</td>";
 					
 				msg += "</tr></table>";
 				console.log("CLIENT: Got hotSpot response --> " + hotSpot.desc);
 				var marker = createMarker(msg, latLng);
 				markers.push(marker);
 				if (map == null) {
 					var mcOptions = { maxZoom:20 };
 					map = initializeMap(latLng);
 				}
 				mc = new MarkerClusterer(map, markers, mcOptions);
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
