package org.nikki.omegle.event;

import java.util.Map;

import org.json.JSONArray;
import org.nikki.omegle.core.OmegleEvent;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.core.OmegleSpyStranger;

/**
 * An Event Adaptor that doesn't require all methods to be implemented.
 * 
 * @see org.nikki.event.OmegleEventListener
 * @author Nikki
 * 
 */
public class OmegleEventAdaptor implements OmegleEventListener {

	@Override
	public void eventFired(OmegleSession session, OmegleEvent event,
			JSONArray array) {
		// Nothing.
	}

	@Override
	public void chatWaiting(OmegleSession session) {
		// Nothing.
	}

	@Override
	public void chatConnected(OmegleSession session) {
		// Nothing.
	}

	@Override
	public void chatMessage(OmegleSession session, String message) {
		// Nothing.
	}

	@Override
	public void strangerDisconnected(OmegleSession session) {
		// Nothing.
	}

	@Override
	public void strangerTyping(OmegleSession session) {
		// Nothing.
	}

	@Override
	public void strangerStoppedTyping(OmegleSession session) {
		// Nothing.
	}

	@Override
	public void recaptchaRequired(OmegleSession session,
			Map<String, Object> variables) {
		// Nothing.
	}

	@Override
	public void recaptchaRejected(OmegleSession session,
			Map<String, Object> variables) {
		// Nothing.
	}

	@Override
	public void count(OmegleSession session, int count) {
		// Nothing.
	}

	@Override
	public void spyMessage(OmegleSession session, OmegleSpyStranger stranger,
			String message) {
		// Nothing.
	}

	@Override
	public void spyTyping(OmegleSession session, OmegleSpyStranger stranger) {
		// Nothing.
	}

	@Override
	public void spyStoppedTyping(OmegleSession session,
			OmegleSpyStranger stranger) {
		// Nothing.
	}

	@Override
	public void spyDisconnected(OmegleSession session,
			OmegleSpyStranger stranger) {
		// Nothing.
	}

	@Override
	public void question(OmegleSession session, String question) {
		// Nothing.
	}

	@Override
	public void omegleError(OmegleSession session, String string) {
		// Nothing.
	}

	@Override
	public void messageSent(OmegleSession session, String string) {
		// Nothing.
	}

	@Override
	public void chatDisconnected(OmegleSession session) {
		// Nothing.
	}

}
