package UIComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.Border;

public class DrawPanel extends JPanel implements MouseMotionListener, ActionListener {
	int draw_x = 0;
	int draw_y = 0;
	ArrayList <Point>points;
	
	public DrawPanel(Border b) {
		this.setPreferredSize(new Dimension(600, 300));
		this.setMaximumSize(new Dimension(600, 300));
		this.setMinimumSize(new Dimension(600, 300));
        this.addMouseMotionListener(this);
        this.setBorder(b);
		this.setBackground(Color.WHITE);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.points = new ArrayList();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.draw_x = e.getX();
		this.draw_y = e.getY();
		this.points.add(new Point(this.draw_x, this.draw_y));
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		this.points.removeAll(this.points);
		this.repaint();
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		for (Point p : this.points) {
			g.fillOval(p.x, p.y, 15, 15);
		}
	}
}