
package com.lizardworks.tiff;

import com.lizardworks.tiff.tag.UnisysISISTags;
import com.lizardworks.util.MemoryFileInputFilter;

/*******************************************************************************
 * { 1, "DIN" }, { 2, "Check Serial Num" }, { 3, "Processing Date" }, { 4,
 * "Check Amount" }, { 5, "Transaction Code" }, { 6, "Account Number" }, { 7,
 * "Transit Transit" }, { 8, "Auxiliary On Us" }, { 9, "Position 44" }, { 10,
 * "Processing Date" }, { 11, "User Area" }, { 0, "User ID" }, { 1, "Site ID" }, {
 * 2, "Capture Start" }, { 3, "Capture PID" }, { 4, "Document Num" }, { 5,
 * "Migration Date" }, { 6, "Micro Film Seq" }, { 7, "Item Seq" }, { 8, "Tracer
 * Number" }, { 9, "Side ID" }, //0=front 1=back { 10, "Zone List" }, { 11,
 * "Next Level 1 IFD offset" },
 ******************************************************************************/

@SuppressWarnings("all")
class UnisysISIS_IFD extends IFD {
  long userAreaIFD = 0;

  public String toString() {
    String s;
    int i;
    s = "Unisys ISIS IFD Entry Count: " + count + "\n";
    for (i = 0; i < count; i++) {
      UnisysISISTags ut = new UnisysISISTags(entries[i].tag.Value());
      s += "\tTag: " + ut.toString() + ", Type: " + entries[i].type.toString()
          + ", Count: " + entries[i].count;

      if (entries[i].isOffset()) {
        int len = Math.min(entries[i].dataArray.length, 256);
        // s += ", Offset: 0x" + Long.toHexString(entries[i].value) + ", " +
        // entries[i].sizeOfData() + "bytes";

        if (entries[i].type.isAscii()) {
          String sz = new String(entries[i].dataArray); // jdk1.1 allows
          // String(byte[])
          s += "\n\t\tValue:" + sz + "\n";
        }
      } else
        s += "\n\t\tValue:" + entries[i].value + "\n";
    }
    s += "\t\tUser Area Offset: " + userAreaIFD + "\n";
    return s;
  }

  public void read(MemoryFileInputFilter in) {
    int i, nStripOffsets = 0, nStripByteCounts = 0;
    String s;
    int compType = 0; // compression type

    count = in.readUnsignedShort();
    entries = new IFDEntry[count];

    for (i = 0; i < count; i++) {
      entries[i] = new IFDEntry();
      entries[i].read(in);
      if (entries[i].tag.equals(UnisysISISTags.USER))
        userAreaIFD = entries[i].value;
    }
    offset = in.readInt();
  }

}
