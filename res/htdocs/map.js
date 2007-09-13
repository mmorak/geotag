/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
function round(number) {
  return Math.round(number*10000000)/10000000;
}

function getLatitude(marker) {
  return round(marker.getPoint().y);
}

function getLongitude(marker) {
  return round(marker.getPoint().x);
}

// change the document title, reflecting the current marker position
function setTitle(marker) {
  document.title = "Geotag "+getLatitude(marker)+" "+getLongitude(marker);
}

// check for compatibility
if (GBrowserIsCompatible()) {
  // parse the arguments
  var URL = String(window.location.href + "&x=x?x=x");
  var args = String(URL.split("?")[1]); /* all of the arguments */
  var pairs = args.split("&");  /* the name=value pairs */
  var i;
  var pair;
  var name;
  var value;
  var latitude=51.5;
  var longitude=0;
  var direction = -1.0;
  var showDirection = false;
  var image=0;
  var thumbnail = false;
  var imageWidth = 300;
  var imageHeight = 300;
  var zoom=6;
  var userMapType = "Hybrid"; // default Hybrid map
  var language="en";
  for (i = 0; i < pairs.length; i++) {
    pair = pairs[i].split("=");
    name = pair[0];
    value = pair[1];
    if (name == "latitude") {
      latitude = parseFloat(value);
    }
    if (name == "longitude") {
      longitude = parseFloat(value);
    }
    if (name == "image") {
      image = parseInt(value);
    }
    if (name == "thumbnail") {
      thumbnail = (value == "true");
    }
    if (name == "width") {
      imageWidth = parseInt(value);
    }
    if (name == "height") {
      imageHeight = parseInt(value);
    }
    if (name == "zoom") {
      zoom = Math.abs(parseInt(value));
    }
    if (name == "maptype") {
      userMapType = value;
    }
    if (name == "direction") {
    	// direction can have three different values:
    	if (value == "true") {
    		// show direction, but we don't have an initial value
    		showDirection = true;
    	} else if (value == "false") {
    		// don't show direction (is already default)
    	} else {
    		// showDirection with an initial value
    		showDirection = true;
        direction = parseFloat(value);
    	}
    }
    if (name == "language") {
      language = value;
    }
  }
  // now the language dependent bits
  var title = "Geotag"; // not translatable, but might be later
  var instructions;
  if (showDirection) {
  	instructions = "Move camera marker to<br>select a different location.<br>Move other marker to<br>change image direction.";
  } else {
  	instructions = "Move the marker to<br>select a different location";
  }
  // Auf Deutsch bitte
  if (language == "de") {
    title = "Geotag";
    if (showDirection) {
    	instructions = "Verschieben Sie die Kamera um<br>den Ort zu &auml;ndern.<br>Verschieben Sie die Markierung<br>um die Richtung zu &auml;ndern";
    } else {
      instructions = "Verschieben Sie die Markierung<br>um den Ort zu &auml;ndern";
    }
  }
  document.title=title;
  
  // update the instructions div to show the instructions
  // Shouldn't use innerHTML, but its a bit much to do all the DOM stuff just for this one update
  document.getElementById("instructions").innerHTML = "<center>" + instructions +"</center>";

  // create the map
  var map = new GMap2(document.getElementById("map"));
  
  // move map to desired location and zoom level
  // determine which map type to use
  var mapType = G_HYBRID_MAP;
  if (userMapType == "Satellite") {
    mapType = G_SATELLITE_MAP;
  }
  if (userMapType == "Map") {
    mapType = G_NORMAL_MAP;
  }
  
  map.setCenter(new GLatLng(latitude, longitude), zoom, mapType);
  // add a bunch of controls to the map
  map.addControl(new GLargeMapControl());
  map.addControl(new GMapTypeControl());
  map.addControl(new GScaleControl());

  // make sure the map still shows the marker when resized
  window.onresize = function() {this.map.panTo(this.cameraMarker.getPoint())};
  
  var centre = map.getCenter();
  
  var lineFromCamera = new GPolyline([centre, centre], '#ff0000');
  map.addOverlay(lineFromCamera);
  
  function newLineFromCamera(fromMarker, toMarker) {
    if (showDirection) {
      lineFromCamera = new GPolyline([fromMarker.getPoint(), toMarker.getPoint()], '#ff0000');
      map.addOverlay(lineFromCamera);
    }
  }
  
  
  var directionMarker;
  // the direction marker needs to know where to place itself
  //  = new GMarker(centre, {draggable: true});
  if (showDirection == false || direction < 0) {
  	directionMarker = new GMarker(centre, {draggable: true});
  } else {
  	var centreCoordinates = map.fromLatLngToDivPixel(centre);
  	// as the radius we use half the map height - minus icon height
  	var radius = map.getSize().height / 2 - G_DEFAULT_ICON.iconSize.height;
  	var theta = (direction - 90.0) / 180 * Math.PI;
  	var x = radius * Math.cos(theta);
  	var y = radius * Math.sin(theta);
  	var point = new GPoint(centreCoordinates.x + x, centreCoordinates.y +y);
  	var coordinates = map.fromDivPixelToLatLng(point);
  	directionMarker = new GMarker(coordinates, {draggable: true});
  }
  
  var cameraMarkerIcon = G_DEFAULT_ICON;
  if (showDirection) {
    // show a little camera picture if we use camera position and direction to object
    cameraMarkerIcon = new GIcon(G_DEFAULT_ICON, "http://maps.google.com/mapfiles/kml/pal4/icon46.png",
    null, "http://maps.google.com/mapfiles/kml/pal4/icon46s.png");
    cameraMarkerIcon.iconSize = new GSize(32,32);
    cameraMarkerIcon.showSize = new GSize(56,32);
    cameraMarkerIcon.iconAnchor = new GPoint(16,32);
    cameraMarkerIcon.infoWindowAnchor = new GPoint(16,0);
  }
  
  // create the location marker
  
  var cameraMarker = new GMarker(centre, {icon: cameraMarkerIcon, draggable: true});
  // the HTML displayed in the info window is:
  var infoWindowHtml = '<center>';
  if (thumbnail == true) {
    infoWindowHtml += '<img src='
     + '"images/'
     + image
     + '.jpg" width="'
     + imageWidth
     + '" height="'
     + imageHeight
     + '"><br>'
  }
  infoWindowHtml += instructions + '</center>' 
  cameraMarker.bindInfoWindowHtml(infoWindowHtml, {});
  var infoText = "<b>"+instructions+"</b>";
  
  function dragStartListener() {
  	// close info window if open
    map.closeInfoWindow();
    // remove line from camera to subject
    if (showDirection) {
      map.removeOverlay(lineFromCamera);
    }
  }
  
  // make sure the info window is closed if drag the marker
  GEvent.addListener(cameraMarker, "dragstart", dragStartListener);
  GEvent.addListener(directionMarker ,"dragstart", dragStartListener);

  // here is what we do if the dragging of the marker has ended
  
  function dragendListener() {
  	 // need to calculate a new direction
    var cameraCoordinates = map.fromLatLngToDivPixel(cameraMarker.getPoint());
    var directionCoordinates = map.fromLatLngToDivPixel(directionMarker.getPoint());
    var x = directionCoordinates.x - cameraCoordinates.x;
    var y = directionCoordinates.y - cameraCoordinates.y;
    var theta = (Math.atan2(y, x) + Math.PI * 5.0 / 2.0) % (Math.PI * 2.0);
    direction = theta / Math.PI * 180;
    // make a string out of the new marker location and send it to the web server
    var url = "update/new.html?image="
       + image
       + "&latitude="
       + getLatitude(cameraMarker)
       + "&longitude="
       + getLongitude(cameraMarker);
    if (showDirection) {
      url += "&direction=" + direction;
    }
    var request = GXmlHttp.create();
    request.open("GET", url, true);
    request.send(null);
    // adjust the line from camera to marker
    newLineFromCamera(cameraMarker, directionMarker);
    // then centre the map on the new (or old) camera marker location
    map.panTo(cameraMarker.getPoint());
    setTitle(cameraMarker);
  };
  
  GEvent.addListener(cameraMarker, "dragend", dragendListener);
  GEvent.addListener(directionMarker, "dragend", dragendListener);

  // add the camera marker to the map
  map.addOverlay(cameraMarker);
  // and the subject marker if requested
  if (showDirection) {
    map.addOverlay(directionMarker);
  }
  // draw the line for the first time
  newLineFromCamera(cameraMarker, directionMarker);
  
  // we can display GPS tracks as well
  var tracksDisplayed = [];
  
  // a function to be called every time the map changes
  mapChanged = function () {
    // collect information about the map
    var bounds = map.getBounds();
    var south = bounds.getSouthWest().lat();
    var west = bounds.getSouthWest().lng();
    var north = bounds.getNorthEast().lat();
    var east = bounds.getNorthEast().lng();
    var size = map.getSize();
    // tell Geotag about it
    var tracksURL = "http://localhost:4321/tracks/tracks.kml?south="
      + south + "&west=" + west + "&north=" + north + "&east=" + east
      + "&width=" +size.width + "&height=" + size.height + "&image=" + image;
    var request = GXmlHttp.create();
    request.open("GET", tracksURL, true);
    // Geotag will send tracks for this map
    request.onreadystatechange = function() {
      // only interested if the request has completed
      if (request.readyState == 4) {
        // first we remove all tracks currently displayed
        for (var trackIndex = 0; trackIndex < tracksDisplayed.length; trackIndex++) {
          map.removeOverlay(tracksDisplayed[trackIndex]);
        } 
        // a new empty array
        tracksDisplayed = [];
        var numPoints = 0;
        // parse the document
        var xmlDocument = GXml.parse(request.responseText);
        // get the tracks
        var tracks = xmlDocument.documentElement.getElementsByTagName("track");
        // loop through the tracks
        for (var trackIndex = 0; trackIndex < tracks.length; trackIndex++) {
          var points = tracks[trackIndex].getElementsByTagName("point");
          var linePoints = [];
          for (var pointIndex = 0; pointIndex < points.length; pointIndex++ ) {
            numPoints ++;
            linePoints[pointIndex] = new GLatLng(parseFloat(points[pointIndex].getAttribute("latitude")),
                             parseFloat(points[pointIndex].getAttribute("longitude")))
          }
          tracksDisplayed[trackIndex] = new GPolyline(linePoints,"#0000FF",5,0.5);
          map.addOverlay(tracksDisplayed[trackIndex]);
        }
        //GLog.write( "Tracks: "+tracks.length+" Points: "+numPoints);
      }
    }
    request.send(null);
  }
  
  GEvent.addListener(map, "moveend", mapChanged);
  GEvent.addListener(map, "load", mapChanged);
  mapChanged();
  
  GEvent.addListener(map, "zoomend", function(oldLevel, newLevel) {
    var request = GXmlHttp.create();
    request.open("GET", "http://localhost:4321/zoom/zoom.html?zoom="+newLevel, true);
    request.send(null);
    //GLog.write("New zoom level: "+newLevel);
  });
  
  GEvent.addListener(map, "maptypechanged", function() {
    var request = GXmlHttp.create();
    request.open("GET", "http://localhost:4321/maptype/maptype.html?maptype=" 
       +map.getCurrentMapType().getName());
    request.send(null);
  });
  
  setTitle(cameraMarker);
              
} else {
  // the browser is not compatible with Google Maps
}
