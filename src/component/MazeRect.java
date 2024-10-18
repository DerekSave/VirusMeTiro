package component;

import java.awt.Color;
import java.awt.Point;

import acm.graphics.GRect;

@SuppressWarnings("serial")
public class MazeRect extends GRect {
	public MazeRect(Point p1, Point p2, Color color) {
		super(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y));
		setColor(color);
		setFilled(true);
		setFillColor(color);
	}
}
