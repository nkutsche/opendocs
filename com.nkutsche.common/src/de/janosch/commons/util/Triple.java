package de.janosch.commons.util;

/**
 * 
 * @author Janos
 * @version 29.09.2011 | 23:50:33
 * 
 */
public class Triple <A, B, C> {

	public A a;
	public B b;
	public C c;

	public Triple() {
	}

	public Triple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	@Override
	public String toString() {
		return "Triple<a:" + a.toString() + ",b:" + b.toString() + ",c:" + c.toString() + ">;";
	}
}
