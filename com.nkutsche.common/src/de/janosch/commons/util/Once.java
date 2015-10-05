package de.janosch.commons.util;

/**
 * 
 * @author Janos
 * @version 07.11.2011 | 15:07:35
 * 
 */
public class Once {
	
	private boolean once = true;
	
	public boolean once() {
		final boolean old = once;
		once = false;
		return old;
	}

}
