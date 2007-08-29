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
 * Java class for NetworkLinkControlType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;NetworkLinkControlType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;all&gt;
 *         &lt;element name=&quot;minRefreshPeriod&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;cookie&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;message&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;linkName&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;linkDescription&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;linkSnippet&quot; type=&quot;{http://earth.google.com/kml/2.1}SnippetType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;expires&quot; type=&quot;{http://earth.google.com/kml/2.1}dateTimeType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;Update&quot; type=&quot;{http://earth.google.com/kml/2.1}UpdateType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;LookAt&quot; type=&quot;{http://earth.google.com/kml/2.1}LookAtType&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkLinkControlType", propOrder = {

})
@SuppressWarnings("all")
public class NetworkLinkControlType {

  @XmlElement(defaultValue = "0")
  protected Float minRefreshPeriod;

  protected String cookie;

  protected String message;

  protected String linkName;

  protected String linkDescription;

  protected SnippetType linkSnippet;

  protected String expires;

  @XmlElement(name = "Update")
  protected UpdateType update;

  @XmlElement(name = "LookAt")
  protected LookAtType lookAt;

  /**
   * Gets the value of the minRefreshPeriod property.
   * 
   * @return possible object is {@link Float }
   * 
   */
  public Float getMinRefreshPeriod() {
    return minRefreshPeriod;
  }

  /**
   * Sets the value of the minRefreshPeriod property.
   * 
   * @param value
   *          allowed object is {@link Float }
   * 
   */
  public void setMinRefreshPeriod(Float value) {
    this.minRefreshPeriod = value;
  }

  /**
   * Gets the value of the cookie property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getCookie() {
    return cookie;
  }

  /**
   * Sets the value of the cookie property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setCookie(String value) {
    this.cookie = value;
  }

  /**
   * Gets the value of the message property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the value of the message property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setMessage(String value) {
    this.message = value;
  }

  /**
   * Gets the value of the linkName property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getLinkName() {
    return linkName;
  }

  /**
   * Sets the value of the linkName property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setLinkName(String value) {
    this.linkName = value;
  }

  /**
   * Gets the value of the linkDescription property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getLinkDescription() {
    return linkDescription;
  }

  /**
   * Sets the value of the linkDescription property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setLinkDescription(String value) {
    this.linkDescription = value;
  }

  /**
   * Gets the value of the linkSnippet property.
   * 
   * @return possible object is {@link SnippetType }
   * 
   */
  public SnippetType getLinkSnippet() {
    return linkSnippet;
  }

  /**
   * Sets the value of the linkSnippet property.
   * 
   * @param value
   *          allowed object is {@link SnippetType }
   * 
   */
  public void setLinkSnippet(SnippetType value) {
    this.linkSnippet = value;
  }

  /**
   * Gets the value of the expires property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getExpires() {
    return expires;
  }

  /**
   * Sets the value of the expires property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setExpires(String value) {
    this.expires = value;
  }

  /**
   * Gets the value of the update property.
   * 
   * @return possible object is {@link UpdateType }
   * 
   */
  public UpdateType getUpdate() {
    return update;
  }

  /**
   * Sets the value of the update property.
   * 
   * @param value
   *          allowed object is {@link UpdateType }
   * 
   */
  public void setUpdate(UpdateType value) {
    this.update = value;
  }

  /**
   * Gets the value of the lookAt property.
   * 
   * @return possible object is {@link LookAtType }
   * 
   */
  public LookAtType getLookAt() {
    return lookAt;
  }

  /**
   * Sets the value of the lookAt property.
   * 
   * @param value
   *          allowed object is {@link LookAtType }
   * 
   */
  public void setLookAt(LookAtType value) {
    this.lookAt = value;
  }

}
