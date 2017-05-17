package UIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class TopPanel extends JPanel {
	
	public JLabel choose_file_label;
	public JButton choose_file;
	
	public JLabel choose_file_index;
	public JButton choose_index;
	
	public JLabel set_morphologic;
	public JButton set_morphologic_button;	
	
	public JLabel choose_file_training;
	public JButton choose_trainng_button;	
	
	public TopPanel(FlowLayout flow_layout) {
		
		this.set_morphologic = new JLabel("Set a Morphologic", SwingConstants.RIGHT);
		this.set_morphologic.setPreferredSize(new Dimension(200,30));
		this.set_morphologic_button = new JButton("...");
		
		this.choose_file_index = new JLabel("Set a Index File", SwingConstants.RIGHT);
		this.choose_file_index.setPreferredSize(new Dimension(200,30));
		this.choose_index = new JButton("...");
		
		this.choose_file_label = new JLabel("Choose a Training File", SwingConstants.RIGHT);
		this.choose_file_label.setPreferredSize(new Dimension(200,30));
		this.choose_file = new JButton("...");
		
		this.choose_file_training = new JLabel("Set A Training Data", SwingConstants.RIGHT);
		this.choose_file_training.setPreferredSize(new Dimension(200,30));
		this.choose_trainng_button = new JButton("...");
		
		super.setLayout(flow_layout);
		super.setPreferredSize(new Dimension(600,80));
		super.setMaximumSize(new Dimension(900,80));
		super.setAlignmentX(LEFT_ALIGNMENT);
		super.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Training Dir"));
		
		super.add(this.set_morphologic);
		super.add(this.set_morphologic_button);
		
		super.add(this.choose_file_index);
		super.add(this.choose_index);
		
		//super.add(this.choose_file_label);
		//super.add(this.choose_file);
		
		super.add(this.choose_file_training);
		super.add(this.choose_trainng_button);
	}
	
}