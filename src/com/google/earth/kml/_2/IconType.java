//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.19 at 11:07:17 AM BST 
//

package com.google.earth.kml._2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for IconType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;IconType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}LinkType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;x&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *         &lt;element name=&quot;y&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *         &lt;element name=&quot;w&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *         &lt;element name=&quot;h&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IconType", propOrder = { "x", "y", "w", "h" })
@SuppressWarnings("all")
public class IconType extends LinkType {

  protected int x;

  protected int y;

  protected int w;

  protected int h;

  /**
   * Gets the value of the x property.
   * 
   */
  public int getX() {
    return x;
  }

  /**
   * Sets the value of the x property.
   * 
   */
  public void setX(int value) {
    this.x = value;
  }

  /**
   * Gets the value of the y property.
   * 
   */
  public int getY() {
    return y;
  }

  /**
   * Sets the value of the y property.
   * 
   */
  public void setY(int value) {
    this.y = value;
  }

  /**
   * Gets the value of the w property.
   * 
   */
  public int getW() {
    return w;
  }

  /**
   * Sets the value of the w property.
   * 
   */
  public void setW(int value) {
    this.w = value;
  }

  /**
   * Gets the value of the h property.
   * 
   */
  public int getH() {
    return h;
  }

  /**
   * Sets the value of the h property.
   * 
   */
  public void setH(int value) {
    this.h = value;
  }

}
