//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.0 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.08.19 at 11:07:17 AM BST 
//

package com.google.earth.kml._2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * <p>
 * Java class for unitsEnum.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name=&quot;unitsEnum&quot;&gt;
 *   &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;&gt;
 *     &lt;enumeration value=&quot;fraction&quot;/&gt;
 *     &lt;enumeration value=&quot;pixels&quot;/&gt;
 *     &lt;enumeration value=&quot;insetPixels&quot;/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlEnum
@SuppressWarnings("all")
public enum UnitsEnum {

  @XmlEnumValue("fraction")
  FRACTION("fraction"), @XmlEnumValue("pixels")
  PIXELS("pixels"), @XmlEnumValue("insetPixels")
  INSET_PIXELS("insetPixels");
  private final String value;

  UnitsEnum(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static UnitsEnum fromValue(String v) {
    for (UnitsEnum c : UnitsEnum.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v.toString());
  }

}
