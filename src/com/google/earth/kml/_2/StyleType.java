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
 * Java class for StyleType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;StyleType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}StyleSelectorType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}IconStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}LabelStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}LineStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}PolyStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}BalloonStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}ListStyle&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StyleType", propOrder = { "iconStyle", "labelStyle",
    "lineStyle", "polyStyle", "balloonStyle", "listStyle" })
@SuppressWarnings("all")
public class StyleType extends StyleSelectorType {

  @XmlElement(name = "IconStyle")
  protected IconStyleType iconStyle;

  @XmlElement(name = "LabelStyle")
  protected LabelStyleType labelStyle;

  @XmlElement(name = "LineStyle")
  protected LineStyleType lineStyle;

  @XmlElement(name = "PolyStyle")
  protected PolyStyleType polyStyle;

  @XmlElement(name = "BalloonStyle")
  protected BalloonStyleType balloonStyle;

  @XmlElement(name = "ListStyle")
  protected ListStyleType listStyle;

  /**
   * Gets the value of the iconStyle property.
   * 
   * @return possible object is {@link IconStyleType }
   * 
   */
  public IconStyleType getIconStyle() {
    return iconStyle;
  }

  /**
   * Sets the value of the iconStyle property.
   * 
   * @param value
   *          allowed object is {@link IconStyleType }
   * 
   */
  public void setIconStyle(IconStyleType value) {
    this.iconStyle = value;
  }

  /**
   * Gets the value of the labelStyle property.
   * 
   * @return possible object is {@link LabelStyleType }
   * 
   */
  public LabelStyleType getLabelStyle() {
    return labelStyle;
  }

  /**
   * Sets the value of the labelStyle property.
   * 
   * @param value
   *          allowed object is {@link LabelStyleType }
   * 
   */
  public void setLabelStyle(LabelStyleType value) {
    this.labelStyle = value;
  }

  /**
   * Gets the value of the lineStyle property.
   * 
   * @return possible object is {@link LineStyleType }
   * 
   */
  public LineStyleType getLineStyle() {
    return lineStyle;
  }

  /**
   * Sets the value of the lineStyle property.
   * 
   * @param value
   *          allowed object is {@link LineStyleType }
   * 
   */
  public void setLineStyle(LineStyleType value) {
    this.lineStyle = value;
  }

  /**
   * Gets the value of the polyStyle property.
   * 
   * @return possible object is {@link PolyStyleType }
   * 
   */
  public PolyStyleType getPolyStyle() {
    return polyStyle;
  }

  /**
   * Sets the value of the polyStyle property.
   * 
   * @param value
   *          allowed object is {@link PolyStyleType }
   * 
   */
  public void setPolyStyle(PolyStyleType value) {
    this.polyStyle = value;
  }

  /**
   * Gets the value of the balloonStyle property.
   * 
   * @return possible object is {@link BalloonStyleType }
   * 
   */
  public BalloonStyleType getBalloonStyle() {
    return balloonStyle;
  }

  /**
   * Sets the value of the balloonStyle property.
   * 
   * @param value
   *          allowed object is {@link BalloonStyleType }
   * 
   */
  public void setBalloonStyle(BalloonStyleType value) {
    this.balloonStyle = value;
  }

  /**
   * Gets the value of the listStyle property.
   * 
   * @return possible object is {@link ListStyleType }
   * 
   */
  public ListStyleType getListStyle() {
    return listStyle;
  }

  /**
   * Sets the value of the listStyle property.
   * 
   * @param value
   *          allowed object is {@link ListStyleType }
   * 
   */
  public void setListStyle(ListStyleType value) {
    this.listStyle = value;
  }

}