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
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for ColorStyleType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;ColorStyleType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}ObjectType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;color&quot; type=&quot;{http://earth.google.com/kml/2.1}color&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;colorMode&quot; type=&quot;{http://earth.google.com/kml/2.1}colorModeEnum&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColorStyleType", propOrder = { "color", "colorMode" })
@SuppressWarnings("all")
public abstract class ColorStyleType extends ObjectType {

  @XmlElement(type = String.class, defaultValue = "ffffffff")
  @XmlJavaTypeAdapter(HexBinaryAdapter.class)
  protected byte[] color;

  @XmlElement(defaultValue = "normal")
  protected ColorModeEnum colorMode;

  /**
   * Gets the value of the color property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public byte[] getColor() {
    return color;
  }

  /**
   * Sets the value of the color property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setColor(byte[] value) {
    this.color = ((byte[]) value);
  }

  /**
   * Gets the value of the colorMode property.
   * 
   * @return possible object is {@link ColorModeEnum }
   * 
   */
  public ColorModeEnum getColorMode() {
    return colorMode;
  }

  /**
   * Sets the value of the colorMode property.
   * 
   * @param value
   *          allowed object is {@link ColorModeEnum }
   * 
   */
  public void setColorMode(ColorModeEnum value) {
    this.colorMode = value;
  }

}
