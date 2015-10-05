package net.sqf.utils.process.exceptions;




public class SQFException extends Exception {
	private static final long serialVersionUID = 1L;
	private String title = "Unkwon error!";
	private int type = JUST_OK;
	private final int level;

	
	public static final int OK_AND_CANCEL = 0;
	public static final int JUST_CANCEL = 1;
	public static final int JUST_OK = 2;
	
	public SQFException(String msg, String title){
		this(msg, title, JUST_OK);
	}
	
	public SQFException(String msg, String title, int type){
		this(msg, title, type, Levels.LEVEL_ERROR);
	}
	
	public SQFException(String msg, String title, int type, int level){
		super(msg);
		this.title = title;
		this.type = type;
		this.level = level;
	}

//	public void error(final _GUI gui) throws CancelException {
//		JFrame guiFrame = (JFrame) gui;
//		ExceptionViewer viewer = new ExceptionViewer(guiFrame);
//		viewer.error(this);
//	}
	
	public String getTitle(){
		return this.title;
	};
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return this.type;
	}
	public int getLevel(){
		return this.level;
	}
}
