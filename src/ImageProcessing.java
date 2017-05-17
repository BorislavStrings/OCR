import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.opencv.contrib.Contrib;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ImageProcessing {

	public File file;
	final int image_h = 70;
	final int image_w = 50;

	public ImageProcessing(String file) {
		this.file = new File(file);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public ArrayList<Word> filterImage() {
		Mat image_original = Highgui.imread(this.file.getAbsolutePath());
		ArrayList<String> img_segmented = new ArrayList();
		int w = image_original.width();
		int h = image_original.height();
		int count = 0;
		double mean_ratio = 0f;

		// Mat thumb = new Mat(new Size(w, h), 0);
		Mat image_gray = new Mat(new Size(w, h), 0);
		Mat image_filter = new Mat(new Size(w, h), 0);
		Mat image_contour = new Mat(new Size(w, h), 0);

		List<MatOfPoint> contours = new ArrayList();
		Mat contours_info = new Mat();
		ArrayList<Letter> letters = new ArrayList();

		if (!image_original.empty()) {
			// Imgproc.resize(image_mat, thumb, thumb.size());
			Imgproc.cvtColor(image_original, image_gray, Imgproc.COLOR_RGB2GRAY);
			Imgproc.medianBlur(image_gray, image_gray, 5);
			
			Highgui.imwrite("blur.png", image_gray);
			Highgui.imwrite("gray.png", image_gray);
			Imgproc.threshold(image_gray, image_filter, 128, 255, 1);
			Highgui.imwrite("thresh.png", image_filter);
			Imgproc.dilate(image_filter, image_filter, Imgproc
					.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)),
					new Point(-1, -1), 4);
			Highgui.imwrite("dilate.png", image_filter);
			Imgproc.erode(image_filter, image_filter, Imgproc
					.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)),
					new Point(-1, -1), 2);
			Highgui.imwrite("erode.png", image_filter);
			// Highgui.imwrite("rendered_image_blob.png", thumb_threshold);

			Imgproc.findContours(image_filter, contours, contours_info,
					Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(
							0, 0));
			Highgui.imwrite("contour.png", image_filter);

			int rect_count = 0;
			int buffer = 2;
			// compute mean ratio
			for (MatOfPoint i : contours) {
				Rect rec = Imgproc.boundingRect(i);
				double[] hierarchy = contours_info.get(0, rect_count);
				// get children of the first parent (the image)
				if (hierarchy[3] < 0) {
					letters.add( new Letter( image_original.submat( rec.y - buffer, rec.y + rec.height + buffer, rec.x - buffer, rec.x + rec.width + buffer ), rec.x - buffer, rec.y - buffer ) );
				}
				rect_count++;
			}
			
			//count = 0;
			//for ( Mat l : letters ) {
				//Highgui.imwrite("export/letter" + (++count) + ".jpg", l );
			//}
			
			// save image
			Highgui.imwrite("rendered_image.png", image_filter);
		}
		
		// sort list
		//Collections.sort( letters, new LetterComparator() );
		Words words = new Words( letters );
		ArrayList<Word> words_list = words.getWords();
		return words_list;
	}

	public Mat histogram(Mat img, int t) {
		int sz = (t == 0 ? img.rows() : img.cols());

		Mat mhist = Mat.zeros(1, sz, CvType.CV_32F);
		Mat data = new Mat(1, sz, 0);

		for (int j = 0; j < sz; j++) {
			data = (t == 0 ? img.row(j) : img.col(j));
			Core.extractChannel(data, data, 0);
			mhist.put(0, j, Core.countNonZero(data));
		}

		// Normalize histogram
		Core.MinMaxLocResult minMax = Core.minMaxLoc(mhist);
		double min = minMax.minVal, max = minMax.maxVal;

		if (max > 0) {
			mhist.convertTo(mhist, -1, 1.0f / max, 0);
		}

		return mhist;
	}

	public Mat invertImage(Mat m) {
		for (int i = 0; i < m.rows(); i++) {
			for (int j = 0; j < m.cols(); j++) {
				double[] data = m.get(i, j);
				for (int k = 0; k < data.length; k++) {
					if (data[k] == 255f) {
						data[k] = 0f;
					} else {
						data[k] = 255f;
					}
				}
				m.put(i, j, data);
			}
		}

		return m;
	}

	private Mat resizeToBox(Mat mat, int size) {
		Mat result = Mat.zeros(new Size(size, size), CvType.CV_32F);
		double ratio = (double) mat.cols() / mat.rows();

		if (ratio > 1) {
			Imgproc.resize(mat, mat, new Size(size, (int) size / ratio));
			Mat selected_area = result.submat(new Rect(0, (result.rows() - mat
					.rows()) / 2, mat.cols(), mat.rows()));
			mat.copyTo(selected_area);
		} else if (ratio < 1) {
			Imgproc.resize(mat, mat, new Size((int) size * ratio, size));
			Mat selected_area = result.submat(new Rect((result.cols() - mat
					.cols()) / 2, 0, mat.cols(), mat.rows()));
			mat.copyTo(selected_area);
		} else {
			Imgproc.resize(mat, result, new Size(size, size));
		}
		return result;
	}

	public double[] exportORCData(Mat in, Size sizeData, int class_name) {
		// Low data feature
		in.convertTo(in, CvType.CV_32F);
		Imgproc.cvtColor(in, in, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(in, in, 128, 255, 1);
		// in = this.resizeToBox(in, 100);

		Mat lowData = Mat.zeros(new Size(100, 100), CvType.CV_32F);
		// Imgproc.resize(in, lowData, new Size(100, 100));
		lowData = this.resizeToBox(in, 100);
		// Highgui.imwrite("blablabla.png", lowData);

		// Histogram feature
		Mat vhist = this.histogram(lowData, 1);
		Mat hhist = this.histogram(lowData, 0);

		Imgproc.resize(lowData, lowData, new Size(20, 20));
		// Random rand = new Random();
		// Highgui.imwrite("ready/" + rand.nextInt() + ".png", in);
		int numCols = vhist.cols() + hhist.cols() + lowData.rows()
				* lowData.cols();

		int j = 0;
		double[] out = new double[numCols + 1];
		if (class_name > 0) {
			out[0] = class_name;
			// Assign values to feature
			j = 1;
		}

		for (int i = 0; i < vhist.cols(); i++) {
			out[j] = vhist.get(0, i)[0];
			j++;
		}

		for (int i = 0; i < hhist.cols(); i++) {
			out[j] = hhist.get(0, i)[0];
			j++;
		}

		for (int x = 0; x < lowData.rows(); x++) {
			for (int y = 0; y < lowData.cols(); y++) {
				out[j] = lowData.get(x, y)[0] / 255;
				j++;
			}
		}

		return out;
	}

	public void getDirFiles(String dir) {
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		HashMap<Integer, double[]> sample_list = new HashMap();
		int count = 0;
		int class_name = 0;

		String filename = "training_data.txt";
		String data = "";
		FileWriter fw;
		try {
			fw = new FileWriter(filename, true);

			for (File sub_dir : listOfFiles) {
				String ch_name = sub_dir.getName();
				System.out.println(ch_name);
				File[] samples = sub_dir.listFiles();
				for (File ch : samples) {
					data = "";
					Mat in = Highgui.imread(ch.getAbsolutePath());
					// sample_list.put(count++, this.exportORCData(in, new
					// Size(120, 90), class_name));
					double[] res = this.exportORCData(in, new Size(120, 90),
							class_name);
					for (int i = 0; i < res.length; i++) {
						data += res[i] + ",";
					}
					data.substring(0, data.length() - 1);
					data += "\n";
					fw.write(data);
				}
				class_name++;
			}
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // the true will append the new data

		// this.exportTrainingFile(sample_list);
	}

	public void formatDirImages(String dir) {
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		HashMap<Integer, double[]> sample_list = new HashMap();
		int count = 0;

		for (File sub_dir : listOfFiles) {
			String ch_name = sub_dir.getName();
			System.out.println(ch_name);
			File theDir = new File("filtered/" + ch_name);

			// if the directory does not exist, create it
			if (!theDir.exists()) {
				theDir.mkdir();
			}

			File[] samples = sub_dir.listFiles();
			for (File ch : samples) {
				Mat in = Highgui.imread(ch.getAbsolutePath());
				// sample_list.put(count++, this.exportORCData(in, new Size(120,
				// 90), class_name));
				ArrayList<Letter> res = new ArrayList();
				this.file = ch;
				// fix this!
				res = null;//this.filterImage();
				for (Letter let : res) {
					Mat image_original = let.getLetter();
					//Mat image_original = Highgui.imread(m);
					Highgui.imwrite("filtered/" + ch_name + "/" + ch.getName(),
							image_original);
				}
			}
			count++;
		}

		// this.exportTrainingFile(sample_list);
	}

	public void exportTrainingFile(HashMap<Integer, double[]> in)
			throws FileNotFoundException, UnsupportedEncodingException {
		String data = "";
		int counter = 0;

		for (Integer ii : in.keySet()) {
			double[] x = in.get(ii);
			// set the id number of the element 0 - 0, 1 - 1, ..., 10 - a, 11 -
			// b, ...
			data += "" + (counter++) + ",";
			for (int i = 0; i < x.length; i++) {
				data += x[i] + ",";
			}
			data.substring(0, data.length() - 1);
			data += "\n";
		}

		PrintWriter writer = new PrintWriter("training_data.txt", "UTF-8");
		writer.println(data);
		writer.close();
	}
}
