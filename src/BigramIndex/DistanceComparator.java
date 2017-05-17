package BigramIndex;

import java.util.Comparator;

public class DistanceComparator implements Comparator<DistanceWords> {
	
	@Override
	public int compare( DistanceWords arg0, DistanceWords arg1) {
		return  (int)(arg0.distance - arg1.distance);
	}

}