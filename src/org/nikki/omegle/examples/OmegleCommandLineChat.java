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
 * An example of a command line Omegle chat
 * 
 * @author Nikki
 * 
 */
public class OmegleCommandLineChat {

	public static void main(String[] args) {
		Omegle omegle = new Omegle();
		try {
			System.out.println("Opening session...");
			
			OmegleSession session = omegle.openSession(OmegleMode.NORMAL, new OmegleEventAdaptor() {
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
