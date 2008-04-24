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
 * 
 * 
 * 
 * Not final="#all" to show how IconType extended LinkType in KML 2.0.
 * 
 * 
 * 
 * 
 * <p>
 * Java class for LinkType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;LinkType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}ObjectType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;href&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}anyURI&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;refreshMode&quot; type=&quot;{http://earth.google.com/kml/2.1}refreshModeEnum&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;refreshInterval&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;viewRefreshMode&quot; type=&quot;{http://earth.google.com/kml/2.1}viewRefreshModeEnum&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;viewRefreshTime&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;viewBoundScale&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;viewFormat&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;httpQuery&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkType", propOrder = { "href", "refreshMode",
    "refreshInterval", "viewRefreshMode", "viewRefreshTime", "viewBoundScale",
    "viewFormat", "httpQuery" })
@SuppressWarnings("all")
public class LinkType extends ObjectType {

  protected String href;

  @XmlElement(defaultValue = "onChange")
  protected RefreshModeEnum refreshMode;

  @XmlElement(defaultValue = "4")
  protected Float refreshInterval;

  @XmlElement(defaultValue = "never")
  protected ViewRefreshModeEnum viewRefreshMode;

  @XmlElement(defaultValue = "4")
  protected Float viewRefreshTime;

  @XmlElement(defaultValue = "1")
  protected Float viewBoundScale;

  protected String viewFormat;

  protected String httpQuery;

  /**
   * Gets the value of the href property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getHref() {
    return href;
  }

  /**
   * Sets the value of the href property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setHref(String value) {
    this.href = value;
  }

  /**
   * Gets the value of the refreshMode property.
   * 
   * @return possible object is {@link RefreshModeEnum }
   * 
   */
  public RefreshModeEnum getRefreshMode() {
    return refreshMode;
  }

  /**
   * Sets the value of the refreshMode property.
   * 
   * @param value
   *          allowed object is {@link RefreshModeEnum }
   * 
   */
  public void setRefreshMode(RefreshModeEnum value) {
    this.refreshMode = value;
  }

  /**
   * Gets the value of the refreshInterval property.
   * 
   * @return possible object is {@link Float }
   * 
   */
  public Float getRefreshInterval() {
    return refreshInterval;
  }

  /**
   * Sets the value of the refreshInterval property.
   * 
   * @param value
   *          allowed object is {@link Float }
   * 
   */
  public void setRefreshInterval(Float value) {
    this.refreshInterval = value;
  }

  /**
   * Gets the value of the viewRefreshMode property.
   * 
   * @return possible object is {@link ViewRefreshModeEnum }
   * 
   */
  public ViewRefreshModeEnum getViewRefreshMode() {
    return viewRefreshMode;
  }

  /**
   * Sets the value of the viewRefreshMode property.
   * 
   * @param value
   *          allowed object is {@link ViewRefreshModeEnum }
   * 
   */
  public void setViewRefreshMode(ViewRefreshModeEnum value) {
    this.viewRefreshMode = value;
  }

  /**
   * Gets the value of the viewRefreshTime property.
   * 
   * @return possible object is {@link Float }
   * 
   */
  public Float getViewRefreshTime() {
    return viewRefreshTime;
  }

  /**
   * Sets the value of the viewRefreshTime property.
   * 
   * @param value
   *          allowed object is {@link Float }
   * 
   */
  public void setViewRefreshTime(Float value) {
    this.viewRefreshTime = value;
  }

  /**
   * Gets the value of the viewBoundScale property.
   * 
   * @return possible object is {@link Float }
   * 
   */
  public Float getViewBoundScale() {
    return viewBoundScale;
  }

  /**
   * Sets the value of the viewBoundScale property.
   * 
   * @param value
   *          allowed object is {@link Float }
   * 
   */
  public void setViewBoundScale(Float value) {
    this.viewBoundScale = value;
  }

  /**
   * Gets the value of the viewFormat property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getViewFormat() {
    return viewFormat;
  }

  /**
   * Sets the value of the viewFormat property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setViewFormat(String value) {
    this.viewFormat = value;
  }

  /**
   * Gets the value of the httpQuery property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getHttpQuery() {
    return httpQuery;
  }

  /**
   * Sets the value of the httpQuery property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setHttpQuery(String value) {
    this.httpQuery = value;
  }

}
