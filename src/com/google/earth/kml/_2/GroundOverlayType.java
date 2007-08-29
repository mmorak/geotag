//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.19 at 11:07:17 AM BST 
//

package com.google.earth.kml._2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for GroundOverlayType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;GroundOverlayType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}OverlayType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;altitude&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}double&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;altitudeMode&quot; type=&quot;{http://earth.google.com/kml/2.1}altitudeModeEnum&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}LatLonBox&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GroundOverlayType", propOrder = { "altitude", "altitudeMode",
    "latLonBox" })
@SuppressWarnings("all")
public class GroundOverlayType extends OverlayType {

  @XmlElement(defaultValue = "0")
  protected Double altitude;

  @XmlElement(defaultValue = "clampToGround")
  protected AltitudeModeEnum altitudeMode;

  @XmlElement(name = "LatLonBox")
  protected LatLonBoxType latLonBox;

  /**
   * Gets the value of the altitude property.
   * 
   * @return possible object is {@link Double }
   * 
   */
  public Double getAltitude() {
    return altitude;
  }

  /**
   * Sets the value of the altitude property.
   * 
   * @param value
   *          allowed object is {@link Double }
   * 
   */
  public void setAltitude(Double value) {
    this.altitude = value;
  }

  /**
   * Gets the value of the altitudeMode property.
   * 
   * @return possible object is {@link AltitudeModeEnum }
   * 
   */
  public AltitudeModeEnum getAltitudeMode() {
    return altitudeMode;
  }

  /**
   * Sets the value of the altitudeMode property.
   * 
   * @param value
   *          allowed object is {@link AltitudeModeEnum }
   * 
   */
  public void setAltitudeMode(AltitudeModeEnum value) {
    this.altitudeMode = value;
  }

  /**
   * Gets the value of the latLonBox property.
   * 
   * @return possible object is {@link LatLonBoxType }
   * 
   */
  public LatLonBoxType getLatLonBox() {
    return latLonBox;
  }

  /**
   * Sets the value of the latLonBox property.
   * 
   * @param value
   *          allowed object is {@link LatLonBoxType }
   * 
   */
  public void setLatLonBox(LatLonBoxType value) {
    this.latLonBox = value;
  }

}
