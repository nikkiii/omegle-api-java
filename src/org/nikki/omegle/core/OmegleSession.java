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
package org.nikki.omegle.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.nikki.omegle.Omegle;
import org.nikki.omegle.event.MessageSendCallback;
import org.nikki.omegle.event.OmegleEventListener;
import org.nikki.omegle.util.HttpUtil;

/**
 * Represents an active Omegle session.
 * 
 * @author Nikki
 * 
 */
public class OmegleSession {

	/**
	 * The service used to send non-blocking messages, single threaded to ensure
	 * message order.
	 */
	private ExecutorService eventService = Executors.newSingleThreadExecutor();

	/**
	 * The parent class
	 */
	private Omegle omegle;

	/**
	 * The flag for whether this session is active, set to false when the
	 * session is closed/disconnected.
	 */
	private boolean active = true;

	/**
	 * The chat id
	 */
	private String id;

	/**
	 * The URL-Safe version of the chat id
	 */
	private String encodedId;

	/**
	 * The list of event listeners
	 */
	private List<OmegleEventListener> listeners = new LinkedList<OmegleEventListener>();

	/**
	 * Whether the session is seen as 'typing' or not
	 */
	private boolean typing = false;

	/**
	 * The amount of failed event checks, after 3 it will disconnect.
	 */
	private int failCount = 0;

