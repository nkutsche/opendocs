package net.sqf.xmlUtils.staxParser;

import javax.xml.stream.Location;

import org.w3c.dom.Element;

public class ElementInfo extends NodeInfo {

	private Location innerStart;
	private Location innerEnd;
	private Location attrRegStart;
	private Location attrRegEnd;
	private String attributRegion;

	protected ElementInfo(Element node) {
		super(node);
		attributRegion = (String) node.getUserData(PositionalXMLReader.ATTRIBUT_REGION);
		if(attributRegion == null)
			attributRegion = "";
		innerStart = PositionalXMLReader.getLocation(node,
				PositionalXMLReader.NODE_INNER_LOCATION_START);
		innerEnd = PositionalXMLReader.getLocation(node,
				PositionalXMLReader.NODE_INNER_LOCATION_END);

		attrRegStart = PositionalXMLReader.getLocation(node, PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START);
		attrRegEnd = PositionalXMLReader.getLocation(node, PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END);
	}

	public Location getInnerStart() {
		return innerStart;
	}

	public int getInnerStartOffset() {
		return innerStart.getCharacterOffset();
	}

	public Location getInnerEnd() {
		return innerEnd;
	}

	public int getInnerEndOffset() {
		return innerEnd.getCharacterOffset();
	}

	public Location getAttributRegionStart() {
		return attrRegStart;
	}

	public int getAttributRegionStartOffset() {
		return attrRegStart.getCharacterOffset();
	}

	public Location getAttributRegionEnd() {
		return attrRegEnd;
	}

	public int getAttributRegionEndOffset() {
		return attrRegEnd.getCharacterOffset();
	}

	public Location getLocation(String key) {
		return PositionalXMLReader.getLocation(this.getNode(), key);
	}

	public int getLocationOffset(String key) {
		return PositionalXMLReader.getLocation(this.getNode(), key)
				.getCharacterOffset();
	}

	public Location getMarkEndLocation(){
		return this.getInnerStart();
	}
	public int getMarkEnd() {
		return this.getInnerStartOffset();
	}
	public String getAttributRegion(){
		return this.attributRegion;
	}

//	private Location[] getAttributRegionStartEnd(Element el) {
//		Location[] startEnd = new Location[2];
//		
//		// if(el.hasAttributes()){
//
//		Pattern patternStart = Pattern.compile("\\s");
//		Matcher matcherStart = patternStart.matcher(attributRegion);
//
//		Pattern patternEnd = Pattern.compile("(/)?>$", Pattern.DOTALL);
//		Matcher matcherEnd = patternEnd.matcher(attributRegion);
//
//		int addEndPos = matcherEnd.find() ? matcherEnd.start() : attributRegion
//				.length() - 1;
//		int addStartPos = matcherStart.find() ? matcherStart.start()
//				: addEndPos;
//		
//		startEnd[0] = NodeInfo.newLocation(this.getStart(), addStartPos);
//		startEnd[1] = NodeInfo.newLocation(this.getStart(), addEndPos);
//		
//		return startEnd;
//		// } else {
//		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_START,
//		// newLocation(range.end, -1), null);
//		// el.setUserData(PositionalXMLReader.ATTRIBUTE_REGION_LOCATION_END,
//		// newLocation(range.end, -1), null);
//		// }
//
//	}
}
