package BigramIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SpellChecker {
	
	private Index index;
	public int max_distance = Integer.MIN_VALUE;
	
	public SpellChecker( Index index ) {
		this.index = index;
	}
	
	public ArrayList<DistanceWords> correct( String word ) {
		ArrayList<Word> words = this.index.search( word );
		ArrayList<DistanceWords> result_distance = new ArrayList<DistanceWords>();
		ArrayList<String> result = new ArrayList<String>();
		
		for ( Word current : words ) {
			int distance = this.distance( word, current.getWord() );
			max_distance = Math.max( distance, max_distance );
			result_distance.add( new DistanceWords( current.getWord(), distance ) );
		}
		
		Collections.sort( result_distance, new DistanceComparator() );
		
		for ( DistanceWords w : result_distance ) {
			result.add( w.word );
		}
		
		return result_distance;
	}
	
	public ArrayList<String> correctSlow( String word ) {
		ArrayList<String> words = this.index.getVocabulary();
		ArrayList<DistanceWords> result_distance = new ArrayList<DistanceWords>();
		ArrayList<String> result = new ArrayList<String>();
		
		for ( String current : words ) {
			result_distance.add( new DistanceWords( current, this.distance( word, current ) ) );
		}
		
		Collections.sort( result_distance, new DistanceComparator() );
		
		for ( DistanceWords w : result_distance ) {
			result.add( w.word );
		}
		
		return result;
	}
	
	public static int distance( String a, String b ) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        
        int[] costs = new int [ b.length() + 1 ];
        for ( int j = 0; j < costs.length; j++ ) costs[j] = j;
        
        for ( int i = 1; i <= a.length(); i++ ) {
            costs[0] = i;
            int nw = i - 1;
            for ( int j = 1; j <= b.length(); j++ ) {
                int cj = Math.min( 1 + Math.min( costs[j], costs[j - 1] ), a.charAt( i - 1 ) == b.charAt( j - 1 ) ? nw : nw + 1 );
                nw = costs[j];
                costs[j] = cj;
            }
        }
        
        return costs[ b.length() ];
    }
	
}