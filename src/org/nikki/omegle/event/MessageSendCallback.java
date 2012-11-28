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
