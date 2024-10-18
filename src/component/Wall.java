package component;

import java.awt.Color;
import java.awt.Point;

@SuppressWarnings("serial")
public class Wall extends MazeRect {

	public static final Color WALL_COLOR = Color.BLACK;

	public Wall(Point p1, Point p2) {
		super(p1, p2, WALL_COLOR);
	}
}
