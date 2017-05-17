import java.util.ArrayList;

public class Word {
	private ArrayList<Letter> word;
	
	public Word() {
		this.word = new ArrayList();
	}
	
	public Word( ArrayList<Letter> word ) {
		this.word = word;
	}		
	
	public void insertLetter( Letter letter) {
		this.word.add( letter );
	}
	
	public ArrayList<Letter> getWord() {
		return this.word;
	}
	
	public boolean isEmpty() {
		return  this.word.isEmpty();
	}
}