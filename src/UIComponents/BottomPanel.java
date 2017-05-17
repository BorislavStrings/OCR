package UIComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class BottomPanel extends JPanel {
	
	public JButton image_add;
	public JButton app_close;
	
	public BottomPanel() {
		JPanel right = new JPanel();
		right.setAlignmentX(Component.RIGHT_ALIGNMENT);
		//right.setPreferredSize(new Dimension(100, 50));
		//right.setMaximumSize(new Dimension(100, 50));
		
		this.image_add = new JButton("Add Image");
		this.image_add.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.app_close = new JButton("Close");
		this.app_close.setAlignmentX(Component.RIGHT_ALIGNMENT);
		this.app_close.setPreferredSize(new Dimension(100, 40));
		
		right.add(app_close);
		
		super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//super.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.black, 1), "Actions"));
		super.setPreferredSize(new Dimension(900, 50));
		super.setMinimumSize(new Dimension(900, 50));
		super.setMaximumSize(new Dimension(900, 50));
		super.add(right);
		super.setAlignmentX(LEFT_ALIGNMENT);
	}
	
}