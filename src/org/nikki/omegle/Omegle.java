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
package org.nikki.omegle;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;
import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleMode;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.event.OmegleEventListener;
import org.nikki.omegle.util.HttpUtil;

/**
 * The main API class
 * 
 * @author Nikki
 * 
 */
public class Omegle implements Runnable {

	/**
	 * The base omegle url
	 */
	public static String BASE_URL = "http://omegle.com";

	/**
	 * The URL used to start a chat
	 */
	public static URL OPEN_URL;

	/**
	 * The URL used to disconnect from a chat
	 */
	public static URL DISCONNECT_URL;

	/**
	 * The URL used to parse events
	 */
	public static URL EVENT_URL;

	/**
	 * The URL used to send messages
	 */
	public static URL SEND_URL;

	/**
	 * The URL used to change typing status
	 */
	public static URL TYPING_URL;

	static {
		try {
			OPEN_URL = new URL(BASE_URL + "/start");
			DISCONNECT_URL = new URL(BASE_URL + "/disconnect");
			EVENT_URL = new URL(BASE_URL + "/events");
			SEND_URL = new URL(BASE_URL + "/send");
			TYPING_URL = new URL(BASE_URL + "/typing");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ExecutorService used to run event checks
	 */
	private ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * A list of all sessions currently active
	 */
	private List<OmegleSession> sessions = new LinkedList<OmegleSession>();

	/**
	 * The 'firstEvents' flag
	 */
	private boolean firstEvents = false;

	/**
	 * The event parsing delay
	 */
	private long eventParseDelay = 5000;

	/**
	 * Constructor
	 */
	public Omegle() {
		service.execute(this);
	}

	/**
	 * Main thread to parse chat messages.
	 */
	public void run() {
		while (true) {
			synchronized (sessions) {
				for (final OmegleSession session : sessions) {
					// In case anybody tries to run blocking operations ._.
					service.execute(new Runnable() {
						public void run() {
							session.checkEvents();
						}
					});
				}
			}
			try {
				Thread.sleep(eventParseDelay);
			} catch (Exception e) {
				// uh
			}
		}
	}

	/**
	 * Opens a new omegle session. Note: This CONNECTS THE SESSION!
	 * 
	 * @return The newly created session
	 * @throws OmegleException
	 *             If an error occurred while attempting to open, always due to
	 *             an IOException
	 */
	public OmegleSession openSession() throws OmegleException {
		return openSession(OmegleMode.NORMAL);
	}

	/**
	 * Opens a new omegle session. Note: This CONNECTS THE SESSION!
	 * 
	 * @param objs
	 *            Parameters are checked, so you may pass event listeners
	 *            through here.
	 * @return The newly created session
	 * @throws OmegleException
	 *             If an error occurred while attempting to open, always due to
	 *             an IOException
	 */
	public OmegleSession openSession(Object... objs) throws OmegleException {
		return openSession(OmegleMode.NORMAL, objs);
	}

	/**
	 * Opens a new omegle session. Note: This CONNECTS THE SESSION!
	 * 
	 * @param mode
	 *            The omegle chat mode
	 * @param objs
	 *            The objects. If mode = SPY_QUESTION, param 1 should be the
	 *            question. Parameters are checked, so you may pass event
	 *            listeners through here.
	 * 
	 * @return The newly created session
	 * @throws OmegleException
	 *             If an error occurred while attempting to open, always due to
	 *             an IOException
	 */
	public OmegleSession openSession(OmegleMode mode, Object... objs)
			throws OmegleException {
		try {
			Map<String, Object> vars = new HashMap<String, Object>();
			vars.put("rcs", "1");
			vars.put("firstevents", firstEvents ? "1" : "0");

			if (mode == OmegleMode.SPY) {
				vars.put("wantsspy", "1");
			} else if (mode == OmegleMode.SPY_QUESTION) {
				if (objs.length > 0) {
					if (objs[0] instanceof String) {
						vars.put("ask", objs[0].toString());
					} else {
						throw new OmegleException(
								"The question MUST be passed as the first parameter!");
					}
				} else {
					throw new OmegleException(
							"You cannot open a spy question session without a question!");
				}
			}

			URL url = new URL(OPEN_URL + "?" + HttpUtil.implode(vars));

			JSONObject resp = new JSONObject(HttpUtil.post(url, ""));

			if (!resp.has("clientID")) {
				throw new OmegleException("Omegle didn't return a client id!");
			}

			OmegleSession session = new OmegleSession(this,
					resp.getString("clientID"));

			for (Object obj : objs) {
				if (obj instanceof OmegleEventListener) {
					session.addListener((OmegleEventListener) obj);
				}
			}

			if (resp.has("events")) {
				session.parseEvents(resp.getJSONArray("events"));
			}

			synchronized (sessions) {
				sessions.add(session);
			}

			return session;
		} catch (IOException e) {
			throw new OmegleException(e);
		} catch (JSONException e) {
			throw new OmegleException(e);
		}
	}

	/**
	 * Adds a session to the system, can be used to listen in on sessions open
	 * in browsers, however the browsers won't get the messages this picks up.
	 * 
	 * @param session
	 *            The session.
	 */
	public void addSession(OmegleSession session) {
		synchronized (sessions) {
			sessions.add(session);
		}
	}

	/**
	 * Remove a session from the list
	 * 
	 * @param session
	 *            The session to remove
	 */
	public void removeSession(OmegleSession session) {
		synchronized (sessions) {
			sessions.remove(session);
		}
	}

	/**
	 * Set the first event flag
	 * 
	 * @param firstEvents
	 *            Whether to parse events from the first connect response
	 */
	public void setFirstEvents(boolean firstEvents) {
		this.firstEvents = firstEvents;
	}

	/**
	 * Set the event parse delay
	 * 
	 * @param eventParseDelay
	 *            The delay between parsing events, this will be called even if
	 *            previous events have not finished parsing!
	 */
	public void setEventParseDelay(long eventParseDelay) {
		this.eventParseDelay = eventParseDelay;
	}
}
