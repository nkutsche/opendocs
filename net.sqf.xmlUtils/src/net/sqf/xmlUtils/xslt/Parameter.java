package net.sqf.xmlUtils.xslt;

public class Parameter {
	private String name;
	private Object value;
	private String namespace;
	private int step = 0;
	
	public Parameter(String name, Object value){
		this.name = name;
		this.value = value;
	}

	public Parameter(String name, int step, Object value){
		this(name, value);
		this.step = step;
	}
	public Parameter(String name, String namespace, Object value){
		this(name, value);
		this.namespace = namespace;
	}
	public Parameter(String name, String namespace, int step, Object value){
		this(name, namespace, value);
		this.step = step;
	}
	
	
	
	public String getQName(){
		if(namespace == null || namespace.equals("")){
			return this.name;
		} else {
			return "{" + namespace + "}" + name;
		}
	}
	
	public Object getValue(){
		return this.value;
	}
	public int getStep(){
		return this.step;
	}
}
