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
package org.nikki.omegle.event;

import java.util.Map;

import org.json.JSONArray;
import org.nikki.omegle.core.OmegleEvent;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.core.OmegleSpyStranger;

/**
 * The main event listener.
 * 
 * @author Nikki
 * 
 */
public interface OmegleEventListener {

	/**
	 * Generic event method, called for every event.
	 * 
	 * @param session
	 *            The session which the event was fired for
	 * @param event
	 *            The event type.
	 * @param array
	 *            The event variables (If it was a plain string, it'll be
	 *            converted to array)
	 */
	public void eventFired(OmegleSession session, OmegleEvent event,
			JSONArray array);

	/**
	 * Called when a chat is sent the 'waiting' event
	 * 
	 * @param session
	 *            The session which was changed to 'waiting'
	 */
	public void chatWaiting(OmegleSession session);

	/**
	 * Called when a chat is connected to a stranger
	 * 
	 * @param session
	 *            The session that was connected
	 */
	public void chatConnected(OmegleSession session);

	/**
	 * Called when a chat receives a message
	 * 
	 * @param session
	 *            The session that received it
	 * @param message
	 *            The message received
	 */
	public void chatMessage(OmegleSession session, String message);

	/**
	 * Called when the stranger disconnects (This is also when the OmegleSession
	 * object is 'closed')
	 * 
	 * @param session
	 *            The session the stranger disconnected from
	 */
	public void strangerDisconnected(OmegleSession session);

	/**
	 * Called when we receive the 'typing' event
	 * 
	 * @param session
	 *            The session that triggered the event
	 */
	public void strangerTyping(OmegleSession session);

	/**
	 * Called when we receive the 'stopped typing' event, not called on
	 * chatMessage.
	 * 
	 * @param session
	 *            The session that triggered the event
	 */
	public void strangerStoppedTyping(OmegleSession session);

	/**
	 * Called when we receive the 'recaptchaRequired' event, prompting for a
	 * captcha
	 * 
	 * @param session
	 *            The session that is required to provide a captcha
	 * @param variables
	 *            The map of variables sent
	 */
	public void recaptchaRequired(OmegleSession session,
			Map<String, Object> variables);

	/**
	 * Called when we receive the 'recaptchaRejected' event, when a recaptcha
	 * submission failed
	 * 
	 * @param session
	 *            The session that is required to provide a captcha
	 * @param variables
	 *            The map of variables sent
	 */
	public void recaptchaRejected(OmegleSession session,
			Map<String, Object> variables);

	/**
	 * Called when we receive the 'count' event, which shows how many users are
	 * online.
	 * 
	 * @param session
	 *            The session that received the count
	 * @param count
	 *            The online count
	 */
	public void count(OmegleSession session, int count);

	/**
	 * Called when a stranger in a spy session sends a message
	 * 
	 * @param session
	 *            The session that contains the stranger
	 * @param stranger
	 *            The stranger that sent the message (Stranger 1, Stranger 2)
	 * @param message
	 *            The message sent
	 */
	public void spyMessage(OmegleSession session, OmegleSpyStranger stranger,
			String message);

	/**
	 * Called when a stranger in a spy session types
	 * 
	 * @param session
	 *            The session that contains the stranger typing
	 * @param stranger
	 *            The stranger that started typing (Stranger 1, Stranger 2)
	 */
	public void spyTyping(OmegleSession session, OmegleSpyStranger stranger);

	/**
	 * Called when a stranger in a spy session stops typing
	 * 
	 * @param session
	 *            The session that contains the stranger
	 * @param stranger
	 *            The stranger that stopped typing (Stranger 1, Stranger 2)
	 */
	public void spyStoppedTyping(OmegleSession session,
			OmegleSpyStranger stranger);

	/**
	 * Called when a stranger disconnects from a spy session
	 * 
	 * @param session
	 *            The session that the stranger disconnected from
	 * @param stranger
	 *            The stranger that disconnected (Stranger 1, Stranger 2)
	 */
	public void spyDisconnected(OmegleSession session,
			OmegleSpyStranger stranger);

	/**
	 * Called when a session receives a question
	 * 
	 * @param session
	 *            The session that received the question
	 * @param question
	 *            The question asked
	 */
	public void question(OmegleSession session, String question);

	/**
	 * Called when the 'error' event is received, usually because the user is
	 * banned.
	 * 
	 * @param session
	 *            The session that triggered the event
	 * @param string
	 *            The error message
	 */
	public void omegleError(OmegleSession session, String string);

	/**
	 * Called when a message was sent successfully
	 * 
	 * @param session
	 *            The session that sent the message
	 * @param string
	 *            The message that was sent
	 */
	public void messageSent(OmegleSession session, String string);

	/**
	 * Called when a chat is disconnected by the user, this should only be used
	 * to verify the session disconnected.
	 * 
	 * @param session
	 *            The session that triggered the event
	 */
	public void chatDisconnected(OmegleSession session);
}
