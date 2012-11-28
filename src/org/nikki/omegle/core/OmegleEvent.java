package org.nikki.omegle.core;

/**
 * Represents valid omegle events.
 * 
 * @author Nikki
 *
 */
public enum OmegleEvent {
	//Omegle events
	waiting, connected, gotMessage, strangerDisconnected, typing, stoppedTyping, recaptchaRequired, recaptchaRejected, count, 
	
	//Spy mode events
	spyMessage, spyTyping, spyStoppedTyping, spyDisconnected, question, error, commonLikes, 
	
	//Misc events
	antinudeBanned,
	
	// Internal events
	disconnected, messageSent
}
