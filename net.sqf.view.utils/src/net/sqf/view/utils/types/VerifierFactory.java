package net.sqf.view.utils.types;

import java.awt.Container;
import java.util.HashMap;

import javax.swing.JFormattedTextField;

public class VerifierFactory {
	private static final String DEFAULT_TYPE = "xs:string";
	
	public static void addVerifier(String type, JFormattedTextField field, Container owner) {
		type = type.replaceFirst("[\\?\\*\\+]$", "");
		type = typeVerifierMap.containsKey(type) ? type : DEFAULT_TYPE;
		if(typeVerifierMap.containsKey(type)){
			field.setEditable(false);
			typeVerifierMap.get(type).getNewInstance().setVerifier(field, owner);
		}
	}

	@SuppressWarnings("unused")
	private static String[] types = new String[] { "float", "double",
			"boolean", "byte", "QName", "dateTime", "hexBinary",
			"base64Binary", "hexBinary",
			"unsignedByte", "time", "g", "anySimpleType", "duration",
			"NOTATION" };
	private static HashMap<String, _Verifier> typeVerifierMap = new HashMap<String, _Verifier>();
	static {
		typeVerifierMap.put(null, new StringVerifier());
		typeVerifierMap.put("xs:string", new StringVerifier());
		typeVerifierMap.put("xs:int", new IntegerVerifier("","+-"));
		typeVerifierMap.put("xs:integer", new IntegerVerifier("","+-"));
		typeVerifierMap.put("xs:short", new IntegerVerifier("","+-"));
		typeVerifierMap.put("xs:long", new IntegerVerifier("","+-"));
		typeVerifierMap.put("xs:decimal", new IntegerVerifier(".","+-"));
		typeVerifierMap.put("xs:unsignedInt", new IntegerVerifier("","+"));
		typeVerifierMap.put("xs:unsignedShort", new IntegerVerifier("","+"));
		typeVerifierMap.put("sqf:color", new ColorVerifier());
		typeVerifierMap.put("xs:date", new CalendarVerifier());
		typeVerifierMap.put("xs:dateTime", new CalendarTimeVerifier());
		typeVerifierMap.put("xs:time", new TimeVerifier());
	}
}
