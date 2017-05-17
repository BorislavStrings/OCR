package Morphologic;

import gate.util.GateException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import BigramIndex.DistanceWords;

public class Morphologic {
	
	private String file = "morphologic.txt";
	private String[] studing_files = new String[]{
			"books/1.txt", "books/2.txt", "books/3.txt", "books/4.txt", "books/5.txt"     
	};
	private HashMap<String, ArrayList<DictionaryWord>> vocabulary = new HashMap();
	private HashMap<String, String> morphological_vocabulary = new HashMap();
	private HashMap<String, ArrayList<DictionaryMorphologic>> morphological_statistic = new HashMap();
	public ArrayList<ArrayList<DistanceWords>> sentences = new ArrayList<ArrayList<DistanceWords>>();
	public ArrayList<String> word_list = new ArrayList<String>();
	public int normalise_value = 0; 
	
	@SuppressWarnings("unchecked")
	public ArrayList<DistanceWords> extractWords(ArrayList<DistanceWords[]> words, ArrayList<DistanceWords> words_sequance, int inx) {
		// each word has a separate String[] in the array list. The String[] contain all variations
		ArrayList<DistanceWords> sentence = (ArrayList<DistanceWords>) words_sequance.clone();
		DistanceWords[] word = words.get(inx);
		for (DistanceWords w : word) {
			if (sentence.size() == (inx + 1)) {
				sentence.remove(inx);
			}
			sentence.add(inx, w);
			
			// here we push the new word
			if ((inx + 1) == words.size()) {
				this.sentences.add((ArrayList<DistanceWords>) sentence.clone());
			}
			
			if ((inx + 1) < words.size()) {
				this.extractWords(words, sentence, (inx + 1));
			}
		}
		
		return words_sequance;
	}
	
	public String bestSentence() {
		double score = 0.00d;
		System.out.println("asd");
		double max = Double.MIN_VALUE;
		
		String best_sentence = "";
		System.out.println( normalise_value );
		for (ArrayList<DistanceWords> sentence : this.sentences) {
			String prev_word = "";
			String string_sentence = "";
			score = 0.00d;
			for (DistanceWords word : sentence) {
				if (word != null && word.word != null) {
					string_sentence += " " + word.word;
					score += (double)(( normalise_value - word.distance ) * this.getMorphologicalStatistic(word.word, prev_word) * this.getWordStatistic(word.word, prev_word));
					//System.out.println(( normalise_value - word.distance ) + "----:  " + this.getMorphologicalStatistic(word.word, prev_word) + ":::" + this.getWordStatistic(word.word, prev_word) );
					prev_word = word.word;
				}
			}
			
			System.out.println(score + ":  " + string_sentence);
			
			if (score > max) {
				best_sentence = string_sentence + " ";
				max = score;
			}
		}
		
		return best_sentence;
	}
	
	/*
	public static void main(String[] args) {
		Morphologic morph = new Morphologic();
		
		String[][] words = new String[3][3];
		ArrayList<String[]> array = new ArrayList<String[]>();
		words[0][0] = "a";
		words[0][1] = "b";
		words[0][2] = "c";
		words[1][0] = "d";
		words[1][1] = "e";
		words[1][2] = "f";
		words[2][0] = "g";
		words[2][1] = "h";
		words[2][2] = "i";
		
		array.add(words[0]);
		array.add(words[1]);
		array.add(words[2]);
		
		morph.extractWords(array, new ArrayList<String>(), 0);
		
		for (ArrayList<String> e_words : morph.sentences) {
			for (String e_word : e_words) {
				System.out.print(e_word + ", ");
			}
			System.out.println("//");
		}
	}
	*/
	
	private String getMorhologicalType(String word) {
		String type = this.morphological_vocabulary.get(word);
		if (type == null) {
			return "";
		}
		
		return type;
	}
	
	private double getWordStatistic(String statistic_word, String prev_word) {
		ArrayList<DictionaryWord> items = this.vocabulary.get(statistic_word);
		int total_count = 0;
		int word_count = 0;
		
		if (items != null) {
			for (DictionaryWord word : items) {
				total_count += word.getCount();
				if (word.getWord().equals(prev_word)) {
					word_count = word.getCount();
				}
			}
		} else {
			
			return 0.0001;
		}
		
		double result = (double)word_count / total_count;
		
		if (result < 0.0001 ) return 0.001;
		
		return result;
	}
	
	private double getMorphologicalStatistic(String statistic_word, String prev_word) {
		String current_word_type = this.morphological_vocabulary.get(statistic_word);
		String prev_word_type = this.morphological_vocabulary.get(prev_word);
		if (prev_word_type == null) prev_word_type = ""; 
		int total_count = 0;
		int word_count = 0;
		
		if (current_word_type == null || prev_word_type == null || ( current_word_type == "" && prev_word_type == "")) {
			return 0.0001;
		}
		
		ArrayList<DictionaryMorphologic> items = this.morphological_statistic.get(current_word_type);
		
		if (items != null && ! items.isEmpty()) {
			for (DictionaryMorphologic item : items) {
				total_count += item.getCount();
				if (item.getType().equals(prev_word_type)) {
					word_count = item.getCount();
				}
			}
		} else {
			
			return 0.0001;
		}
		
		return (double)word_count / total_count;
	}
	
