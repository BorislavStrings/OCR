package UIComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class ResultPanel{
	
	public JPanel pane;
	
	public ResultPanel() {
		 this.pane = new JPanel();
		 this.pane.setPreferredSize(new Dimension(200, 300));
		 this.pane.setMaximumSize(new Dimension(200, 300));
		 this.pane.setMinimumSize(new Dimension(200, 300));
	}
	
	public void addComponent(JComponent comp) {
		this.pane.add(comp);
		this.pane.validate();
		this.pane.repaint();
	}
	
	public void removeAllComponents() {
		this.pane.removeAll();
		this.pane.validate();
		this.pane.repaint();
	}
	
}