	/**
	 * Constructs a new session
	 * 
	 * @param omegle
	 *            The parent handler
	 * @param id
	 *            The chat id
	 */
	public OmegleSession(Omegle omegle, String id) {
		this.omegle = omegle;
		this.id = id;

		try {
			this.encodedId = URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * An easy send function, can be used to send nonblocking messages without
	 * the callback, same as send(text, null)
	 * 
	 * @param text
	 *            The text to send
	 * @param blocking
	 *            True for blocking, false for non
	 * @throws OmegleException
	 *             If an error occurred (Only blocking, nonblocking won't report
	 *             anything)
	 */
	public void send(String text, boolean blocking) throws OmegleException {
		if (blocking) {
			send(text);
		} else {
			send(text, null);
		}
	}

	/**
	 * Non-blocking send
	 * 
	 * @param text
	 *            The text to send
	 * @param callback
	 *            The callback to use to inform that the message was sent, or
	 *            null if none.
	 */
	public void send(final String text, final MessageSendCallback callback) {
		eventService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					send(text);
					if (callback != null)
						callback.messageSent(OmegleSession.this, text);
				} catch (OmegleException e) {
					if (callback != null)
						callback.messageError(OmegleSession.this, e);
				}
			}
		});
	}

	/**
	 * Blocking send method, it's the backbone of the send functions.
	 * 
	 * @param text
	 *            The text to send
	 * @throws OmegleException
	 *             If an error occurred while attempting to send
	 */
	public void send(final String text) throws OmegleException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("msg", text);
		try {
			String resp = HttpUtil.post(Omegle.SEND_URL, map);
			if (!resp.equals("win")) {
				throw new OmegleException("Unable to send message, response: "
						+ resp);
			}
			fireEvent(OmegleEvent.messageSent, text);
		} catch (IOException e) {
			throw new OmegleException(e);
		}
	}

	/**
	 * Set the status to typing.
	 * 
	 * @throws OmegleException
	 *             If an error occurred while attempting to set the typing
	 *             status
	 */
	public void typing() throws OmegleException {
		try {
			String resp = HttpUtil.post(Omegle.TYPING_URL, "id=" + encodedId);
			if (!resp.equals("win")) {
				throw new OmegleException(
						"Unable to set state to typing, response: " + resp);
			}
			typing = !typing;
		} catch (IOException e) {
			throw new OmegleException(e);
		}
	}

	/**
	 * Called to check events, CAN be called by users, but the main thread will
	 * call it again when the next cycle is received.
	 */
	public void checkEvents() {
		try {
			String resp = HttpUtil.post(Omegle.EVENT_URL, "id=" + encodedId);

			if (resp == null || resp.equals("null")) {
				if (failCount++ >= 3) {
					fireEvent(OmegleEvent.disconnected, null);
					omegle.removeSession(this);
				}
				return;
			}

			parseEvents(new JSONArray(resp));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses events from a JSON Array
	 * @param events
	 * 			The array containing events
	 * @throws JSONException
	 * 			If an error occurred while reading the JSON values
	 */
	public void parseEvents(JSONArray events) throws JSONException {
		for (int i = 0; i < events.length(); i++) {
			JSONArray e = events.getJSONArray(i);

			try {
				OmegleEvent event = OmegleEvent.valueOf(e.getString(0));
				if(event != null) {
					fireEvent(event, e);
				}
			} catch(IllegalArgumentException ex) {
				// Ignore unknown events
			}
		}
	}

	/**
	 * Internal function used to call events. Note: Events are called on a
	 * service while parsed, it is best to not use any blocking operations
	 * (Sending chat messages, etc) in events, unless they are performed
	 * quickly.
	 * 
	 * @param event
	 *            The event name
	 * @param obj
	 *            The event data
	 */
	private void fireEvent(OmegleEvent event, Object obj) {
		try {
			//Called for EVERY event.
			JSONArray arrayObj = obj instanceof JSONArray ? (JSONArray) obj : new JSONArray(new Object[] {obj});
			for (OmegleEventListener listener : listeners) {
				listener.eventFired(this, event, arrayObj);
			}
			//Call the appropriate handler.
			switch (event) {
			case waiting:
				for (OmegleEventListener listener : listeners) {
					listener.chatWaiting(this);
				}
				break;
			case connected:
				for (OmegleEventListener listener : listeners) {
					listener.chatConnected(this);
				}
				break;
			case gotMessage:
				String message = ((JSONArray) obj).getString(1);
				for (OmegleEventListener listener : listeners) {
					listener.chatMessage(this, message);
				}
				break;
			case strangerDisconnected:
				for (OmegleEventListener listener : listeners) {
					listener.strangerDisconnected(this);
				}
				// Closed the session.
				active = false;
				omegle.removeSession(this);
				break;
			case typing:
				for (OmegleEventListener listener : listeners) {
					listener.strangerTyping(this);
				}
				break;
			case stoppedTyping:
				for (OmegleEventListener listener : listeners) {
					listener.strangerStoppedTyping(this);
				}
				break;
			case recaptchaRequired:
				Map<String, Object> requiredVals = HttpUtil.parseQueryString(((JSONArray) obj).getString(1));
				for (OmegleEventListener listener : listeners) {
					listener.recaptchaRequired(this, requiredVals);
				}
				break;
			case recaptchaRejected:
				Map<String, Object> rejectedVals = HttpUtil.parseQueryString(((JSONArray) obj).getString(1));
				for (OmegleEventListener listener : listeners) {
					listener.recaptchaRejected(this, rejectedVals);
				}
				break;
			case count:
				int count = ((JSONArray) obj).getInt(1);
				for (OmegleEventListener listener : listeners) {
					listener.count(this, count);
				}
				break;
			case spyMessage:
				JSONArray a = (JSONArray) obj;
				int strangerId = Integer.valueOf(a.getString(1).substring(9));
				OmegleSpyStranger stranger = OmegleSpyStranger.values()[strangerId-1];
				String spyMessage = a.getString(2);
				
				for (OmegleEventListener listener : listeners) {
					listener.spyMessage(this, stranger, spyMessage);
				}
				break;
			case spyTyping:
				OmegleSpyStranger typingStranger = OmegleSpyStranger.valueOf(((JSONArray) obj).getString(1).replace(" ", "_"));
				
				for (OmegleEventListener listener : listeners) {
					listener.spyTyping(this, typingStranger);
				}
				break;
			case spyStoppedTyping:
				OmegleSpyStranger sTypingStranger = OmegleSpyStranger.valueOf(((JSONArray) obj).getString(1).replace(" ", "_"));
				
				for (OmegleEventListener listener : listeners) {
					listener.spyStoppedTyping(this, sTypingStranger);
				}
				break;
			case spyDisconnected:
				OmegleSpyStranger disconnectedStranger = OmegleSpyStranger.valueOf(((JSONArray) obj).getString(1).replace(" ", "_"));
				
				for (OmegleEventListener listener : listeners) {
					listener.spyDisconnected(this, disconnectedStranger);
				}
				break;
			case question:
				String question = ((JSONArray) obj).getString(1);
				
				for(OmegleEventListener listener : listeners) {
					listener.question(this, question);
				}
				break;
			case error:
				String errorMessage = ((JSONArray) obj).getString(1);
				for (OmegleEventListener listener : listeners) {
					listener.omegleError(this, errorMessage);
				}
				break;
			case commonLikes:
				break;
			//Custom events
			case messageSent:
				for (OmegleEventListener listener : listeners) {
					listener.messageSent(this, obj.toString());
				}
				break;
			case disconnected:
				for (OmegleEventListener listener : listeners) {
					listener.chatDisconnected(this);
				}
				active = false;
				break;
			}
		} catch (Exception e) {
			// Nothing
			e.printStackTrace();
		}
	}

	/**
	 * Disconnect from the omegle chat
	 * 
	 * @throws OmegleException
	 *             If an error occurred while disconnecting
	 */
	public void disconnect() throws OmegleException {
		try {
			String resp = HttpUtil.post(Omegle.DISCONNECT_URL, "id="
					+ encodedId);
			if (!resp.equals("win")) {
				throw new OmegleException("Unable to disconnect, response: "
						+ resp);
			}
			fireEvent(OmegleEvent.disconnected, null);
			omegle.removeSession(this);
		} catch (IOException e) {
			throw new OmegleException(e);
		}
	}

	/**
	 * Add an OmegleEventListener to our list of listeners
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addListener(OmegleEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Gets whether this session is active.
	 * 
	 * @return True, if the session is active.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Get the chat id
	 * 
	 * @return The current chat id
	 */
	public String getId() {
		return id;
	}
}
