package Morphologic;

import java.io.Serializable;

public class DictionaryWord implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count = 1;
	private Word word;
	
	public DictionaryWord(Word word, int count) {
		this.count = count;
		this.word = word;
	}
	
	public void increase() {
		this.count++;
	}
	
	public void setWord( Word word ) {
		this.word = word;
	}
	
	public String getWord() {
		return this.word.word;
	}
	
	public String getMorphType() {
		return this.word.type;
	}
	
	public int getCount() {
		return this.count;
	}	
	
}
