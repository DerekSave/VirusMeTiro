package component;

import java.awt.Color;
import java.awt.Point;

@SuppressWarnings("serial")
public class Player extends MazeRect {

	public static final Color PLAYER_COLOR = Color.BLUE;

	public Player(Point p1, Point p2) {
		super(p1, p2, PLAYER_COLOR);
	}
}
