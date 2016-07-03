/**
 * Geotag
 * Copyright (C) 2007-2013 Andreas Schneider
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
	
  var language = "en" // default language
  var showDirection = false
  var showTracks = false
  var showWikipedia = false
	
  // the image the map will be centred on
  var activeImage = null
  
  if(typeof console === "undefined"){
    console = { log: function() { } };
  }
  
  function encodeHtml(str){
    var aStr = str.split('');
    var result = "";;

    for (index = 0; index < str.length; index++) {
      var charCode = aStr[index].charCodeAt();
      if (charCode < 32 || charCode > 127) {
        result += '&#'+charCode+';';
        console.log("character code "+charCode);
      } else {
        result += aStr[index];
      }
    }
    console.log("encodeHtml: "+result);
    return str;
  }
  
  // a simple ArrayList class
  function ArrayList() {
    count = 0
    array = new Array()
    this.add = function add(image) {
      array[count]=image
      count++
    }
    this.getAll = function getAll() {
      return array
    }
    this.get = function(index) {
    	return array[index]
    }
    this.size = function() {
    	return count
    }
    this.indexOf = function(image) {
    	for (var i=0; i < count;i++) {
    		if (array[i]==image) {
    			return i
    		}
    	}
    	return -1
    }
  }
  
  // a list of all the images
  var imageInfos = new ArrayList()
  
  // ImageInfo class
  function ImageInfo(id) {
  	var instance = this
    // store the id
    this.id = id
    this.dragStartListener = function() {
    	dragStart(instance)
    }
    this.dragEndListener = function() {
    	dragEnd(instance)
    }
  }
  
  // change active image, pan to it and change title
  function setActiveImage(image) {
  	activeImage = image
	map.panTo(activeImage.locationMarker.getPosition())
    setTitle(activeImage.locationMarker)
  }
  
  // the correct icon to use for the location marker
  function getLocationIcon() {
    url = showDirection?"http://maps.google.com/mapfiles/kml/pal4/icon46.png":"http://www.google.com/mapfiles/marker.png";
    console.log("url:"+url);
    icon = {
      url: url
    }
    if (showDirection == true) {
      icon.size = new google.maps.Size(32,32)
      icon.anchor = new google.maps.Point(16,32)
    }
    return icon
  }
  
    // the icon to use for the direction marker
  function getDirectionIcon() {
  	url = "http://www.google.com/mapfiles/marker.png";
    icon = {
      url: url,
      size: new google.maps.Size(20,34),
      anchor: new google.maps.Point(10,34)
    }
    return icon
  }
  
    // the line between location and direction marker
  function createDirectionLine(imageInfo) {
    polylineOptions = {
      path: [imageInfo.locationMarker.getPosition(), imageInfo.directionMarker.getPosition()],
      strokeColor: 'red',
      map: map
    }
    return new google.maps.Polyline(polylineOptions)
  }
  
    // what to do when a marker starts being dragged
  function dragStart(imageInfo) {
    // close info window if open
    //map.closeInfoWindow()
    // remove line from camera to subject
    if (showDirection) {
      imageInfo.directionLine.setVisible(false)
    }
  }
  
  // what to to when a marker finished being dragged
  function dragEnd(imageInfo) {
    // need to calculate a new direction
    var cameraCoordinates = imageInfo.locationMarker.getPosition()
    var directionCoordinates = imageInfo.directionMarker.getPosition()
    var longitudeDistance = directionCoordinates.lng() - cameraCoordinates.lng()
    var latitudeDistance = directionCoordinates.lat() - cameraCoordinates.lat()
    // Add a full circle to avoid negative directions
    var theta = (Math.atan2(longitudeDistance ,latitudeDistance) + Math.PI * 2.0) % (Math.PI * 2.0)
    // Convert to degrees
    var newDirection = theta / Math.PI * 180
    console.log("New direction: "+newDirection)
    // make a string out of the new marker location and send it to the web server
    var url = "/update/new.html?image="
       + imageInfo.id
       + "&latitude="
       + getLatitude(imageInfo.locationMarker)
       + "&longitude="
       + getLongitude(imageInfo.locationMarker)
    if (showDirection) {
      url += "&direction=" + newDirection
    }
    updateRequest = new XMLHttpRequest();
    updateRequest.open("GET", url, true)
    updateRequest.send(null)
    // adjust the line from camera to marker
    imageInfo.directionLine = createDirectionLine(imageInfo)
    if (showDirection == false) {
      imageInfo.directionLine.setVisible(false)
    }
    // then centre the map on the new (or old) camera marker location
    setActiveImage(imageInfo)
  }
  
  // the HTML displayed in the info window for a marker is:  
  function infoWindowHtml(imageInfo) {
    var html = '<center>'
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
    return html   	
  }
  
  // create a location marker
  function createLocationMarker(imageInfo) {
    markerOptions = {
      position: new google.maps.LatLng(imageInfo.latitude, imageInfo.longitude),
      icon: getLocationIcon(showDirection),
      draggable: true,
      map: map
    }
    var marker = new google.maps.Marker(markerOptions)
    html = infoWindowHtml(imageInfo)
    var infowindow = new google.maps.InfoWindow({
      content: html
    });
    var listener = google.maps.event.addListener(marker, 'click', function() {
      infowindow.open(map,marker);
    });
    return marker
  }
  
    // create a direction marker
  function createDirectionMarker(imageInfo) {
    var scale = Math.pow(2, map.getZoom());
    var east = map.getBounds().getNorthEast().lng()
    var west = map.getBounds().getSouthWest().lng()
    var north = map.getBounds().getNorthEast().lat()
    var south = map.getBounds().getSouthWest().lat()
    var width = Math.abs(east - west)
    var height = Math.abs(north - south)
    icon = getDirectionIcon()
    var directionMarker
    // the direction marker needs to know where to place itself
    var location = new google.maps.LatLng(imageInfo.latitude, imageInfo.longitude)
    console.log("Location: "+location+" Direction "+imageInfo.direction)
    if (imageInfo.direction < 0) {
      markerOptions = {
        position: location,
        icon: icon,
        draggable: true,
        map: map
      }
      directionMarker = new google.maps.Marker(markerOptions)
    } else {
      // as the radius we use half the map height or width - minus icon height
      var radius = Math.min(height, width) / 4
      console.log("Radius: "+radius)
      var theta = (imageInfo.direction) / 180 * Math.PI
      var x = radius * Math.cos(theta)
      var y = radius * Math.sin(theta)
      console.log("Direction "+x+"/"+y)
      var coordinates = new google.maps.LatLng(location.lat() + x, location.lng() +y)
      console.log("Coordinates: "+coordinates)
      markerOptions = {
        position: coordinates,
        icon: icon,
        draggable: true,
        map: map
      }
      directionMarker = new google.maps.Marker(markerOptions)
    }
    return directionMarker
  }
  
  // ask Geotag about the images to be displayed
  function requestImageInfos(imageInfoList) {
  	// request information about the image
    var URL = "/imageinfo/imageinfo.xml?ids="
    for (var index = 0; index < imageInfoList.size(); index++) {
    	URL += index == 0 ? "" : ","
    	URL += imageInfoList.get(index).id
    }
    console.log("Requesting image infos")
    var imageInfoRequest = new XMLHttpRequest();
    imageInfoRequest.open("GET", URL, true)
    imageInfoRequest.onreadystatechange = function() {
      // only interested if the request has completed
      if (imageInfoRequest.readyState == 4) {
        // parse the information
        //console.log(imageInfoRequest);
        xmlDocument = imageInfoRequest.responseXML;
        infos = xmlDocument.documentElement.getElementsByTagName("image")
        console.log("ImageInfos: "+infos.length)
        for (var index = 0; index < infos.length; index++) {
          info = infos[index]
          id = parseFloat(info.getAttribute("id"))
          // find the imageInfo with that id
          imageInfo = null
          for (var index2 = 0; index2 < imageInfoList.size(); index2++) {
          	if (imageInfoList.get(index2).id == id) {
          		imageInfo = imageInfoList.get(index2)
          		break
          	}
          }
          // store information with the imageInfo
          if (imageInfo != null) {
            imageInfo.filename = info.getAttribute("name")
            imageInfo.width = parseInt(info.getAttribute("width"))
            imageInfo.height = parseInt(info.getAttribute("height"))
            imageInfo.hasThumbnail = (this.width != 0 && this.height != 0)
            imageInfo.latitude = parseFloat(info.getAttribute("latitude"))
            imageInfo.longitude = parseFloat(info.getAttribute("longitude"))
            imageInfo.direction = parseFloat(info.getAttribute("direction"))
            // create the location marker for this image
            imageInfo.locationMarker = createLocationMarker(imageInfo)
            console.log("Marker created")
            // Do the same for the direction marker...
            imageInfo.directionMarker = createDirectionMarker(imageInfo)
            // ...and the line between them
            imageInfo.directionLine = createDirectionLine(imageInfo)
            // If we don't show directions we hide the marker and the line
            if (showDirection == false) {
              imageInfo.directionMarker.setVisible(false)
              imageInfo.directionLine.setVisible(false)
            }
            // now we add listeners to the markers
            google.maps.event.addListener(imageInfo.locationMarker, "dragstart", imageInfo.dragStartListener)
            google.maps.event.addListener(imageInfo.directionMarker, "dragstart", imageInfo.dragStartListener)
            google.maps.event.addListener(imageInfo.locationMarker, "dragend", imageInfo.dragEndListener)
            google.maps.event.addListener(imageInfo.directionMarker, "dragend", imageInfo.dragEndListener)
            //GLog.write(requst.responseText)
            if (activeImage == null) {
              setActiveImage(imageInfo)
            }
          }
        }
      }
    }
    imageInfoRequest.send(null)
  }  
  
  // parse the URL arguments
  function Arguments() {
    var URL = String(window.location.href + "&x=x?x=x")
    var argumentArray = String(URL.split("?")[1]) /* all of the arguments */
    var pairs = argumentArray.split("&")  /* the name=value pairs */
    this.latitude=51.5
    this.longitude=0
    this.zoom=6
    this.menuopen = true
    for (var i = 0; i < pairs.length; i++) {
      pair = pairs[i].split("=")
      name = pair[0]
      value = pair[1]
      if (name == "latitude") {
        this.latitude = parseFloat(value)
      }
      if (name == "longitude") {
        this.longitude = parseFloat(value)
      }
      if (name == "images") {
      	imageIds = value.split("_")
      	for (var image = 0; image < imageIds.length; image++) {
      		id = parseInt(imageIds[image])
      		imageInfos.add(new ImageInfo(id))
      	}
      }
      if (name == "zoom") {
        this.zoom = Math.abs(parseInt(value))
      }
      if (name == "maptype") {
        this.mapTypeId = google.maps.MapTypeId.HYBRID
        if (value == "Satellite") {
          this.mapTypeId = google.maps.MapTypeId.SATELLITE
        }
        if (value == "Map") {
          this.mapTypeId = google.maps.MapTypeId.ROADMAP
        }
      }
      if (name == "direction") {
        // direction can have three different values:
        if (value == "true") {
          // show direction, but we don't have an initial value
          showDirection = true
        }
      }
      if (name == "language") {
        language = value
      }
      if (name == "menuopen") {
      	this.menuopen = (value == "true")
      }
      if (name == 'showtracks') {
      	showTracks = (value == "true")
      }
      if (name == 'wikipedia') {
        // Wikipedia entries currently disabled as InfoWindows are currently not working properly
      	showWikipedia = (value == 'true')
      }
    }
  }
  
  // parse the arguments from the URL  
  var args = new Arguments()

  // a few utility functions

  function round(number) {
    return Math.round(number*10000000)/10000000
  }

  function getLatitude(marker) {
    return round(marker.getPosition().lat())
  }

  function getLongitude(marker) {
    return round(marker.getPosition().lng())
  }
  
  function removeElementById(elementID) {
     var element = document.getElementById(elementID)
     if (element) {
     	 element.parentNode.removeChild(element)
     }
   } 
   
  // change the document title, reflecting the current marker position
  function setTitle(marker) {
    document.title = "Geotag "+getLatitude(marker)+" "+getLongitude(marker)
  }
  
  function mapProjectionReady() {
    console.log("New projection: "+map.getProjection()) 
    if (activeImage == null) {
      requestImageInfos(imageInfos)
    }
  }
  
  
  // create the map
  var mapOptions = {
      zoom: args.zoom,
      center: new google.maps.LatLng(args.latitude, args.longitude),
      mapTypeId: args.mapTypeId,
      disableDefaultUI: true,
      mapTypeControl: true,
      panControl: true,
      zoomControl: true,
      tilt: 0,
  };
  var map = new google.maps.Map(document.getElementById("map"), mapOptions)
  //map.addListener("projection_changed", mapProjectionReady)
  map.addListener("idle", mapProjectionReady)
  //requestImageInfos(imageInfos)
  // move map to desired location and zoom level

  // now the language dependent bits
