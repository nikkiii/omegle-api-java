/**
 *  This file is part of Omegle API - Java.
 *
 *  Omegle API - Java is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Omegle API - Java is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with  Omegle API - Java.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.nikki.omegle.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An HTTP Utility class, only contains POST methods.
 * 
 * @author Nikki
 * 
 */
public class HttpUtil {

	/**
	 * POST to the specified URL with the specified map of values.
	 * 
	 * @param url
	 *            The URL
	 * @param values
	 *            The values to implode
	 * @return The HTTP response
	 * @throws IOException
	 *             If an error occurred while connecting/receiving the data
	 */
	public static String post(URL url, Map<String, Object> values)
			throws IOException {
		return post(url, implode(values));
	}

	/**
	 * POST to the specified URL with the specified data. Data MUST be encoded
	 * if it is sent.
	 * 
	 * @param url
	 *            The URL to send to
	 * @param data
	 *            The data to send
	 * @return The response
	 * @throws IOException
	 *             If an error occurred while connecting/receiving the data
	 */
	public static String post(URL url, String data) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Omegle API 1.0");
		conn.setDoOutput(true);

		OutputStreamWriter writer = new OutputStreamWriter(
				conn.getOutputStream());
		writer.write(data);
		writer.flush();

		StringBuilder out = new StringBuilder();

		String line;

		BufferedReader input = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		try {
			while ((line = input.readLine()) != null) {
				out.append(line).append("\n");
			}
		} finally {
			input.close();
		}

		return out.toString().trim();
	}

	/**
	 * Implode a map of key -> value pairs to a URL safe string
	 * 
	 * @param values
	 *            The values to implode
	 * @return The imploded string
	 * @throws IOException
	 *             If an error occurred while encoding any values.
	 */
	public static String implode(Map<String, Object> values) throws IOException {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String, Object>> iterator = values.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			builder.append(entry.getKey());

			if (entry.getValue() != null) {
				builder.append("=")
						.append(URLEncoder.encode(entry.getValue().toString(),
								"UTF-8"));
			}
			if (iterator.hasNext())
				builder.append("&");
		}
		return builder.toString();
	}
	
	/**
	 * Parse an http query string
	 * @param string
	 * 			The string to parse
	 * @return
	 * 			The parsed string in a map.
	 */
	public static Map<String, Object> parseQueryString(String string) {
		Map<String, Object> values = new HashMap<String, Object>();
		String[] split = string.split("&");
		
		for(String s : split) {
			if(s.indexOf('=') != -1) {
				values.put(s.substring(0, s.indexOf('=')), s.substring(s.indexOf('=')+1));
			} else {
				values.put(s, null);
			}
		}
		
		return values;
	}
}
