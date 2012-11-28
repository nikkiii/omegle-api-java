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

import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleSession;

/**
 * A callback used for nonblocking sending of messages. Both methods are empty
 * by default in case the user wants either the success or failure callbacks.
 * 
 * @author Nikki
 * 
 */
public abstract class MessageSendCallback {

	/**
	 * Called when a message was sent.
	 * 
	 * @param session
	 *            The session that sent the message
	 * @param message
	 *            The message that was sent
	 */
	public void messageSent(OmegleSession session, String message) {
		// Nothing by default.
	}

	/**
	 * Called when a message sending experienced an error
	 * 
	 * @param session
	 *            The session that attempted to send the message
	 * @param exception
	 *            The exception which caused it, could be an IOException behind
	 *            it, or a string.
	 */
	public void messageError(OmegleSession session, OmegleException exception) {
		// Nothing by default.
	}
}
