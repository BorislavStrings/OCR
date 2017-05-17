package Morphologic;

import java.io.Serializable;

public class DictionaryMorphologic implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count = 1;
	private String type;
	
	public DictionaryMorphologic(String type, int count) {
		this.count = count;
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void increase() {
		this.count++;
	}
	
	public int getCount() {
		return this.count;
	}	
	
}