	public void extractTextData() throws IOException, GateException {
		// extract file words
		for (String file : this.studing_files) {
			BufferedReader br = new BufferedReader( new FileReader(file) );
			String line;
			while ( ( line = br.readLine() ) != null ) {
			   String[] voc_words = line.split( "\\s+" );
			   if ( voc_words.length > 0 ) {
				   Word prev = null;
				   ArrayList<DictionaryWord> items = new ArrayList<DictionaryWord>();
				   for ( String word : voc_words ) {
					   word = word.trim().toLowerCase();
					   if ( ! word.isEmpty()) {
						   if (prev != null) {
							   items = this.vocabulary.get( word );
							   int inx = -1;
							   
							   if (items != null) {
								   for (DictionaryWord i : items) {
									   if (i.getWord().equals(prev.word)) {
										   inx = items.indexOf(i);
									   }
								   }
							   } else {
								   items = new ArrayList<DictionaryWord>();
							   }
							   
							   if (inx > -1) {
								   items.get(inx).increase();
							   } else {
								   items.add(new DictionaryWord( prev, 1));   
							   }
							   
							   this.vocabulary.put(word, items);
						   } else {
							   this.vocabulary.put(word, new ArrayList<DictionaryWord>());
						   }
						   
						   String prev_type = null;
						   if (prev != null) {
							   prev_type = this.getMorhologicalType(prev.word);   
						   }
						   
						   String word_type = this.getMorhologicalType(word);
						   
						   prev = new Word(word, word_type);
						   this.morphologicalStatistic(word_type, prev_type);
					   }
				   }
			   }
			}
			br.close();
		}
	}
	
	private void morphologicalStatistic(String type_next, String type_prev) {
		ArrayList<DictionaryMorphologic> items = this.morphological_statistic.get(type_next);
		type_prev = (type_prev == null ? "" : type_prev);
		boolean found = false;
		
		if ( items != null && ! items.isEmpty()) {
			for (DictionaryMorphologic item : items) {
				if (item.getType().equals(type_prev)) {
					item.increase();
					found = true;
					break;
				}
			}
			
			if ( ! found) {
				items.add(new DictionaryMorphologic(type_prev, 1)); 
			}
		} else {
			items = new ArrayList<DictionaryMorphologic>();
			items.add(new DictionaryMorphologic(type_prev, 1));
		}
		
		this.morphological_statistic.put(type_next, items);
	}
	
	
	// create a list with words - it will be used in the spell checker
	// create a morphological hashmap: word -> form
	public void constructWordDIctionary() throws IOException, GateException {
		if ( (this.file != null) ) {
			// extract file words
			BufferedReader br = new BufferedReader( new FileReader( this.file ) );
			String line;
			while ( ( line = br.readLine() ) != null ) {
			   String[] voc_words = line.split("\\.");
			   if ( voc_words.length > 0 ) {
				   String[] words = voc_words[0].split( "," );
				   
				   String type = voc_words[1];
				   if (type.indexOf(":") > -1) {
					   String[] type_forms = type.split( ":" );
					   type = type_forms[0];
				   }
				   
				   if (type.indexOf("+") > -1) {
					   String[] type_forms = type.split( "\\+" );
					   type = type_forms[0];
				   }
				   
				   ArrayList<DictionaryWord> items = new ArrayList();
				   for ( String word : words ) {
					   this.morphological_vocabulary.put(word, type);
					   this.word_list.add(word);
				   }
			   }
			}
			br.close();
		}
	}
	
	public void serializingObjects() {
	      try {
	    	  System.out.println("Starting Serializing Objects!");
	    	 // write vocabulary
	         FileOutputStream fileOut = new FileOutputStream("tmp/vocabulary.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this.vocabulary);
	         out.close();
	         fileOut.close();
	         
	    	 // write morphological_vocabulary
	         fileOut = new FileOutputStream("tmp/morphological_vocabulary.ser");
	         out = new ObjectOutputStream(fileOut);
	         out.writeObject(this.morphological_vocabulary);
	         out.close();
	         fileOut.close();

	    	 // write morphological_statistic
	         fileOut = new FileOutputStream("tmp/morphological_statistic.ser");
	         out = new ObjectOutputStream(fileOut);
	         out.writeObject(this.morphological_statistic);
	         out.close();
	         fileOut.close();
	         
	    	 // write morphological_statistic
	         fileOut = new FileOutputStream("tmp/word_list.ser");
	         out = new ObjectOutputStream(fileOut);
	         out.writeObject(this.word_list);
	         out.close();
	         fileOut.close();	         
	         
	         System.out.printf("Serialized data is saved!");
	      } catch(IOException i) {
	          i.printStackTrace();
	      }
	}
	
	public boolean deserializing() {
	      try
	      {
	         FileInputStream fileIn = new FileInputStream("tmp/vocabulary.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         this.vocabulary = (HashMap<String, ArrayList<DictionaryWord>>) in.readObject();
	         in.close();
	         fileIn.close();

	         fileIn = new FileInputStream("tmp/morphological_vocabulary.ser");
	         in = new ObjectInputStream(fileIn);
	         this.morphological_vocabulary = (HashMap<String, String>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         fileIn = new FileInputStream("tmp/morphological_statistic.ser");
	         in = new ObjectInputStream(fileIn);
	         this.morphological_statistic = (HashMap<String, ArrayList<DictionaryMorphologic>>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         fileIn = new FileInputStream("tmp/word_list.ser");
	         in = new ObjectInputStream(fileIn);
	         this.word_list = (ArrayList<String>) in.readObject();
	         in.close();
	         fileIn.close();
	         
	         return true;
	         
	      } catch(IOException i) {
	         i.printStackTrace();
	         return false;
	      } catch(ClassNotFoundException c) {
	         c.printStackTrace();
	         return false;
	      }
	}
	
	/*
	public static void main(String[] args) throws IOException, GateException {
		Morphologic morph = new Morphologic();
		morph.constructWordDIctionary();
		
		for (String word : morph.morphological_vocabulary.keySet()) {
			System.out.println( word + " : " + morph.morphological_vocabulary.get(word));
		}
		
		morph.extractTextData();
		System.out.println("Ready");
		
	}
	*/
}
