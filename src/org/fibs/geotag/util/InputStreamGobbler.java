/**
 * Geotag
 * Copyright (C) 2007-2016 Andreas Schneider
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fibs.geotag.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class that gobbles up an input stream. Useful for use with ProcessBuilders
 * where the output of the process is not important.
 * 
 * @author Andreas Schneider
 * 
 */
public class InputStreamGobbler extends Thread {

  /** The InputStream to gobble up. */
  private InputStream inputStream;

  /**
   * The OutputStream to write the gobbled up InputStream to. Can be null, in
   * that case the InputStream is not written. This usually is System.out or
   * null.
   */
  private OutputStream outputStream;

  /** The size of the buffer used to read the input stream. */
  private int bufferSize = Constants.ONE_K; // small buffer size for reading
                                            // process

  // output

  /**
   * @param process
   *          The Process whose InputStream to gobble up
   * @param outputStream
   *          The (optional) OutputStream to echo the InputStream to, can be
   *          null.
   */
  public InputStreamGobbler(Process process, OutputStream outputStream) {
    this(process.getInputStream(), outputStream);
  }

  /**
   * @param inputStream
   *          The InputStream to gobble up
   * @param outputStream
   *          The (optional) OutputStream to echo the InputStream to, can be
   *          null.
   */
  public InputStreamGobbler(InputStream inputStream, OutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  /**
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    byte[] buffer = new byte[bufferSize];
    for (;;) {
      try {
        int read = inputStream.read(buffer);
        if (read == -1) {
          break;
        }
        if (outputStream != null) {
          outputStream.write(buffer, 0, read);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @return the inputStream
   */
  public InputStream getInputStream() {
    return inputStream;
  }

  /**
   * @param inputStream
   *          the inputStream to set
   */
  public void setInputStream(InputStream inputStream) {
    this.inputStream = inputStream;
  }

  /**
   * @return the outputStream
   */
  public OutputStream getOutputStream() {
    return outputStream;
  }

  /**
   * @param outputStream
   *          the outputStream to set
   */
  public void setOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * @return the bufferSize
   */
  protected int getBufferSize() {
    return bufferSize;
  }

  /**
   * @param bufferSize
   *          the bufferSize to set
   */
  protected void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }
}
