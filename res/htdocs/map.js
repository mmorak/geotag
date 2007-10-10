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
 
// check for compatibility
if (GBrowserIsCompatible()) {
	
	var language = "en"; // default language
	var showDirection = false;
	
	// create the map
  var map = new GMap2(document.getElementById("map"));
  // the image the map will be centred on
  var activeImage = null;
  
  // create the instructions (depening on language and showDirection) 
  function getInstructions(showDirection) {
  	var instructions;
    if (showDirection) {
      instructions = "Move camera marker to<br>select a different location.<br>Move other marker to<br>change image direction.";
    } else {
      instructions = "Move the marker to<br>select a different location";
    }
    if (language == "de") {
    	// Auf Deutsch bitte..
    	if (showDirection) {
        instructions = "Verschieben Sie die Kamera um<br>den Ort zu &auml;ndern.<br>Verschieben Sie die Markierung<br>um die Richtung zu &auml;ndern";
      } else {
        instructions = "Verschieben Sie die Markierung<br>um den Ort zu &auml;ndern";
      }
    }
    return instructions;
  }
  
  // a simple ArrayList class
  function ArrayList() {
    count = 0;
    array = new Array();
    this.add = function add(image) {
      array[count]=image;
      count++;
    }
    this.getAll = function getAll() {
      return array;
    }
    this.get = function(index) {
    	return array[index];
    }
    this.size = function() {
    	return count;
    }
    this.indexOf = function(image) {
    	for (var i=0; i < count;i++) {
    		if (array[i]==image) {
    			return i;
    		}
    	}
    	return -1;
    }
  }
  
  // a list of all the images
  var imageInfos = new ArrayList();
  
  // the correct icon to use for the location marker
  function getLocationIcon() {
  	var icon = new GIcon(G_DEFAULT_ICON,
    showDirection?"http://maps.google.com/mapfiles/kml/pal4/icon46.png":null,
    null,
    showDirection?"http://maps.google.com/mapfiles/kml/pal4/icon46s.png":null);
 
    if (showDirection == true) {
      icon.iconSize = new GSize(32,32);
      icon.showSize = new GSize(56,32);
      icon.iconAnchor = new GPoint(16,32);
      icon.infoWindowAnchor = new GPoint(16,0);
    }
    return icon;
  }
  
  // the icon to use for the direction marker
  function getDirectionIcon() {
  	return G_DEFAULT_ICON;
  }
  
  // the line between location and direction marker
  function createDirectionLine(imageInfo) {
    return new GPolyline([imageInfo.locationMarker.getLatLng(), imageInfo.directionMarker.getLatLng()], '#ff0000');
  }
  
  // what to do when a marker starts being dragged
  function dragStart(imageInfo) {
    // close info window if open
    map.closeInfoWindow();
    // remove line from camera to subject
    if (showDirection) {
      map.removeOverlay(imageInfo.directionLine);
    }
  }
  
  // what to to when a marker finished being dragged
  function dragEnd(imageInfo) {
    // need to calculate a new direction
    var cameraCoordinates = map.fromLatLngToDivPixel(imageInfo.locationMarker.getPoint());
    var directionCoordinates = map.fromLatLngToDivPixel(imageInfo.directionMarker.getPoint());
    var x = directionCoordinates.x - cameraCoordinates.x;
    var y = directionCoordinates.y - cameraCoordinates.y;
    var theta = (Math.atan2(y, x) + Math.PI * 5.0 / 2.0) % (Math.PI * 2.0);
    var newDirection = theta / Math.PI * 180;
    // make a string out of the new marker location and send it to the web server
    var url = "/update/new.html?image="
       + imageInfo.id
       + "&latitude="
       + getLatitude(imageInfo.locationMarker)
       + "&longitude="
       + getLongitude(imageInfo.locationMarker);
    if (showDirection) {
      url += "&direction=" + newDirection;
    }
    var request = GXmlHttp.create();
    request.open("GET", url, true);
    request.send(null);
    // adjust the line from camera to marker
    imageInfo.directionLine = createDirectionLine(imageInfo);
    map.addOverlay(imageInfo.directionLine);
    if (showDirection == false) {
      imageInfo.directionLine.hide();
    }
    // then centre the map on the new (or old) camera marker location
    setActiveImage(imageInfo);
  };

  // the HTML displayed in the info window for a marker is:  
  function infoWindowHtml(imageInfo) {
    var html = '<center>';
    if (imageInfo.hasThumbnail == true) {
      html += '<img src='
       + '"/images/'
       + imageInfo.id
       + '.jpg" width="'
       + imageInfo.width
       + '" height="'
       + imageInfo.height
       + '"><br>'
    }
    html += imageInfo.filename + '</center>'
    return html;   	
  }

  // create a location marker
  function createLocationMarker(imageInfo) {
    var icon = getLocationIcon(showDirection);
    var location = new GLatLng(imageInfo.latitude, imageInfo.longitude);  	
    var marker = new GMarker(location, {icon: icon, draggable: true});  	
    marker.bindInfoWindowHtml(infoWindowHtml(imageInfo), {});
    return marker;
  }

  // create a direction marker
  function createDirectionMarker(imageInfo) {
  	var location = new GLatLng(imageInfo.latitude, imageInfo.longitude);
    var directionMarker;
    // the direction marker needs to know where to place itself
    if (imageInfo.direction < 0) {
      directionMarker = new GMarker(location, {draggable: true});
    } else {
      var centreCoordinates = map.fromLatLngToDivPixel(location);
      // as the radius we use half the map height or width - minus icon height
      var radius = Math.min(map.getSize().height, map.getSize().width) / 2 - G_DEFAULT_ICON.iconSize.height;
      var theta = (imageInfo.direction - 90.0) / 180 * Math.PI;
      var x = radius * Math.cos(theta);
      var y = radius * Math.sin(theta);
      var point = new GPoint(centreCoordinates.x + x, centreCoordinates.y +y);
      var coordinates = map.fromDivPixelToLatLng(point);
      directionMarker = new GMarker(coordinates, {draggable: true});
    }
    directionMarker.bindInfoWindowHtml(infoWindowHtml(imageInfo), {});
    return directionMarker;
  }
  
  // ImageInfo class
  function ImageInfo(id) {
  	var instance = this;
    // store the id
    this.id = id;
    this.dragStartListener = function() {
    	dragStart(instance);
    }
    this.dragEndListener = function() {
    	dragEnd(instance);
    }
  }
  
  // change active image, pan to it and change title
  function setActiveImage(image) {
  	activeImage = image;
  	map.panTo(activeImage.locationMarker.getPoint());
    setTitle(activeImage.locationMarker);
  }
  
  // ask Geotag about the images to be displayed
  function requestImageInfos(imageInfoList) {
  	// request information about the image
    var URL = "/imageinfo/imageinfo.xml?ids=";
    for (var index = 0; index < imageInfoList.size(); index++) {
    	URL += index == 0 ? "" : ",";
    	URL += imageInfoList.get(index).id;
    }
    var request = GXmlHttp.create();
    request.open("GET", URL, true);
    request.onreadystatechange = function() {
      // only interested if the request has completed
      if (request.readyState == 4) {
        // parse the information
        xmlDocument = GXml.parse(request.responseText);
        infos = xmlDocument.documentElement.getElementsByTagName("image");
        for (var index = 0; index < infos.length; index++) {
          info = infos[index];
          id = parseFloat(info.getAttribute("id"));
          // find the imageInfo with that id
          imageInfo = null;
          for (var index2 = 0; index2 < imageInfoList.size(); index2++) {
          	if (imageInfoList.get(index2).id == id) {
          		imageInfo = imageInfoList.get(index2);
          		break;
          	}
          }
          // store information with the imageInfo
          if (imageInfo != null) {
            imageInfo.filename = info.getAttribute("name");
            imageInfo.width = parseInt(info.getAttribute("width"));
            imageInfo.height = parseInt(info.getAttribute("height"));
            imageInfo.hasThumbnail = (this.width != 0 && this.height != 0);
            imageInfo.latitude = parseFloat(info.getAttribute("latitude"));
            imageInfo.longitude = parseFloat(info.getAttribute("longitude"));
            imageInfo.direction = parseFloat(info.getAttribute("direction"));
            // create the location marker for this image
            imageInfo.locationMarker = createLocationMarker(imageInfo);
            // and add it to the map
            map.addOverlay(imageInfo.locationMarker);
            // Do the same for the direction marker...
            imageInfo.directionMarker = createDirectionMarker(imageInfo);
            map.addOverlay(imageInfo.directionMarker);
            // ...and the line between them
            imageInfo.directionLine = createDirectionLine(imageInfo);
            map.addOverlay(imageInfo.directionLine);
            // If we don't show directions we hide the marker and the line
            if (showDirection == false) {
              imageInfo.directionMarker.hide();
              imageInfo.directionLine.hide();
            }
            // now we add listeners to the markers
            GEvent.addListener(imageInfo.locationMarker, "dragstart", imageInfo.dragStartListener);
            GEvent.addListener(imageInfo.directionMarker, "dragstart", imageInfo.dragStartListener);
            GEvent.addListener(imageInfo.locationMarker, "dragend", imageInfo.dragEndListener);
            GEvent.addListener(imageInfo.directionMarker, "dragend", imageInfo.dragEndListener);
            //GLog.write(requst.responseText);
            if (activeImage == null) {
              setActiveImage(imageInfo);
            }
          }
        }
      }
    }
    request.send(null);
  }    
  
  // parse the URL arguments
  function Arguments() {
    var URL = String(window.location.href + "&x=x?x=x");
    var argumentArray = String(URL.split("?")[1]); /* all of the arguments */
    var pairs = argumentArray.split("&");  /* the name=value pairs */
    this.latitude=51.5;
    this.longitude=0;
    this.zoom=6;
    this.wheelzoom = false;
    this.menuopen = true;
    for (var i = 0; i < pairs.length; i++) {
      pair = pairs[i].split("=");
      name = pair[0];
      value = pair[1];
      if (name == "latitude") {
        this.latitude = parseFloat(value);
      }
      if (name == "longitude") {
        this.longitude = parseFloat(value);
      }
      if (name == "images") {
      	imageIds = value.split("_");
      	for (var image = 0; image < imageIds.length; image++) {
      		id = parseInt(imageIds[image]);
      		imageInfos.add(new ImageInfo(id));
      	}
      }
      if (name == "zoom") {
        this.zoom = Math.abs(parseInt(value));
      }
      if (name == "maptype") {
        this.mapType = G_HYBRID_MAP;
        if (value == "Satellite") {
          this.mapType = G_SATELLITE_MAP;
        }
        if (value == "Map") {
          this.mapType = G_NORMAL_MAP;
        }
      }
      if (name == "direction") {
        // direction can have three different values:
        if (value == "true") {
          // show direction, but we don't have an initial value
          showDirection = true;
        }
      }
      if (name == "language") {
        language = value;
      }
      if (name == "wheelzoom") {
      	this.wheelzoom = (value == "true");
      }
      if (name == "menuopen") {
      	this.menuopen = (value == "true");
      }
    }
  }
  
  // parse the arguments from the URL  
  var args = new Arguments();

  // a few utility functions

  function round(number) {
    return Math.round(number*10000000)/10000000;
  }

  function getLatitude(marker) {
    return round(marker.getPoint().y);
  }

  function getLongitude(marker) {
    return round(marker.getPoint().x);
  }
  
  function removeElementById(elementID) {
     var element = document.getElementById(elementID);
     if (element) {
     	 element.parentNode.removeChild(element);
     }
   } 
  
  // change the document title, reflecting the current marker position
  function setTitle(marker) {
    document.title = "Geotag "+getLatitude(marker)+" "+getLongitude(marker);
  }
  
  requestImageInfos(imageInfos);
  
  // move map to desired location and zoom level

  map.setCenter(new GLatLng(args.latitude, args.longitude), args.zoom, args.mapType);
  // add a bunch of controls to the map
  map.addControl(new GLargeMapControl());
  map.addControl(new GMapTypeControl());
  map.addControl(new GScaleControl());
  // enabled mouse wheel zooming
  
  // now the language dependent bits
  var title = "Geotag"; // not translated, but might be later
  var showMenuText = 'Show menu';
  var hideMenuText = 'Hide menu';
  var mouseZoomText = 'Enable scroll wheel zoom';
  var currentImageText = "Current image";
  var nextImageText = 'Next image';
  var previousImageText = 'Previous image';
  var showAllText = "Show all images";

  // Auf Deutsch bitte
  if (language == "de") {
    title = "Geotag";
    showMenuText = "Men&uuml; &ouml;ffnen";
    hideMenuText = 'Men&uuml; schliessen';
    mouseZoomText = "Mit Mausrad zoomen";
    currentImageText = "Aktuelles Bild";
    nextImageText = 'N&auml;chstes Bild';
    previousImageText = 'Voriges Bild';
    showAllText = "Alle Bilder zeigen";
  }

  document.title=title;
  
  // create the instructions control
  var instructions = getInstructions(showDirection);
  removeElementById("instructions");
  var html = '<div class="htmlControl" style="font-weight: bold"><center>'+instructions+'</center></div>';
  instructionsControl = new HtmlControl(html);
  map.addControl(instructionsControl, new GControlPosition(G_ANCHOR_TOP_RIGHT, new GSize(7, 30)));
  
  // create a menu
  menuHtml='<div style="background-color:transparent;">' +
    		   '<div class="htmlControl htmlMenuItem" id="menuButton"><b>'+showMenuText+'</b></div>'+
    		   '<div id="menuPanel" style="display:none">'+
    		       '<div class="htmlControl" id="scrollZoomItem"><input type="checkbox" id="scrollZoomCheckBox">'+mouseZoomText+'</input></div>'+
    		       '<div class="htmlControl htmlMenuItem" id="currentImage">'+currentImageText+'</div>'+
    		       '<div class="htmlControl htmlMenuItem" id="nextImage">'+nextImageText+'</div>'+
    		       '<div class="htmlControl htmlMenuItem" id="previousImage">'+previousImageText+'</div>'+
    		       '<div class="htmlControl htmlMenuItem" id="showAll">'+showAllText+'</div>'+
    		  '</div>'+
    	 '</div>';
  map.addControl(new HtmlControl(menuHtml), new GControlPosition(G_ANCHOR_TOP_LEFT, new GSize(100, 7)));
  
  function setMenuState(visible) {
  	var menuPanel=document.getElementById('menuPanel');
  	menuPanel.style.display=visible ? 'block':'none';
  	var button=document.getElementById('menuButton');
    var html=visible?hideMenuText:showMenuText;
    html='<b>'+html+'</b>';
    button.innerHTML=html;
    // tell the main program about it
    var request = GXmlHttp.create();
    request.open("GET", "/settings/set.html?menuopen="+visible, true);
    // leave out the request.onreadystatechange = function() bit
    // we're not interested in the response from the server
    request.send(null);
  }
  // add function that opens/closes the menu
  GEvent.addDomListener(document.getElementById('menuButton'), 'click', function() {
  	var menuPanel=document.getElementById('menuPanel');
    setMenuState(menuPanel.style.display=='none');
  });
  setMenuState(args.menuopen);
  
  // handle the scroll zoom menu item
    // the initial wheel zoom status comes from the args
  if (args.wheelzoom) {
    map.enableScrollWheelZoom();
  } else {
    map.disableScrollWheelZoom();
  }
  // reflect this in the menu item
  function setScrollWheelZoomItem() {
  	var checkBox = document.getElementById("scrollZoomCheckBox");
  	  checkBox.checked = map.scrollWheelZoomEnabled();
  }
  setScrollWheelZoomItem();
  // a call back when menu item is clicked 
  document.getElementById("scrollZoomCheckBox").onclick = function() {
  	var checked = document.getElementById("scrollZoomCheckBox").checked;
  	if(checked) {
  		map.enableScrollWheelZoom();
  	} else {
  		map.disableScrollWheelZoom();
  	}
  	// tell the main program about it
    var request = GXmlHttp.create();
    request.open("GET", "/settings/set.html?wheelzoom="+checked, true);
    request.send(null);
  }

  
  // handle the 'current image' menu item
  document.getElementById("currentImage").onclick = function() {
    map.panTo(activeImage.locationMarker.getPoint());
  }
  
  // handle the 'next image' menu item
  document.getElementById("nextImage").onclick = function() {
  	currentIndex = imageInfos.indexOf(activeImage);
  	nextIndex = (currentIndex + 1) % imageInfos.size();
  	setActiveImage(imageInfos.get(nextIndex));
  	//activeImage.locationMarker.openInfoWindow();
  }
  
  // handle the 'previous image' menu item
  document.getElementById("previousImage").onclick = function() {
    currentIndex = imageInfos.indexOf(activeImage);
    previousIndex = (currentIndex -1);
    if (previousIndex < 0) {
    	 previousIndex += imageInfos.size();
    }
    setActiveImage(imageInfos.get(previousIndex));
    //activeImage.locationMarker.openInfoWindow();
  }
  
  // handle the 'show all' menu item
  document.getElementById("showAll").onclick = function() {  
    // this is a bit of. First we need to find the bounds of the images
    // this only makes sense if there is more than one image
    if (imageInfos.size() > 0) {
    	var minLatitude = 90;;
    	var maxLatitude = -90;
    	var minLongitude = 180;
    	var maxLongitude = -180;
    	for (var i= 0; i < imageInfos.size(); i++) {
    		var imageInfo = imageInfos.get(i);
    		var latLng = imageInfo.locationMarker.getLatLng();
    		if (latLng.lat() > maxLatitude) {
    			maxLatitude = latLng.lat();
    		}
    		if (latLng.lat() < minLatitude) {
    			minLatitude = latLng.lat();
    		}
    		if (latLng.lng() > maxLongitude) {
    			maxLongitude = latLng.lng();
    		}
    		if (latLng.lng() < minLongitude) {
    			minLongitude = latLng.lng();
    		}
    	}
    	var southWest = new GLatLng(minLatitude, minLongitude);
    	var northEast = new GLatLng(maxLatitude, maxLongitude);
    	var bounds = new GLatLngBounds(southWest, northEast);
    	var zoomLevel = map.getBoundsZoomLevel(bounds);
    	var centre = new GLatLng((minLatitude + maxLatitude)/2, (minLongitude + maxLongitude)/2);
    	map.setZoom(zoomLevel);
    	map.panTo(centre);
    }
  }
  
  // make sure the map still shows the marker when resized
  window.onresize = function() {
  	if (activeImage != null) {
  		map.panTo(activeImage.locationMarker.getPoint())
  	}
  };  
  
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
    var tracksURL = "/tracks/tracks.kml?south="
      + south + "&west=" + west + "&north=" + north + "&east=" + east
      + "&width=" +size.width + "&height=" + size.height;
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
      }
    }
    request.send(null);
  }
  
  GEvent.addListener(map, "moveend", mapChanged);
  GEvent.addListener(map, "load", mapChanged);
  mapChanged();
  
  GEvent.addListener(map, "zoomend", function(oldLevel, newLevel) {
    var request = GXmlHttp.create();
    request.open("GET", "/settings/set.html?zoom="+newLevel, true);
    // leave out the request.onreadystatechange = function() bit
    // we're not interested in the response from the server
    request.send(null);
  });
  
  GEvent.addListener(map, "maptypechanged", function() {
    var request = GXmlHttp.create();
    request.open("GET", "/settings/set.html?maptype=" 
       +map.getCurrentMapType().getName());
    // not interested in server response, just send the request
    request.send(null);
  });
  
} else {
  // the browser is not compatible with Google Maps
}
