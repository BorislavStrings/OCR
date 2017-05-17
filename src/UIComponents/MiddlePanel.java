package UIComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.html.HTMLEditorKit;

public class MiddlePanel extends JPanel {
	
	public JPanel top_panel;
	public JPanel down_panel;
	//public ResultPanel result_area;
	public JLabel image_obj;
	public JEditorPane  text_field;
	public JEditorPane spell_checker_text_field;
	//public DrawPanel draw_panel;
	public JButton recognise;
	public JButton correct;
	public JButton button_clear;
	
	public MiddlePanel(FlowLayout flow_layout) {		
		BufferedImage image_src = null;
		Border border = BorderFactory.createLineBorder(Color.black, 1);
		JPanel actions = new JPanel();
		actions.setPreferredSize(new Dimension(900, 40));
		actions.setMaximumSize(new Dimension(900, 40));
		//actions.setAlignmentX(LEFT_ALIGNMENT);
		this.top_panel = new JPanel();
		this.down_panel = new JPanel();
		//this.draw_panel = new DrawPanel(null);
		this.recognise = new JButton("Recognise");
		this.correct = new JButton("Is Correct?");
		this.text_field = new JEditorPane(new HTMLEditorKit().getContentType(), "");
		this.text_field.setSize( new Dimension( 900, 120 ) );
		this.text_field.setMaximumSize( new Dimension( 900, 120 ) );
		this.text_field.setPreferredSize( new Dimension( 900, 120 ) );
		
		this.spell_checker_text_field = new JEditorPane(new HTMLEditorKit().getContentType(), "");
		this.spell_checker_text_field.setSize( new Dimension( 900, 200 ) );
		this.spell_checker_text_field.setMinimumSize( new Dimension( 900, 200 ) );
		
		/*
		try {
			image_src = ImageIO.read(new File("images/example_1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//left panel
		JLabel image_title = new JLabel("Result Text:");
		image_title.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//this.top_panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.top_panel.setPreferredSize( new Dimension( 900, 300) );
		this.top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.PAGE_AXIS));
		this.top_panel.setBorder(new EmptyBorder(20, 20, 20, 0));
		this.top_panel.add(image_title);
		//this.left_panel.add(this.image_obj);
		
		
		
		this.top_panel.add( this.text_field );
		this.button_clear = new JButton("Clear");
		//button_clear.addActionListener(this.drawtext_field_panel);
		this.top_panel.add(Box.createVerticalStrut(5));
		actions.add(button_clear);
		actions.add(this.recognise);
		actions.add(this.correct);
		this.top_panel.add(actions);
		//this.recognise.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//right panel
		JLabel result_label = new JLabel("Spell Checker:");
		result_label.setAlignmentX(Component.LEFT_ALIGNMENT);
		//this.result_area = new ResultPanel();
		//this.result_area.pane.setBackground(Color.WHITE);
		//this.result_area.setMargin(new Insets(10,10,10,10));
		//this.result_area.setEnabled(false);
		//this.result_area.setBorder(border);
		//this.result_area.pane.setAlignmentX(Component.LEFT_ALIGNMENT);
		//left_panel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		this.down_panel.setLayout(new BoxLayout(down_panel, BoxLayout.PAGE_AXIS));
		this.down_panel.setBorder(new EmptyBorder(20, 20, 20, 0));

		this.down_panel.add(result_label);
		//this.down_panel.add(result_area.pane);
		this.down_panel.add( this.spell_checker_text_field );
		this.down_panel.setMinimumSize(new Dimension(900, 190));
		this.down_panel.setPreferredSize(new Dimension(900, 190));
		this.down_panel.setMaximumSize(new Dimension(900, 190));
		
		super.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Content"));
		super.setPreferredSize(new Dimension(900, 474));
		super.setMaximumSize(new Dimension(900, 474));
		super.setAlignmentX(LEFT_ALIGNMENT);
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		super.add(top_panel);
		super.add(Box.createHorizontalStrut(10));
		super.add(new JSeparator(SwingConstants.HORIZONTAL));
		super.add(Box.createHorizontalStrut(10));
		super.add(down_panel);
	}
	
	public JLabel setImageLabel(int w, int h, BufferedImage image) {  
        BufferedImage scaled = scale(image, w, h);  
        JLabel label = new JLabel(new ImageIcon(scaled));  
        label.setPreferredSize(new Dimension(w, h));  
        label.setBorder(BorderFactory.createEtchedBorder());  
        return label;
	}
	
	public BufferedImage scale(BufferedImage src, int w, int h) {
        int type = BufferedImage.TYPE_INT_RGB;  
        BufferedImage dst = new BufferedImage(w, h, type);  
        Graphics2D g2 = dst.createGraphics();  
        // Fill background for scale to fit.  
        g2.setBackground(UIManager.getColor("Panel.background"));  
        g2.clearRect(0,0,w,h);  
        double xScale = (double)w/src.getWidth();  
        double yScale = (double)h/src.getHeight();  
        // Scaling options:  
        // Scale to fit - image just fits in label.  
        double scale = Math.min(xScale, yScale);  
        // Scale to fill - image just fills label.  
        //double scale = Math.max(xScale, yScale);  
        int width  = (int)(scale*src.getWidth());  
        int height = (int)(scale*src.getHeight());  
        int x = (w - width)/2;  
        int y = (h - height)/2;  
        g2.drawImage(src, x, y, width, height, null);  
        g2.dispose();  
        return dst;  
    }	
}