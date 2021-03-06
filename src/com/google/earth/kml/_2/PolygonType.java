//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.19 at 11:07:17 AM BST 
//

package com.google.earth.kml._2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for PolygonType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;PolygonType&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://earth.google.com/kml/2.1}GeometryType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref=&quot;{http://earth.google.com/kml/2.1}geometryElements&quot;/&gt;
 *         &lt;element name=&quot;outerBoundaryIs&quot; type=&quot;{http://earth.google.com/kml/2.1}boundaryType&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;innerBoundaryIs&quot; type=&quot;{http://earth.google.com/kml/2.1}boundaryType&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolygonType", propOrder = { "extrude", "tessellate",
    "altitudeMode", "outerBoundaryIs", "innerBoundaryIs" })
@SuppressWarnings("all")
public class PolygonType extends GeometryType {

  @XmlElement(defaultValue = "0")
  protected Boolean extrude;

  @XmlElement(defaultValue = "0")
  protected Boolean tessellate;

  @XmlElement(defaultValue = "clampToGround")
  protected AltitudeModeEnum altitudeMode;

  protected BoundaryType outerBoundaryIs;

  protected List<BoundaryType> innerBoundaryIs;

  /**
   * Gets the value of the extrude property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isExtrude() {
    return extrude;
  }

  /**
   * Sets the value of the extrude property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   * 
   */
  public void setExtrude(Boolean value) {
    this.extrude = value;
  }

  /**
   * Gets the value of the tessellate property.
   * 
   * @return possible object is {@link Boolean }
   * 
   */
  public Boolean isTessellate() {
    return tessellate;
  }

  /**
   * Sets the value of the tessellate property.
   * 
   * @param value
   *          allowed object is {@link Boolean }
   * 
   */
  public void setTessellate(Boolean value) {
    this.tessellate = value;
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
   * Gets the value of the outerBoundaryIs property.
   * 
   * @return possible object is {@link BoundaryType }
   * 
   */
  public BoundaryType getOuterBoundaryIs() {
    return outerBoundaryIs;
  }

  /**
   * Sets the value of the outerBoundaryIs property.
   * 
   * @param value
   *          allowed object is {@link BoundaryType }
   * 
   */
  public void setOuterBoundaryIs(BoundaryType value) {
    this.outerBoundaryIs = value;
  }

  /**
   * Gets the value of the innerBoundaryIs property.
   * 
   * <p>
   * This accessor method returns a reference to the live list, not a snapshot.
   * Therefore any modification you make to the returned list will be present
   * inside the JAXB object. This is why there is not a <CODE>set</CODE>
   * method for the innerBoundaryIs property.
   * 
   * <p>
   * For example, to add a new item, do as follows:
   * 
   * <pre>
   * getInnerBoundaryIs().add(newItem);
   * </pre>
   * 
   * 
   * <p>
   * Objects of the following type(s) are allowed in the list
   * {@link BoundaryType }
   * 
   * 
   */
  public List<BoundaryType> getInnerBoundaryIs() {
    if (innerBoundaryIs == null) {
      innerBoundaryIs = new ArrayList<BoundaryType>();
    }
    return this.innerBoundaryIs;
  }

}
