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
 * Java class for OrientationType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;OrientationType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}ObjectType&quot;&gt;
 *       &lt;all&gt;
 *         &lt;element name=&quot;heading&quot; type=&quot;{http://earth.google.com/kml/2.1}angle360&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;tilt&quot; type=&quot;{http://earth.google.com/kml/2.1}angle360&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;roll&quot; type=&quot;{http://earth.google.com/kml/2.1}angle360&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/all&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrientationType", propOrder = { "heading", "tilt", "roll" })
@SuppressWarnings("all")
public class OrientationType extends ObjectType {

  @XmlElement(defaultValue = "0.0")
  protected Double heading;

  @XmlElement(defaultValue = "0.0")
  protected Double tilt;

  @XmlElement(defaultValue = "0.0")
  protected Double roll;

  /**
   * Gets the value of the heading property.
   * 
   * @return possible object is {@link Double }
   * 
   */
  public Double getHeading() {
    return heading;
  }

  /**
   * Sets the value of the heading property.
   * 
   * @param value
   *          allowed object is {@link Double }
   * 
   */
  public void setHeading(Double value) {
    this.heading = value;
  }

  /**
   * Gets the value of the tilt property.
   * 
   * @return possible object is {@link Double }
   * 
   */
  public Double getTilt() {
    return tilt;
  }

  /**
   * Sets the value of the tilt property.
   * 
   * @param value
   *          allowed object is {@link Double }
   * 
   */
  public void setTilt(Double value) {
    this.tilt = value;
  }

  /**
   * Gets the value of the roll property.
   * 
   * @return possible object is {@link Double }
   * 
   */
  public Double getRoll() {
    return roll;
  }

  /**
   * Sets the value of the roll property.
   * 
   * @param value
   *          allowed object is {@link Double }
   * 
   */
  public void setRoll(Double value) {
    this.roll = value;
  }

}