package BigramIndex;

import java.util.ArrayList;
import java.util.HashMap;

public class Biword {
	// n grams length = n - 1
	private int biword_length = 2;
	private String end_symbol = "$";
	private HashMap<String, ArrayList<Word>> biwords;
	
	public Biword() {
		this.biwords = new HashMap();
	}
	
	public ArrayList<String> extractBiWords( String word ) {
		ArrayList<String> bi_words = new ArrayList();
		
		if ( word != null && ! word.isEmpty() ) {
			word = (String)(this.end_symbol + word + this.end_symbol);
			int end_index = 0, start_index = 0;
			
			for ( int i = 1; i < word.length(); i++ ) {
				start_index = i - 1;
				end_index = start_index + this.biword_length;
				bi_words.add( word.substring( start_index, end_index ) );
			}
		}
		
		return bi_words;
	}
}
