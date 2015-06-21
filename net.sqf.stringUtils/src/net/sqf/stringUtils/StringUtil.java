package net.sqf.stringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	private static Pattern pattern = Pattern.compile("(\\r\\n?|\\r?\\n)");
	private static ArrayList<String> getLines(String document){
		Matcher m = pattern.matcher(document);
		ArrayList<String> lines = new ArrayList<String>();
		while (m.find()) {
			int lastPosFirstLine = m.end();
			String firstLine = document.substring(0, lastPosFirstLine);
			document = document.substring(lastPosFirstLine);
			m = pattern.matcher(document);
			lines.add(firstLine);
		}
		if(!document.equals("")){
			lines.add(document);
		}
		return lines;
	}
	public static String[] getLinesArr(String document){
		ArrayList<String> lineList = getLines(document);
		String[] arr = lineList.toArray(new String[lineList.size()]);
		return arr;
	}
	
	public static String escapeRegex(String regex){
		return Pattern.quote(regex);
	}
	
	
	public static final boolean matches(final String text, final String regex){
		if (text == null || regex == null) {
			return false;
		}
		final Pattern p = Pattern.compile(regex);
		final Matcher m = p.matcher(text);
		return m.find();
	}
	
	public static void main(String[] args) {
//		String[] lines = StringUtil.getLinesArr("<root>\n<a>hier kommt ein Umbruch\r\n</a>\r</root>");
		
		System.out.println(escapeRegex("[]"));
//		System.out.print(lines[0]);
//		System.out.print(lines[1]);
//		System.out.print(lines[2]);
//		System.out.print(lines[3]);
	}
}
