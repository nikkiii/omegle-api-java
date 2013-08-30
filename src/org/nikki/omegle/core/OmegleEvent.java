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
	disconnected, messageSent,
	
	statusInfo,identDigests
}
