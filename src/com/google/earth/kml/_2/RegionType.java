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
 * Java class for RegionType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;RegionType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}ObjectType&quot;&gt;
 *       &lt;all&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}LatLonAltBox&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}Lod&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/all&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegionType", propOrder = { "latLonAltBox", "lod" })
@SuppressWarnings("all")
public class RegionType extends ObjectType {

  @XmlElement(name = "LatLonAltBox", required = true)
  protected LatLonAltBoxType latLonAltBox;

  @XmlElement(name = "Lod")
  protected LodType lod;

  /**
   * Gets the value of the latLonAltBox property.
   * 
   * @return possible object is {@link LatLonAltBoxType }
   * 
   */
  public LatLonAltBoxType getLatLonAltBox() {
    return latLonAltBox;
  }

  /**
   * Sets the value of the latLonAltBox property.
   * 
   * @param value
   *          allowed object is {@link LatLonAltBoxType }
   * 
   */
  public void setLatLonAltBox(LatLonAltBoxType value) {
    this.latLonAltBox = value;
  }

  /**
   * Gets the value of the lod property.
   * 
   * @return possible object is {@link LodType }
   * 
   */
  public LodType getLod() {
    return lod;
  }

  /**
   * Sets the value of the lod property.
   * 
   * @param value
   *          allowed object is {@link LodType }
   * 
   */
  public void setLod(LodType value) {
    this.lod = value;
  }

}
