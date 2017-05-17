package Morphologic;

import java.io.Serializable;

public class Word implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String word;
	public String type;
	
	public Word(String word, String type) {
		this.word = word;
		this.type = type;
	}
}
