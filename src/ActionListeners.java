import gate.util.GateException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

import BigramIndex.DistanceWords;
import BigramIndex.Index;
import BigramIndex.SpellChecker;
import Morphologic.Morphologic;
import Semantic.Semantic;
import UIComponents.ResultPanel;

public class ActionListeners {
	
	final UI main_container;
	static SpellChecker spell_checker;
	final int draw_x = 0;
	final int draw_y = 0;
	final JPanel wrapper;
	Morphologic morph;
	final String[] characters = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y" ,"Z"};
	
	public ActionListeners(UI container) {
		 this.main_container = container;
		 this.morph = null;
		 this.wrapper = new JPanel();
	}
	
	public ActionListener closeAction() {
		
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
				//ImageProcessing im = new ImageProcessing("samples");
				//im.getDirFiles("samples");
				//im.formatDirImages("filter");
			}
        };
        
        return action;
	}
	
	public ActionListener clearTextField() {
		
		
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				main_container.middle_panel.text_field.setText("");
				main_container.middle_panel.spell_checker_text_field.setText("");
			}
        };
        
        return action;
	}
	
	public ActionListener correctWord() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String text = main_container.middle_panel.text_field.getSelectedText();
				String[] words = text.split( "\\s+" );
				String result = "";
				ArrayList<DistanceWords[]> all_posabilities = new ArrayList<DistanceWords[]>(); 
				
				long startTime = System.currentTimeMillis();
				for ( String word : words ) {
					ArrayList<DistanceWords> correct_words = ActionListeners.spell_checker.correct( word );
					result += "<span style='color:blue; font-size: 14px;' > " + word + "</span>: ";
					int count = 0;
					
					if ( ! correct_words.isEmpty() ) {
						if ( ! correct_words.get( 0 ).equals( word.toLowerCase() ) ) {
							String old = main_container.middle_panel.text_field.getText();
							main_container.middle_panel.text_field.setText( old.replaceAll( word, "<span style='color:red;'>" + word + "</span>") );
						}
					}
					
					DistanceWords[] chances = new DistanceWords[7];
					for ( DistanceWords correct : correct_words ) {
						result += correct.word + ", ";
						chances[count] = correct;
						if ( ++count > 6 ) {
							break;
						}
					}
					
					all_posabilities.add(chances);
					result += "<br />";
				}
				
				if (morph != null) {
					morph.extractWords(all_posabilities, new ArrayList<DistanceWords>(), 0);
					morph.normalise_value = ActionListeners.spell_checker.max_distance;
					String sentence = morph.bestSentence();
					String semantic_result = "";
					System.out.println("-" + sentence + "-");
					
					/*
					Semantic semantic = new Semantic();
					try {
						semantic_result = semantic.check(sentence);
					} catch (Exception e) {
						e.printStackTrace();
					}
					*/						
					
					String current_text = main_container.middle_panel.text_field.getText(); 
					main_container.middle_panel.text_field.setText("<span style='color:red;'>Old: </span>" + text + "<br />"
							+ " <span style='color:blue;'>New: </span>" + sentence);
									
				}
				

				
				main_container.middle_panel.spell_checker_text_field.setText( result );
				
				long endTime   = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				System.out.println( "Total Time:" + totalTime );
				
				
				
			}
		};
		
		return action;
	}
	
	public ActionListener addImageAction() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF Images", "jpg", "gif", "png", "bmp");
				chooser.setFileFilter(filter);
				
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					BufferedImage image_src = null;
					
					try {
						image_src = ImageIO.read(chooser.getSelectedFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					main_container.middle_panel.image_obj.setIcon(new ImageIcon(main_container.middle_panel.scale(image_src, 300, 200)));
					main_container.middle_panel.image_obj.repaint();
				}
				
			}
        }; 
        
        return action;
	}
	
	public ActionListener createMorphologic() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				morph = new Morphologic();
				
				try {
					// create the morphological logic
					
					boolean success = morph.deserializing();
					if ( ! success) {
						morph.constructWordDIctionary();
						morph.extractTextData();
					} else {
						System.out.println("Loaded Morph data");
					}
					
					// create the index logic
					Index bi_index;
					bi_index = new Index( morph.word_list );
					bi_index.createIndex();
					ActionListeners.spell_checker = new SpellChecker( bi_index );
					
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (GateException e1) {
					e1.printStackTrace();
				}
				System.out.println("Ready");
			}
		};
		
		return action;
	}	
	
	public ActionListener createIndex() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//morph.serializingObjects();
				
				//JFileChooser chooser = new JFileChooser();
				//chooser.setCurrentDirectory(new File("./"));
				String file_name = "wordsEn.txt";
				Index bi_index;
				try {
					bi_index = new Index( file_name );
					bi_index.createIndex();
					ActionListeners.spell_checker = new SpellChecker( bi_index );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		
		return action;
	}
	
	public ActionListener trainNetwork() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./"));
				String line;
				HashMap <Integer, double[]>class_container = new HashMap();
				ArrayList <Integer>training_order = new ArrayList();
				int class_number = 0;
				int sample_number = 0;
				double testing_parition_percents = 0.3;
				
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					//init file read
					//chooser.getSelectedFile()
					main_container.top_panel.choose_file_label.setText(chooser.getSelectedFile().getPath());
					BufferedReader br;
					try {
						br = new BufferedReader(new FileReader(chooser.getSelectedFile()));
						HashMap <Integer, ArrayList>training_data = new HashMap();
						HashMap <Integer, ArrayList>testing_data = new HashMap();
						while ((line = br.readLine()) != null) {
						   String []arr = line.split(",");
						   double[] buffer = new double[arr.length - 1];
						   String class_name = arr[0];
						   int j = 0;
						   
						   for (int c = 1; c < arr.length; c++) {
							   String str = arr[c];
							   if (str.trim() != "" && ! str.isEmpty()) {
								   buffer[j++] = Double.parseDouble(str);
							   }
						   }
						   
						   ArrayList wrapper = new ArrayList();
						   wrapper.add((int) Double.parseDouble(class_name));
						   wrapper.add(buffer);
						   wrapper.add( 1.00 );
						   
						   training_data.put(sample_number, wrapper);
						   training_order.add(sample_number++);
						   //create class
						   class_container.put((int)Double.parseDouble(class_name), null);
						}
						
						br.close();
						class_number = class_container.size();
						
						int ann_length = ((double[])training_data.get(0).get(1)).length;
						
						Collections.shuffle(training_order);
						
						// create a testing data 
						int testing_part = (int)(training_data.size() * testing_parition_percents);
						int kk = 0;
						
						for ( int a : training_order ) {
							if ( --testing_part < 0) {
								break;
							}
							
							testing_data.put( kk++, training_data.get( a ));
							training_data.remove( a );
						}
						
						//create update class object
						for (int key : class_container.keySet()) {
							double[] target = new double[class_number];
							Arrays.fill(target, 0);
							target[key] = 1;
							class_container.put(key, target);
						}
						
						System.out.println("Finished file reading");
						System.out.println("Network is Studing ...");
						
						//System.out.println(ann_length);
						for ( int i = 0; i < main_container.ANN.length; i++ ) {
							main_container.ANN[i] = new Network(ann_length, 400, class_number, 0.5, 0.2);
						
							int s = 0;
							double error = 0;
							for (int p = 0; p < 20; p ++) {
								for (int key : training_order) {
									ArrayList cont = training_data.get(key);
									if ( cont != null && ! cont.isEmpty() ) {
										if (cont.size() == 3) {
											double[] in = (double[]) cont.get(1);
											int class_name = (Integer) cont.get(0);
											double example_weight = (Double) cont.get(2);
											
											if ( ! in.equals(null)) {
												// array of 0 and only one 1 which shows the class
												double[] target_ann = class_container.get(class_name);
												/*
												System.out.print("Class " + class_name + ":");
												for (double d : target_ann) {
													System.out.print(d+", ");
												}
												System.out.println("");
												*/
												main_container.ANN[i].getOutputs(in);
												main_container.ANN[i].calculateError(target_ann, true);
												main_container.ANN[i].updateWeights();
												
												error = main_container.ANN[i].getError(in.length);
												
												// test the result with the testing data
												if ( s % 10 == 0 ) {
													ArrayList testing_content = new ArrayList();
													double[] testing_target_ann;
													for ( int t_key : testing_data.keySet() ) {
														testing_content = testing_data.get( t_key );
														if ( ! testing_content.isEmpty()) {
															if (testing_content.size() == 2) {
																double[] testing_content_in = (double[]) testing_content.get(1);
																int testing_content_class_name = (Integer) testing_content.get(0);
																main_container.ANN[i].getOutputs( testing_content_in );
																testing_target_ann = class_container.get( testing_content_class_name );
																main_container.ANN[i].calculateError( testing_target_ann, false );
																main_container.ANN[i].bestMatrix( main_container.ANN[i].getError( testing_content_in.length ) );
															}
														}
													}
												}
												// end of testing
												s++;
											
											}
										}
									}
								}
								System.out.println("Iteration: " + p + " Global Error:" + error);
								s = 0;
							}
						}
						
						//save training data into file
						saveTrainingData();
						System.out.println("Studing Finished");
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
        }; 
        
        return action;
	}
	
	public ActionListener recogniseImage() {
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				
				ArrayList <JPanel>result = new ArrayList();
				String result_character = "";
				ArrayList <double[]>sample_list = new ArrayList();
			   // BufferedImage bi = new BufferedImage(main_container.middle_panel.draw_panel.getWidth(), main_container.middle_panel.draw_panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		       // Graphics g = bi.createGraphics();
		       // main_container.middle_panel.draw_panel.print(g);
		        //g.dispose();
		        
		        //try {
		            //ImageIO.write(bi, "png", new File("test_img.png"));
		      //  } catch (IOException e) {
		            // TODO Auto-generated catch block
		       //     e.printStackTrace();
		       // }
		       // 
		        
				File image_src = new File("test.png");
				ImageProcessing images = new ImageProcessing(image_src.getAbsolutePath());
				
				ArrayList <Word>img_list = images.filterImage();
				int count = 0;
				String result_text = "";
				
				for ( Word word : img_list ) {
					ArrayList<Letter> letters = word.getWord();
					count++;
					int letter = 0;
					String spell_word = "";
					
					
					for ( Letter current : letters ) {
						Mat in = current.getLetter();
						Highgui.imwrite("export/" + count + "_" + (letter++) + ".png", in);
						int[] avg_result_vote = new int[characters.length];
						double[] export = images.exportORCData( in, new Size( 120, 90 ), -1 );
						for ( int i = 0; i < main_container.ANN.length; i++ ) {
							double[] arrr = main_container.ANN[i].getOutputs( export );
							
							//System.out.println("");
							//System.out.println("Result Array:");
							double max_val = Double.NEGATIVE_INFINITY;
							double max_inx = 0;
							int c = 0;
							for ( double res : arrr ) {
								if ( max_val <= res ) {
									max_val = res;
									max_inx = c;
								}
								c++;
								//System.out.print((res)+", ");
							}
							avg_result_vote[ (int) max_inx ]++;
						}
						
						//find bigger value:
						int max_inx = 0;
						for ( int a = max_inx; a < avg_result_vote.length; a++ ) {
							if ( avg_result_vote[ a ] >= avg_result_vote[ max_inx ] ) {
								max_inx = a;
							}
						}
						//System.out.print( "max index:" + max_inx );
						//System.out.println("Result Character: ");
						result_character = letter( (int) max_inx );
						spell_word += replaceNumbers( result_character.toLowerCase() );
						
						
					}
					if ( ! ActionListeners.spell_checker.correct( spell_word ).get( 0 ).word.trim().equals( spell_word.trim() ) ) {
						spell_word = "<span style='color:red;'>" + spell_word + "</span>";
					}
					result_text += spell_word + " ";
				}
				main_container.middle_panel.text_field.setText( result_text );
			}
        };
        
        return action;
	}
	
	protected static BufferedImage mat2Img(Mat image) {
		MatOfByte bytemat = new MatOfByte();

		Highgui.imencode(".jpg", image, bytemat);

		byte[] bytes = bytemat.toArray();

		InputStream in = new ByteArrayInputStream(bytes);

		try {
			return  ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
     } 	
	
	private void saveTrainingData() throws IOException {
		String data = "";
		FileWriter fw = new FileWriter("training_data.save", true);
		
		/* 1 row input count
		 * 2 row hidden count
		 * 3 row output count
		 * 4 row weights
		 * 5 row threshold
		 */
		for ( int i = 0; i < main_container.ANN.length; i++ ) {
			data = "";
			data += main_container.ANN[i].input_count + "\n";
			data += main_container.ANN[i].hidden_count + "\n";
			data += main_container.ANN[i].output_count + "\n";
			
			fw.write(data);
			
			for (double d : main_container.ANN[i].matrix) {
				fw.write(d + ",");
			}
			fw.write("\n");
			
			for (double d : main_container.ANN[i].threshold) {
				fw.write(d + ",");
			}
			fw.write("\n");
		}
		
		fw.close();
	}
	
	public ActionListener setTrainingData() {
		
		ActionListener action = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new File("./"));
				String line;
				ArrayList <String>data = new ArrayList();
				
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					//main_container.top_panel.choose_file_label.setText(chooser.getSelectedFile().getPath());
					BufferedReader br;
					try {
						br = new BufferedReader(new FileReader(chooser.getSelectedFile()));
						
						while ((line = br.readLine()) != null) {
							data.add(line.trim());
						}
						
						for (int j = 0; j < data.size(); j+=5 ) {
							int input_count = Integer.parseInt(data.get( j ));
							int hidden_count = Integer.parseInt(data.get( j + 1 ));
							int output_count = Integer.parseInt(data.get( j + 2 ));
							
							String[] weights_string = data.get( j + 3 ).split(",");
							double[] weights_double = new double[weights_string.length];
							for (int w = 0; w < weights_string.length; w++) {
								if (weights_string[w] != "") {
									weights_double[w] = Double.parseDouble(weights_string[w]);
								}
							}
							
							String[] threshold_string = data.get( j + 4 ).split(",");
							double[] threshold_double = new double[threshold_string.length];
							for (int w = 0; w < threshold_string.length; w++) {
								if (threshold_string[w] != "") {
									threshold_double[w] = Double.parseDouble(threshold_string[w]);
								}
							}
							
							for ( int i = 0; i < main_container.ANN.length; i++ ) {
								main_container.ANN[i] = new Network(input_count, hidden_count, output_count, weights_double, threshold_double);
								main_container.top_panel.choose_file_training.setText(chooser.getSelectedFile().getPath());
							}
						}						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		};
        return action;
	}
	
	private String replaceNumbers( String character ) {
		String result = character;
		
		if ( character == "0" ) result = "o";
		if ( character == "1" ) result = "i";
		if ( character == "4" ) result = "f";
		if ( character == "6" ) result = "s";
		if ( character == "7" ) result = "t";
		
		return result;
	}
	
	public String letter(int a) {
		
		String str = "N/A";
		if (a < characters.length) {
			System.out.print(characters[a]);
			str = characters[a];
		} else {
			System.out.print("N/A");
			str = "N/A";
		}
		
		return str;
	}
}
