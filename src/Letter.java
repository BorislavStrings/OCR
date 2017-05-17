import org.opencv.core.Mat;


public class Letter {
	private Mat letter = null;
	private double cor_x, cor_y;
	
	public Letter( Mat m, double cor_x, double cor_y ) {
		this.letter = m;
		this.cor_x = cor_x;
		this.cor_y = cor_y;
	}
	
	public double getX() {
		return this.cor_x;
	}
	
	public double getY() {
		return this.cor_y;
	}
	
	public Mat getLetter() {
		return this.letter;
	}

	public boolean isClose(Letter l, double treshold, double letter_distance_threshold ) {
		//System.out.println( this.getX() + this.getLetter().cols() - l.getX() + " < "  + treshold + letter_distance_threshold );
		if ( Math.abs( this.getX() + this.getLetter().cols() - l.getX() ) < ( treshold + letter_distance_threshold ) ) {
			return true;
		}
		return false;
	}
}