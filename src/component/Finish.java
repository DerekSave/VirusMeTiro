package component;

import java.awt.Color;
import java.awt.Point;

@SuppressWarnings("serial")
public class Finish extends MazeRect {
	public static final Color FINISH_COLOR = Color.RED;

	public Finish(Point p1, Point p2) {
		super(p1, p2, FINISH_COLOR);
	}
}