//#includeI18N  

  
  // create the instructions (depending on showDirection) 
  function getInstructions(showDirection) {
    if (showDirection) {
    	return instructionsWithDirection
    }
    return instructions 
  }
  
  // setup the menu shortcuts
  findShortcut = function(text) {
  	return text.replace(/.*<u>/,'').replace(/<.u>.*/,'')
  }
  var toggleMenuShortcut = findShortcut(showMenuText) 
  var showTracksShortcut = findShortcut(showTracksText)
  var showWikipediaShortcut = findShortcut(showWikipediaText)
  var currentImageShortcut = findShortcut(currentImageText)
  var nextImageShortcut = findShortcut(nextImageText)
  var previousImageShortcut = findShortcut(previousImageText)
  var showAllShortcut = findShortcut(showAllText)
  
  document.title=title
  
  function InstructionsControl(controlDiv, map) {
    controlDiv.style.padding = '5px';  
    // create the instructions control
    var instructions = getInstructions(showDirection)
    removeElementById("instructions")
    
    // Set CSS for the control border.
    var controlUI = document.createElement('div');
    controlUI.style.backgroundColor = 'white';
    controlUI.style.borderStyle = 'solid';
    controlUI.style.borderWidth = '2px';
    controlUI.style.cursor = 'pointer';
    controlUI.style.textAlign = 'center';
    controlUI.title = 'What to do next';
    controlDiv.appendChild(controlUI);
    
    // Set CSS for the control interior.
    var controlText = document.createElement('div');
    controlText.style.fontFamily = 'Arial,sans-serif';
    controlText.style.fontSize = '100%';
    controlText.style.paddingLeft = '4px';
    controlText.style.paddingRight = '4px';
    controlText.innerHTML = encodeHtml(instructions);
    controlUI.appendChild(controlText);
  }
  
  var instructionsControlDiv = document.createElement('div')
  var instructionsControl = new InstructionsControl(instructionsControlDiv, map)
  instructionsControlDiv.index = 1
  map.controls[google.maps.ControlPosition.TOP_RIGHT].push(instructionsControlDiv);
  
  function MenuControl(controlDiv) {
  
    this.visible = true;
    
    controlDiv.style.padding = '5px';  
    // Set CSS for the control border.
    controlUI = document.createElement('div');
    controlUI.style.backgroundColor = 'white';
    controlUI.style.borderStyle = 'solid';
    controlUI.style.borderWidth = '2px';
    controlUI.style.cursor = 'pointer';
    controlUI.style.textAlign = 'left';
    controlDiv.appendChild(controlUI);
    
    // Set CSS for the control interior.
    this.menuButton = document.createElement('div');
    this.menuButton.style.fontSize = '100%';
    this.menuButton.style.paddingLeft = '4px';
    this.menuButton.style.paddingRight = '4px';
    this.menuButton.innerHTML = '<b>'+showMenuText+'</b>';
    controlUI.appendChild(this.menuButton);
    
    this.divider = document.createElement('hr');
    controlUI.appendChild(this.divider);
        
    this.showTracksItem = document.createElement('div');    
    this.showTracksItem.style.fontSize = '100%';
    this.showTracksItem.style.paddingLeft = '4px';
    this.showTracksItem.style.paddingRight = '4px';
    this.showTracksItem.innerHTML = showTracksText;
    controlUI.appendChild(this.showTracksItem);
    
    this.showWikipediaItem = document.createElement('div');
    this.showWikipediaItem.style.fontSize = '100%';
    this.showWikipediaItem.style.paddingLeft = '4px';
    this.showWikipediaItem.style.paddingRight = '4px';
    this.showWikipediaItem.innerHTML = showWikipediaText;
    controlUI.appendChild(this.showWikipediaItem);
    
    this.currentImageItem = document.createElement('div');
    this.currentImageItem.style.fontSize = '100%';
    this.currentImageItem.style.paddingLeft = '4px';
    this.currentImageItem.style.paddingRight = '4px';
    this.currentImageItem.innerHTML = currentImageText;
    controlUI.appendChild(this.currentImageItem);
    
    this.nextImageItem = document.createElement('div');
    this.nextImageItem.style.fontSize = '100%';
    this.nextImageItem.style.paddingLeft = '4px';
    this.nextImageItem.style.paddingRight = '4px';
    this.nextImageItem.innerHTML = nextImageText;
    controlUI.appendChild(this.nextImageItem);
    
    this.previousImageItem = document.createElement('div');
    this.previousImageItem.style.fontSize = '100%';
    this.previousImageItem.style.paddingLeft = '4px';
    this.previousImageItem.style.paddingRight = '4px';
    this.previousImageItem.innerHTML = previousImageText;
    controlUI.appendChild(this.previousImageItem);

    this.showAllItem = document.createElement('div');
    this.showAllItem.style.fontSize = '100%';
    this.showAllItem.style.paddingLeft = '4px';
    this.showAllItem.style.paddingRight = '4px';
    this.showAllItem.innerHTML = showAllText;
    controlUI.appendChild(this.showAllItem);  
      
    this.setMenuState = function(visible) {
      console.log("Menu visible will be "+visible);
      this.visible = visible;
      var html=visible?hideMenuText:showMenuText
      html='<b>'+html+'</b>'
      this.menuButton.innerHTML=html
      // tell the main program about it
      menuRequest = new XMLHttpRequest()
      menuRequest.open("GET", "/settings/set.html?menuopen="+visible, true)
      // leave out the menuRequest.onreadystatechange = function() bit
      // we're not interested in the response from the server
      menuRequest.send(null)
      var display = visible ? 'block' : 'none';
      this.divider.style.display = display;
      this.showTracksItem.style.display = display;
      this.showWikipediaItem.style.display = display;
      // Certain menu items don't make sense for just a single image
      if (imageInfos.size() == 1) {
        display = 'none';
      }
      this.currentImageItem.style.display = display;
      this.nextImageItem.style.display = display;
      this.previousImageItem.style.display = display;
      this.showAllItem.style.display = display;
    }
  }
  
  var menuControlDiv = document.createElement('div')
  var menuControl = new MenuControl(menuControlDiv) 
  //map.addControl(new HtmlControl(menuHtml), new GControlPosition(G_ANCHOR_TOP_LEFT, new GSize(100, 7)))
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(menuControlDiv)
  menuControl.setMenuState(args.menuopen)
  
  // add function that opens/closes the menu
  toggleMenu = function() {
  	var menuPanel=document.getElementById('menuPanel')
    menuControl.setMenuState(!menuControl.visible)
  }
  menuControl.menuButton.onclick = toggleMenu
    
  // show tracks or not menu item
  updateShowTracksCheckbox = function() {
    var fontWeight = showTracks ? "bold" : "normal"
    menuControl.showTracksItem.style.fontWeight = fontWeight
  }
  updateShowTracksCheckbox()
  
    // a callback when show tracks is clicked
  showTracksClicked = function() {
  	showTracks = ! showTracks
  	console.log("Show tracks: "+showTracks);
  	removeTracks()
  	if (showTracks) {
  		requestTracks()
  	}
  	// tell the main program about it
    showTracksRequest = new XMLHttpRequest();
    showTracksRequest.open("GET", "/settings/set.html?showtracks="+showTracks, true)
    showTracksRequest.send(null)
    updateShowTracksCheckbox()
  }
  menuControl.showTracksItem.onclick = showTracksClicked
  
    // show Wikipedia or not menu item
  updateShowWikipediaCheckbox = function() {
    var fontWeight = showWikipedia ? "bold" : "normal"
    menuControl.showWikipediaItem.style.fontWeight = fontWeight
  }
  updateShowWikipediaCheckbox()
  
  // a callback when show Wikipedia is clicked
  showWikipediaClicked = function() {
    showWikipedia = ! showWikipedia
    removeWikipediaEntries()
    if (showWikipedia) {
      requestWikipediaEntries()
    }
    // tell the main program about it
    showWikiRequest = new XMLHttpRequest()
    showWikiRequest.open("GET", "/settings/set.html?wikipedia="+showWikipedia, true)
    showWikiRequest.send(null)
    updateShowWikipediaCheckbox()
  }
  menuControl.showWikipediaItem.onclick = showWikipediaClicked
  
  // handle the 'current image' menu item
  gotoCurrentImage = function() {
  	map.panTo(activeImage.locationMarker.getPosition())
  }
  menuControl.currentImageItem.onclick = gotoCurrentImage
  
  // handle the 'next image' menu item
  gotoNextImage = function() {
  	currentIndex = imageInfos.indexOf(activeImage)
    nextIndex = (currentIndex + 1) % imageInfos.size()
    setActiveImage(imageInfos.get(nextIndex))
    //activeImage.locationMarker.openInfoWindow()
  }
  menuControl.nextImageItem.onclick = gotoNextImage
  
  // handle the 'previous image' menu item
  gotoPreviousImage = function() {
    currentIndex = imageInfos.indexOf(activeImage)
    previousIndex = (currentIndex -1)
    if (previousIndex < 0) {
       previousIndex += imageInfos.size()
    }
    setActiveImage(imageInfos.get(previousIndex))
    //activeImage.locationMarker.openInfoWindow()	
  }
  menuControl.previousImageItem.onclick = gotoPreviousImage
  
  // Next method was found here: http://stackoverflow.com/questions/9837017/equivalent-of-getboundszoomlevel-in-gmaps-api-3

  /**
   * Returns the zoom level at which the given rectangular region fits in the map view. 
   * The zoom level is computed for the currently selected map type. 
   * @param {google.maps.Map} map
   * @param {google.maps.LatLngBounds} bounds 
   * @return {Number} zoom level
  **/
  function getZoomByBounds( map, bounds ){
    var MAX_ZOOM = map.mapTypes.get( map.getMapTypeId() ).maxZoom || 21 ;
    var MIN_ZOOM = map.mapTypes.get( map.getMapTypeId() ).minZoom || 0 ;

    var northEast = map.getProjection().fromLatLngToPoint( bounds.getNorthEast() );
    var southWest = map.getProjection().fromLatLngToPoint( bounds.getSouthWest() ); 

    var worldCoordWidth = Math.abs(northEast.x-southWest.x);
    var worldCoordHeight = Math.abs(northEast.y-southWest.y);

    //Fit padding in pixels 
    var FIT_PAD = 40;

    for( var zoom = MAX_ZOOM; zoom >= MIN_ZOOM; --zoom ){ 
    if( worldCoordWidth*(1<<zoom)+2*FIT_PAD < map.getDiv().offsetWidth && 
      worldCoordHeight*(1<<zoom)+2*FIT_PAD < map.getDiv().offsetHeight )
      return zoom;
    }
    return 0;
  }

  // handle the 'show all' menu item
  showAllImages = function() {
    // First we need to find the bounds of the images
    // this only makes sense if there is more than one image
    if (imageInfos.size() > 0) {
      var minLatitude = 90
      var maxLatitude = -90
      var minLongitude = 180
      var maxLongitude = -180
      for (var i= 0; i < imageInfos.size(); i++) {
        var imageInfo = imageInfos.get(i)
        var latLng = imageInfo.locationMarker.getPosition()
        if (latLng.lat() > maxLatitude) {
          maxLatitude = latLng.lat()
        }
        if (latLng.lat() < minLatitude) {
          minLatitude = latLng.lat()
        }
        if (latLng.lng() > maxLongitude) {
          maxLongitude = latLng.lng()
        }
        if (latLng.lng() < minLongitude) {
          minLongitude = latLng.lng()
        }
      }
      var southWest = new google.maps.LatLng(minLatitude, minLongitude)
      var northEast = new google.maps.LatLng(maxLatitude, maxLongitude)
      var bounds = new google.maps.LatLngBounds(southWest, northEast)
      var zoomLevel = getZoomByBounds(map, bounds)
      var centre = new google.maps.LatLng((minLatitude + maxLatitude)/2, (minLongitude + maxLongitude)/2)
      map.setZoom(zoomLevel)
      map.panTo(centre)
    }  	
  }
  menuControl.showAllItem.onclick = showAllImages 
  
    document.onkeydown = function(e) {
  	var keyCharCode
  	var keyChar
  	if(window.event) { // IE
      keyCharCode = e.keyCode
    } else if(e.which) { // Netscape/Firefox/Opera
      keyCharCode = e.which
    }
    keyChar = String.fromCharCode(keyCharCode)
    if (keyChar == toggleMenuShortcut.toUpperCase()) {
    	toggleMenu()
    } else if (keyChar == showTracksShortcut.toUpperCase()) {
    	showTracksClicked()
    } else if (keyChar == showWikipediaShortcut.toUpperCase()) {
    	showWikipediaClicked()
    } else if (keyChar == currentImageShortcut.toUpperCase()) {
      gotoCurrentImage()
    } else if (keyChar == nextImageShortcut.toUpperCase()) {
    	gotoNextImage()
    } else if (keyChar == previousImageShortcut.toUpperCase()) {
    	gotoPreviousImage()
    } else if (keyChar == showAllShortcut.toUpperCase()) {
    	showAllImages()
    }
    return false
  } 
  
  // we can display GPS tracks as well
  var tracksDisplayed = []
  
  removeTracks = function() {
  removed = 0;
  	// remove all tracks currently displayed
    for (var trackIndex = 0; trackIndex < tracksDisplayed.length; trackIndex++) {
      tracksDisplayed[trackIndex].setMap(null)
      removed++;
    } 
    // a new empty array
    tracksDisplayed = []
    console.log("Tracks removed: "+removed);
  }
  
  requestTracks = function() {
    // collect information about the map
    var bounds = map.getBounds()
    var south = bounds.getSouthWest().lat()
    var west = bounds.getSouthWest().lng()
    var north = bounds.getNorthEast().lat()
    var east = bounds.getNorthEast().lng()
    var width = map.getDiv().offsetWidth 
    var height = map.getDiv().offsetHeight
    // tell Geotag about it
    var tracksURL = "/tracks/tracks.kml?south="
      + south + "&west=" + west + "&north=" + north + "&east=" + east
      + "&width=" +width + "&height=" + height
    console.log("Request "+tracksURL)
    var tracksRequest = new XMLHttpRequest()
    tracksRequest.open("GET", tracksURL, true)
    // Geotag will send tracks for this map
    tracksRequest.onreadystatechange = function() {
      // only interested if the request has completed
      if (tracksRequest.readyState == 4) {
        removeTracks()
        var numPoints = 0
        console.log("Response: "+tracksRequest.responseText)
        // parse the document
        var xmlDocument = tracksRequest.responseXML
        // get the tracks
        var tracks = xmlDocument.documentElement.getElementsByTagName("track")
        // loop through the tracks
        for (var trackIndex = 0; trackIndex < tracks.length; trackIndex++) {
          var points = tracks[trackIndex].getElementsByTagName("point")
          var linePoints = []
          for (var pointIndex = 0; pointIndex < points.length; pointIndex++ ) {
            numPoints ++
            linePoints[pointIndex] = new google.maps.LatLng(parseFloat(points[pointIndex].getAttribute("latitude")),
                             parseFloat(points[pointIndex].getAttribute("longitude")))
          }
          tracksDisplayed[trackIndex] =  new google.maps.Polyline({
            path: linePoints,
            geodesic: true,
            strokeColor: '#0000FF',
            strokeOpacity: 0.5,
            strokeWeight: 5,
            map: map
          }); 
           
        }
        console.log("TRACKS DONE "+tracksDisplayed.length+"/"+tracks.length)  	
      }
    }
    tracksRequest.send(null)
  }
  
  // we can also show nearby Wikipedia entries

  function WikipediaMarker(location, title, summary, wikipediaUrl) {
    var icon = {
      url: "http://maps.google.com/mapfiles/kml/pal3/icon35.png"
    }
    markerOptions = {
      position: location,
      icon: icon,
      draggable: false,
      map: map
    }   
    var marker = new google.maps.Marker(markerOptions) 
    html = '<div id="content">'+
      '<div id="bodyContent">'+
      '<p><b>'+title+'</b></p>'+
      '<p>'+summary+'</p>' +
      '<p><a href="' +wikipediaUrl + '">Wikipedia</a></p>' +
      '</div>'+
       '</div>';
     var infowindow = new google.maps.InfoWindow({
       content: html
     });
     var listener = google.maps.event.addListener(marker, 'click', function() {
       infowindow.open(map,marker);
     });
     this.remove = function() {
       marker.setMap(null)
       google.maps.event.removeListener(listener);
     }
  }
  
  wikipediaEntries = []
  
  removeWikipediaEntries = function() {
    console.log('Remove Wikipedia markers')
    // remove all wikipedia entries currently displayed
    for (var index = 0; index < wikipediaEntries.length; index++) {
      wikipediaEntries[index].remove();
    }
    // a new empty array
    wikipediaEntries = []
  }
      
  requestWikipediaEntries = function() {
    // collect information about the map
    var bounds = map.getBounds()
    console.log("Bounds: "+JSON.stringify(bounds))
    var south = bounds.getSouthWest().lat()
    var west = bounds.getSouthWest().lng()
    var north = bounds.getNorthEast().lat()
    var east = bounds.getNorthEast().lng()
    // tell geonames.org about it
    var URL = "/geonames/wikipediaBoundingBox?south="
      + south + "&west=" + west + "&north=" + north + "&east=" + east
      + "&lang=" + language
    var wikipediaRequest = new XMLHttpRequest()
    console.log("Requesting Wikipedia data")
    wikipediaRequest.open("GET", URL, true)
    // geonames will send wikipedia entries for this map
    wikipediaRequest.onreadystatechange = function() {
      console.log("Ready state: "+wikipediaRequest.readyState)
      // only interested if the wikipediaRequest has completed
      if (wikipediaRequest.readyState == 4) {
        removeWikipediaEntries()
        //console.log(wikipediaRequest.responseText)
        var numEntries = 0
        // parse the document
        var xmlDocument = wikipediaRequest.responseXML
        //console.log(xmlDocument)
        //GLog.write(wikipediaRequest.responseText)
        // get the geonames entry
        var entries = xmlDocument.documentElement.getElementsByTagName("entry")
        //GLog.write(entries.length+" entries")
        // loop through the entries
        for (var entryIndex = 0; entryIndex < entries.length; entryIndex++) {
          var entry = entries[entryIndex]
          var title = entry.getElementsByTagName("title")[0].textContent;
          var latitude = parseFloat(entry.getElementsByTagName("lat")[0].textContent)
          var longitude = parseFloat(entry.getElementsByTagName("lng")[0].textContent)
          var wikipediaUrl = entry.getElementsByTagName("wikipediaUrl")[0].textContent
          var thumbnail = entry.getElementsByTagName("thumbnailImg")[0].textContent
          var summary = entry.getElementsByTagName("summary")[0].textContent
          var location = new google.maps.LatLng(latitude, longitude)
          wikipediaEntries[entryIndex] = new WikipediaMarker(location, title, summary, wikipediaUrl)
        }
      }
    }
    wikipediaRequest.send(null)    
  }


  // a function to be called every time the map changes
  mapChanged = function () {
  console.log("Map changed: "+showWikipedia)
  	if (showTracks) {
      requestTracks()
  	}
  	if (showWikipedia) {
  	  requestWikipediaEntries()
  	}
  }
  
  google.maps.event.addListener(map, "moveend", mapChanged)
  google.maps.event.addListener(map, "load", mapChanged)
  map.addListener("idle", mapChanged)
  
  //mapChanged()