package net.sqf.xmlUtils.staxParser;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.Location;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class AttributeInfo extends NodeInfo {

	
	private final Location start;
	private final Location end;
	
	protected AttributeInfo(Attr node) {
		super(node);
		ElementInfo parentInfo = PositionalXMLReader.getNodeInfo(node.getOwnerElement());
		String attributeRegion = parentInfo.getAttributRegion();
		Location[] startEnd = getAttributStartEnd(attributeRegion, parentInfo.getAttributRegionStart(), node);
		start = startEnd[0];
		end = startEnd[1];
	}
	public Node getNode() {
		return node;
	}

	public Location getStart() {
		
		return start;
	}
	
	public int getStartOffset(){
		return start.getCharacterOffset();
	}

	public Location getEnd() {
		return end;
	}
	
	public int getEndOffset(){
		return end.getCharacterOffset();
	}

	

	private final Pattern attrPattern = Pattern
			.compile("\\s+(\\S+?)(\\s+)?=(\\s+)?(\"([^\"]*)\"|'([^']*)')");
	private Location[] getAttributStartEnd(String attributRegion, Location locStart, Node attr) {
		Location[] startEnd = new Location[2];
		Matcher matcher = attrPattern.matcher(attributRegion);
//		debug-error! can not be debugged...
		while (matcher.find()) {
			String name = matcher.group(1);
			if (name.equals(attr.getNodeName())) {

				MatchResult mresult = matcher.toMatchResult();
				int startPos = mresult.start();
				int endPos = mresult.end();
				startEnd[0] = NodeInfo.newLocation(locStart, startPos);
				startEnd[1] = NodeInfo.newLocation(locStart, endPos);
				break;
			}
		}
		return startEnd;
	}
	
}
