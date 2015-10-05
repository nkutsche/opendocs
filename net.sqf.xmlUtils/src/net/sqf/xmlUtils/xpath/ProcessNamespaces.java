package net.sqf.xmlUtils.xpath;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class ProcessNamespaces {

	public static final String SQF_NS = "http://www.schematron-quickfix.com/validator/process";
	public static final String ES_NS = "http://www.escali.schematron-quickfix.com/";
	public static final String SVRL_NS = "http://purl.oclc.org/dsdl/svrl";
	public static final String SCH_NS = "http://purl.oclc.org/dsdl/schematron";
	public static final String XML_NS = "http://www.w3.org/XML/1998/namespace";
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema";
	public static final String XSM_NS = "http://www.schematron-quickfix.com/manipulator/process";
	
	public static final NamespaceContext NAMESPACES = new NamespaceContext() {
		public String getNamespaceURI(String prefix) {
			String uri;
			if (prefix.equals("svrl"))
				uri = SVRL_NS;
			else if (prefix.equals("sqf"))
				uri = SQF_NS;
			else if (prefix.equals("es"))
				uri = ES_NS;
			else if (prefix.equals("xsm"))
				uri = XSM_NS;
			else if (prefix.equals("sch"))
				uri = SCH_NS;
			else if (prefix.equals("xs"))
				uri = XSD_NS;
			else if (prefix.equals("xml"))
				uri = XML_NS;
			else
				uri = null;
			return uri;
		}

		// Dummy implementation - not used!
		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String val) {
			return null;
		}

		// Dummy implemenation - not used!
		public String getPrefix(String uri) {
			return null;
		}
	};
}
