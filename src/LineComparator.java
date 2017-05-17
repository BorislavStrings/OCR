import java.util.Comparator;


public class LineComparator implements Comparator<Letter> {

	@Override
	public int compare(Letter l1, Letter l2) {
		return (int) ( l1.getY() - l2.getY() );
	}

}
