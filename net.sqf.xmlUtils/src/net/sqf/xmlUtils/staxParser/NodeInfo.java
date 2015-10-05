package net.sqf.xmlUtils.staxParser;

import javax.xml.stream.Location;

import org.w3c.dom.Node;

public class NodeInfo {
	protected final Node node;
	private final Location start;
	private final Location end;
	
	
	protected NodeInfo(Node node){
		this.node = node;
		this.start = PositionalXMLReader.getLocation(node, PositionalXMLReader.NODE_LOCATION_START);
		this.end = PositionalXMLReader.getLocation(node, PositionalXMLReader.NODE_LOCATION_END);
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
	
	public int getLength(){
		return this.getEndOffset() - this.getStartOffset();
	}

	public Location getMarkStartLocation(){
		return this.getStart();
	}
	public Location getMarkEndLocation(){
		return this.getEnd();
	}
	public int getMarkStart(){
		return this.getStartOffset();
	}
	public int getMarkEnd(){
		return this.getEndOffset();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.node.toString() + ": [start:" + this.getStartOffset() +"; end:" + this.getEndOffset() + "]";
	}
	
	protected static Location newLocation(final Location loc, int addPositon) {
		final int correctLineNumber = loc.getLineNumber();
		final int correctColumnNumber = loc.getColumnNumber();
		final int correctPosition = loc.getCharacterOffset() + addPositon;
		return new Location() {

			@Override
			public String getSystemId() {
				// TODO Auto-generated method stub
				return loc.getSystemId();
			}

			@Override
			public String getPublicId() {
				// TODO Auto-generated method stub
				return loc.getPublicId();
			}

			@Override
			public int getLineNumber() {
				// TODO Auto-generated method stub
				return correctLineNumber;
			}

			@Override
			public int getColumnNumber() {
				// TODO Auto-generated method stub
				return correctColumnNumber;
			}

			@Override
			public int getCharacterOffset() {
				// TODO Auto-generated method stub
				return correctPosition;
			}
		};
	}
}
