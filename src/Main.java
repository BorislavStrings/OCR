import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;


public class Main {

	static Network[] ANN = new Network[30];
	
	public static void main(String[] args) {
		UI main_container = new UI(Main.ANN);
		
		main_container.showWindow();
	}

}
