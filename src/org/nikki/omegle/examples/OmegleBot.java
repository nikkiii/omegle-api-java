package org.nikki.omegle.examples;

import org.nikki.omegle.Omegle;
import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.event.OmegleEventAdaptor;

/**
 * A simple bot that will say a fixed set of messages.
 * 
 * @author Nikki
 * 
 */
public class OmegleBot {

	public static void main(String[] args) {
		String[] messages = { "Hello", "How are you?", "Gotta go!" };

		if (args.length > 0) {
			messages = args;
		}

		Omegle omegle = new Omegle();
		try {
			final String[] fMessages = messages;

			OmegleSession session = omegle.openSession();
			session.addListener(new OmegleEventAdaptor() {
				private int msgIdx = 0;

				@Override
				public void chatMessage(OmegleSession session, String message) {
					try {
						session.send(fMessages[msgIdx++], false);
						if (msgIdx < fMessages.length) {
							session.disconnect();
						}
					} catch (OmegleException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (OmegleException e) {
			e.printStackTrace();
		}
	}
}
