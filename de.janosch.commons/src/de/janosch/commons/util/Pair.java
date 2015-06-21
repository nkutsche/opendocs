package de.janosch.commons.util;

/**
 * 
 * @author Janos
 * @version 29.09.2011 | 22:45:40
 * 
 */
public class Pair<A, B> {

	public A a;
	public B b;

	public Pair() {
	}

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		return "Pair<a:" + a.toString() + ",b:" + b.toString() + ">;";
	}
	
}
