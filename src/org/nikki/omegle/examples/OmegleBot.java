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
