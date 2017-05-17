import java.util.Comparator;


public class LetterComparator implements Comparator<Letter> {

	@Override
	public int compare(Letter l1, Letter l2) {
		return (int) ( l1.getX() - l2.getX() );
	}

}
