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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.nikki.omegle.Omegle;
import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleMode;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.event.OmegleEventAdaptor;

/**
 * An example of a command line Omegle spy chat
 * 
 * @author Nikki
 * 
 */
public class OmegleSpyChat {

	public static void main(String[] args) {
		Omegle omegle = new Omegle();
		try {
			System.out.println("Opening session...");

			OmegleSession session = omegle.openSession(OmegleMode.SPY, new OmegleEventAdaptor() {
				@Override
				public void chatWaiting(OmegleSession session) {
					System.out.println("Waiting for chat...");
				}

				@Override
				public void chatConnected(OmegleSession session) {
					System.out
							.println("You are now talking to a random stranger!");
				}

				@Override
				public void chatMessage(OmegleSession session, String message) {
					System.out.println("Stranger: " + message);
				}

				@Override
				public void messageSent(OmegleSession session, String string) {
					System.out.println("You: " + string);
				}

				@Override
				public void strangerDisconnected(OmegleSession session) {
					System.out.println("Stranger disconnected, goodbye!");
					System.exit(0);
				}
				
				@Override
				public void question(OmegleSession session, String question) {
					System.out.println("Question: "+question);
				}

				@Override
				public void omegleError(OmegleSession session, String string) {
					System.out.println("ERROR! " + string);
					System.exit(1);
				}
			});

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("quit")) {
					session.disconnect();
				} else {
					// Send the message in non-blocking mode
					session.send(line, true);
				}
			}
		} catch (OmegleException | IOException e) {
			e.printStackTrace();
		}
	}
}
