package net.sqf.view.utils;

import java.io.InputStream;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream is = java.lang.Class.class.getResourceAsStream("/icons/diagona_my.gif");
		System.out.println(is);
	}

}
