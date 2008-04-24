/*	Google Maps API HtmlControl v1.1
	based on code posted on Google Maps API discussion group
	last updated/modified by Martin Pearman 28 May 2007
	http://www.panoramas.eznom.net/htmlcontrol/
*/

function HtmlControl(_html, _options) {
	this._html=_html;
	this.isVisible=true;
	this._isPrintable=false;	
	this._isSelectable=false;
	if (_options) {
		this.isVisible=(_options.visible===false)?false:true;
		this._isPrintable=(_options.printable===true)?true:false;
		this._isSelectable=(_options.selectable===true)?true:false;
	}
	this.setVisible=function(_bool) {
		this._div.style.display=(_bool)? 'block':'none';
		this.isVisible=_bool;
	};
}
HtmlControl.prototype=new GControl();
HtmlControl.prototype.initialize=function(_map) {
	this.selectable=function() {
		return this._isSelectable;
	};
	this.printable=function() {
		return this._isPrintable;
	};
	this._div=document.createElement('div');
	this._div.innerHTML=this._html;
	this.setVisible(this.isVisible);
	_map.getContainer().appendChild(this._div);
	return this._div;
};
HtmlControl.prototype.getDefaultPosition=function() {
	return new GControlPosition(G_ANCHOR_TOP_LEFT, new GSize(7,7));
};
