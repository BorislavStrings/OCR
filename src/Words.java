import java.awt.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.opencv.core.Mat;


public class Words {
	
	private ArrayList<Word> words;
	private ArrayList<Letter> letters;
	private HashMap<Letter, ArrayList<Letter>> lines;
	private double letter_distance = 0; 
	private final double letter_distance_threshold = 20;
	
	public Words( ArrayList<Letter> letters ) {
		this.words = new ArrayList();
		this.letters = letters;
	}	
	public ArrayList<Word> getWords() {
		// compute line words
		this.getLines();
		this.computeLetterDistance();
		this.extractWords();
		
		return this.words;
	}
	
	private HashMap<Letter, ArrayList<Letter>> getLines() {
		ArrayList<Letter> letters = this.letters;
		HashMap<Letter, ArrayList<Letter>> lines = new HashMap();
		
		for (Letter current : letters ) {
			boolean included = false;
			if ( ! lines.isEmpty() ) {
				for ( Letter ln : lines.keySet() ) {
					double current_middle = (double)( 2 * current.getY() + current.getLetter().rows() ) / 2;
					
					/*
					 * ( ln.getY() <= current.getY() && ( ln.getY() + ln.getLetter().rows() ) >= current.getY() )
							||
							( ( ln.getY() + ln.getLetter().rows() ) >= ( current.getY() + current.getLetter().rows() ) && ln.getY() >= ( current.getY() + current.getLetter().rows() ) )
					 */
					
					if ( ln.getY() <= current_middle && ( ln.getY() + ln.getLetter().rows() ) >= current_middle ) {
						//insert it in the array list/line
						ArrayList<Letter> line = lines.get( ln );
						line.add( current );
						Collections.sort( line, new LetterComparator() );
						lines.put( ln, line );
						included = true;
					}
				}
				
			}
			
			if ( ! included ) {
				ArrayList<Letter> line = new ArrayList();
				line.add( current );
				lines.put( current, line );
			}
			
		}
		
		this.lines = lines;
		
		return lines;
	}
	
	private void addWord( Word word ) {
		this.words.add( word );
	}
	
	private ArrayList<Word> extractWords() {
		Letter prev = null;
		boolean new_word = true;
		Word word = null;
		
		
		//sort rows 
		ArrayList <Letter>keys = new ArrayList<Letter>( this.lines.keySet() );
		Collections.sort( keys, new LineComparator() );
		
		for ( Letter let_inx : keys ) {
			ArrayList<Letter> letters = this.lines.get( let_inx );
			for ( Letter current : letters ) {
				if ( prev != null && ! prev.isClose( current, this.letter_distance, this.letter_distance_threshold ) ) {
					new_word = true;
				}
				
				if ( new_word ) {
					if ( word != null ) {
						this.words.add( word );
					}
					word = new Word();
					new_word = false;
				}
				
				word.insertLetter( current );
				prev = current;
			}
		}
		
		return this.words;
	}
	
	private double computeLetterDistance() {
		// find mean
		double min = Double.MAX_VALUE;
		Letter prev = null;
		
		for ( Letter let_inx : this.lines.keySet() ) {
			ArrayList<Letter> letters = this.lines.get( let_inx );
			for ( Letter current : letters ) {
				if ( prev != null ) {
					//System.out.println( Math.abs( prev.getX() + prev.getLetter().cols() - current.getX() ) );
					min = Math.min( Math.abs( prev.getX() + prev.getLetter().cols() - current.getX() ), min );
				}
				
				prev = current;
			}
		}
		
		this.letter_distance = (double) min;
		
		return this.letter_distance;
	}
	
}