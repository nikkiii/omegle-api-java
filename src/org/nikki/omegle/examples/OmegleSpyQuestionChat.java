package org.nikki.omegle.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.nikki.omegle.Omegle;
import org.nikki.omegle.core.OmegleException;
import org.nikki.omegle.core.OmegleMode;
import org.nikki.omegle.core.OmegleSession;
import org.nikki.omegle.core.OmegleSpyStranger;
import org.nikki.omegle.event.OmegleEventAdaptor;

/**
 * An example of a command line Omegle spy chat with the question asked by the user
 * 
 * @author Nikki
 * 
 */
public class OmegleSpyQuestionChat {

	public static void main(String[] args) {
		Omegle omegle = new Omegle();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			String question = null;
			while(question == null) {
				System.out.print("Question: ");
				question = reader.readLine();
				if(question.trim().length() == 0) {
					question = null;
				}
			}
			
			final String fQuestion = question;
			
			System.out.println("Opening session...");

			OmegleSession session = omegle.openSession(OmegleMode.SPY_QUESTION, question, new OmegleEventAdaptor() {
				@Override
				public void chatWaiting(OmegleSession session) {
					System.out.println("Waiting for chat...");
				}

				@Override
				public void chatConnected(OmegleSession session) {
					System.out
							.println("You are now watching two strangers talk about \""+fQuestion+"\"!");
				}

				@Override
				public void spyMessage(OmegleSession session, OmegleSpyStranger stranger, String message) {
					System.out.println(stranger + ": " + message);
				}

				@Override
				public void spyDisconnected(OmegleSession session, OmegleSpyStranger stranger) {
					System.out.println("Stranger "+stranger+" disconnected, goodbye!");
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

			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				if (line.equals("quit")) {
					session.disconnect();
				}
			}
		} catch (OmegleException | IOException e) {
			e.printStackTrace();
		}
	}
}
