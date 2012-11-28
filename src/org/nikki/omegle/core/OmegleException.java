package org.nikki.omegle.core;

/**
 * A generic exception used for expressing problems caused internally
 * 
 * @author Nikki
 * 
 */
public class OmegleException extends Exception {

	private static final long serialVersionUID = 5845898517299356613L;

	public OmegleException(Throwable e) {
		super(e);
	}

	public OmegleException(String s) {
		super(s);
	}
}
