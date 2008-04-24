//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.19 at 11:07:17 AM BST 
//

package com.google.earth.kml._2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for FeatureType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;FeatureType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}ObjectType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;name&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;visibility&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;open&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}boolean&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;address&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;phoneNumber&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;Snippet&quot; type=&quot;{http://earth.google.com/kml/2.1}SnippetType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;description&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}string&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}LookAt&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}TimePrimitive&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}styleUrl&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}StyleSelector&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element ref=&quot;{http://earth.google.com/kml/2.1}Region&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;Metadata&quot; type=&quot;{http://earth.google.com/kml/2.1}MetadataType&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FeatureType", propOrder = { "name", "visibility", "open",
    "address", "phoneNumber", "snippet", "description", "lookAt",
    "timePrimitive", "styleUrl", "styleSelector", "region", "metadata" })
@SuppressWarnings("all")
public abstract class FeatureType extends ObjectType {

  protected String name;

  @XmlElement(defaultValue = "1")
  protected Boolean visibility;

  @XmlElement(defaultValue = "1")
  protected Boolean open;

  protected String address;

  protected String phoneNumber;

  @XmlElement(name = "Snippet")
  protected SnippetType snippet;

  protected String description;

  @XmlElement(name = "LookAt")
  protected LookAtType lookAt;

  @XmlElementRef(name = "TimePrimitive", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class)
  protected JAXBElement<? extends TimePrimitiveType> timePrimitive;

  protected String styleUrl;

  @XmlElementRef(name = "StyleSelector", namespace = "http://earth.google.com/kml/2.1", type = JAXBElement.class)
  protected List<JAXBElement<? extends StyleSelectorType>> styleSelector;

  @XmlElement(name = "Region")
  protected RegionType region;

  @XmlElement(name = "Metadata")
  protected MetadataType metadata;

  /**
   * Gets the value of the name property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the value of the name property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setName(String value) {
    this.name = value;
  }

  /**
   * Gets the value of the visibility property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isVisibility() {
    return visibility;
  }

  /**
   * Sets the value of the visibility property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   * 
   */
  public void setVisibility(Boolean value) {
    this.visibility = value;
  }

  /**
   * Gets the value of the open property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isOpen() {
    return open;
  }

  /**
   * Sets the value of the open property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   * 
   */
  public void setOpen(Boolean value) {
    this.open = value;
  }

  /**
   * Gets the value of the address property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the value of the address property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setAddress(String value) {
    this.address = value;
  }

  /**
   * Gets the value of the phoneNumber property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the value of the phoneNumber property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setPhoneNumber(String value) {
    this.phoneNumber = value;
  }

  /**
   * Gets the value of the snippet property.
   * 
   * @return possible object is {@link SnippetType }
   * 
   */
  public SnippetType getSnippet() {
    return snippet;
  }

  /**
   * Sets the value of the snippet property.
   * 
   * @param value
   *          allowed object is {@link SnippetType }
   * 
   */
  public void setSnippet(SnippetType value) {
    this.snippet = value;
  }

  /**
   * Gets the value of the description property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the value of the description property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setDescription(String value) {
    this.description = value;
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

  /**
   * Gets the value of the timePrimitive property.
   * 
   * @return possible object is {@link JAXBElement }{@code <}{@link TimePrimitiveType }{@code >}
   *         {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
   *         {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
   * 
   */
  public JAXBElement<? extends TimePrimitiveType> getTimePrimitive() {
    return timePrimitive;
  }

  /**
   * Sets the value of the timePrimitive property.
   * 
   * @param value
   *          allowed object is {@link JAXBElement }{@code <}{@link TimePrimitiveType }{@code >}
   *          {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}
   *          {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}
   * 
   */
  public void setTimePrimitive(JAXBElement<? extends TimePrimitiveType> value) {
    this.timePrimitive = ((JAXBElement<? extends TimePrimitiveType>) value);
  }

  /**
   * Gets the value of the styleUrl property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getStyleUrl() {
    return styleUrl;
  }

  /**
   * Sets the value of the styleUrl property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setStyleUrl(String value) {
    this.styleUrl = value;
  }

  /**
   * Gets the value of the styleSelector property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE>
   * method for the styleSelector property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getStyleSelector().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link JAXBElement }{@code <}{@link StyleSelectorType }{@code >}
   * {@link JAXBElement }{@code <}{@link StyleType }{@code >}
   * {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}
   * 
   * 
   */
  public List<JAXBElement<? extends StyleSelectorType>> getStyleSelector() {
    if (styleSelector == null) {
      styleSelector = new ArrayList<JAXBElement<? extends StyleSelectorType>>();
    }
    return this.styleSelector;
  }

  /**
   * Gets the value of the region property.
   * 
   * @return possible object is {@link RegionType }
   * 
   */
  public RegionType getRegion() {
    return region;
  }

  /**
   * Sets the value of the region property.
   * 
   * @param value
   *          allowed object is {@link RegionType }
   * 
   */
  public void setRegion(RegionType value) {
    this.region = value;
  }

  /**
   * Gets the value of the metadata property.
   * 
   * @return possible object is {@link MetadataType }
   * 
   */
  public MetadataType getMetadata() {
    return metadata;
  }

  /**
   * Sets the value of the metadata property.
   * 
   * @param value
   *          allowed object is {@link MetadataType }
   * 
   */
  public void setMetadata(MetadataType value) {
    this.metadata = value;
  }

}
