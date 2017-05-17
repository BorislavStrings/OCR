package BigramIndex;

import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Index {
	
	private ArrayList<Word> index_words;
	private HashMap<String, ArrayList<Word>> index_biwords;
	
	private ArrayList<String> vocabulary;
	private String file;
	private final Biword biword;
	
	public Index( String file ) throws IOException {
		this.file = file;
		this.vocabulary = new ArrayList();
		this.biword = new Biword();
		this.index_words = new ArrayList();
		this.index_biwords = new HashMap();
		
		try {
			this.extractFileData();
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Index(ArrayList<String> vocabulary) {
		this.vocabulary = vocabulary;
		this.biword = new Biword();
		this.index_words = new ArrayList();
		this.index_biwords = new HashMap();		
	}
	
	public ArrayList<String> getVocabulary() {
		return this.vocabulary;
	}
	
	public void createIndex() {
		if ( this.vocabulary != null && ! this.vocabulary.isEmpty() ) {
			for ( String word : this.vocabulary ) {
				ArrayList biwords = biword.extractBiWords( word );
				
				Word word_obj = null;
				boolean exists = false;
				
				// there is not a connection between biwords and vocabulary index , check it!
				
				for ( Word w :  this.index_words ) {
					if ( w.getWord() == word ) {
						word_obj = w;
						exists = true;
						break;
					}
				}				
				
				if ( ! exists ) {
					word_obj = new Word( word );
					this.index_words.add( word_obj );
				}
				
				this.insertBiword( biwords, word_obj );
				//insert them in the words array and in the biwords array with the proper links
			}
		}
	}
	
	public ArrayList<Word> search( String word ) {
		ArrayList<Word> found_words = new ArrayList<Word>();
		
		if ( word != null ) {
			ArrayList<String> biwords = biword.extractBiWords( word );
			for ( String bi : biwords ) {
				ArrayList<Word> current_bi_words = this.getBiWords( bi );
				if ( ! current_bi_words.isEmpty() ) {
					for ( Word bi_word : current_bi_words ) {
						if ( ! found_words.contains( bi_word ) ) {
							found_words.add( bi_word );
						}
					}
				}
			}
		}
		
		return found_words;
	}
	
	private ArrayList<Word> getBiWords( String bi ) {
		ArrayList<Word> found = new ArrayList();
		
		if ( this.index_biwords.containsKey( bi ) ) {
			found = this.index_biwords.get( bi );
		}
		
		return found;
	}
	
	private void insertBiword( ArrayList<String> biwords, Word word ) {
		if ( ! biwords.isEmpty() ) {
			for ( String bi : biwords ) {
				ArrayList<Word> current_words = new ArrayList(); 
				if ( this.index_biwords.containsKey( bi ) ) {
					current_words = this.index_biwords.get( bi );
				}
				
				if ( current_words.isEmpty() || ! current_words.contains( word ) ) {
					current_words.add( word );
				}
				
				this.index_biwords.put( bi, current_words );
			}
		}
	}
	
	private void extractFileData() throws IOException, GateException {
		if ( (this.file != null) ) {
			// extract file words
			BufferedReader br = new BufferedReader( new FileReader( this.file ) );
			String line;
			while ( ( line = br.readLine() ) != null ) {
			   String[] voc_words = line.split( "\\s+" );
			   if ( voc_words.length > 0 ) {
				   for ( String word : voc_words ) {
					   this.vocabulary.add( word );	   
				   }
			   }
			}
			br.close();
		}
		
	}
	
}