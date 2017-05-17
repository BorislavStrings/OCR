import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import UIComponents.BottomPanel;
import UIComponents.MiddlePanel;
import UIComponents.TopPanel;


public class UI extends JFrame {
	
	JPanel container;
	Network[] ANN;
	TopPanel top_panel;
	MiddlePanel middle_panel;
	BottomPanel bottom_panel;
	final FlowLayout flow_layout = new FlowLayout();
	ActionListeners ui_actions;
	
	public UI(Network[] ANN) {
		this.ANN = ANN;
		this.container = new JPanel();
		this.container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		this.top_panel = new TopPanel(flow_layout);
		this.middle_panel = new MiddlePanel(flow_layout);
		this.bottom_panel = new BottomPanel();
		
		this.container.add(this.top_panel);
		this.container.add(Box.createVerticalStrut(20));
		this.container.add(this.middle_panel);
		this.container.add(Box.createVerticalStrut(20));
		this.container.add(this.bottom_panel);
		this.container.setBorder(new EmptyBorder(20, 20, 20, 20));
		
		//jframe params
		super.setTitle("ANN - OCR");
	    super.setSize(900, 700);
	    super.setLocationRelativeTo(null);
	    super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    super.getContentPane().add(this.container);
	    super.setBackground(new Color(191, 191, 191));
	    
	    //set action listeners
	    this.ui_actions = new ActionListeners(this);
	    this.setActionListeners();
	}
	
	private void setActionListeners() {
		//bottom panel
		this.bottom_panel.app_close.addActionListener(this.ui_actions.closeAction());
		this.bottom_panel.image_add.addActionListener(this.ui_actions.addImageAction());
		
		//top panel
		this.top_panel.choose_file.addActionListener(this.ui_actions.trainNetwork());
		this.top_panel.choose_trainng_button.addActionListener(this.ui_actions.setTrainingData());
		
		this.top_panel.choose_index.addActionListener(this.ui_actions.createIndex());
		this.top_panel.set_morphologic_button.addActionListener(this.ui_actions.createMorphologic());
		
		//middle panel
		this.middle_panel.recognise.addActionListener(this.ui_actions.recogniseImage());
		this.middle_panel.correct.addActionListener(this.ui_actions.correctWord());
		
		this.middle_panel.button_clear.addActionListener(this.ui_actions.clearTextField());
	}
	
	public void showWindow() {
		this.setVisible(true);
	}
	
	public void hideWindow() {
		this.setVisible(false);
	}
}
