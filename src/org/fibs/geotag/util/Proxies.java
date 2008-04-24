/**
 * Geotag
 * Copyright (C) 2007,2008 Andreas Schneider
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

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Proxy.Type;
import java.util.StringTokenizer;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;

/**
 * A class to help with proxy connections.
 * 
 * @author Andreas Schneider
 * 
 */
public final class Proxies {
  /**
   * hide constructor.
   */
  private Proxies() {
    // hide constructor
  }

  /** The names of the proxy types available. */
  public static final String[] PROXY_TYPES = {
      Messages.getString("Proxies.NoProxy"), //$NON-NLS-1$
      Messages.getString("Proxies.HttpProxy"), //$NON-NLS-1$
      Messages.getString("Proxies.SocksProxy") //$NON-NLS-1$
  };

  /**
   * @return The proxy as determined by the settings
   */
  public static Proxy getProxy() {
    // default proxy is no proxy
    Proxy proxy = Proxy.NO_PROXY;
    int type = Settings.get(SETTING.PROXY_TYPE, 0);
    if (type != 0) {
      String address = Settings.get(SETTING.PROXY_ADDRESS, ""); //$NON-NLS-1$
      StringTokenizer tokenizer = new StringTokenizer(address, ":"); //$NON-NLS-1$
      try {
        String hostname = tokenizer.nextToken();
        int port = Integer.parseInt(tokenizer.nextToken());
        SocketAddress socketAddress = new InetSocketAddress(hostname, port);
        if (type == 1) {
          proxy = new Proxy(Type.HTTP, socketAddress);
        } else if (type == 2) {
          proxy = new Proxy(Type.SOCKS, socketAddress);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return proxy;
  }
}
