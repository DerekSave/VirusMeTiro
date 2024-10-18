package maze;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import component.Finish;
import component.Player;
import component.Wall;

public class MazeReader {

	private Player player;
	private Set<Wall> walls = new HashSet<>();
	private Finish finish;
	private Dimension size;

	public MazeReader(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);

		String sizeLine = scanner.nextLine();
		int[] sizeNumbers = Arrays.stream(sizeLine.split(" ")).mapToInt(Integer::parseInt).toArray();
		size = new Dimension(sizeNumbers[0], sizeNumbers[1]);

		String playerLine = scanner.nextLine();
		int[] playerNumbers = Arrays.stream(playerLine.split(" ")).mapToInt(Integer::parseInt).toArray();
		player = new Player(new Point(playerNumbers[0], playerNumbers[1]),
				new Point(playerNumbers[2], playerNumbers[3]));

		String finishLine = scanner.nextLine();
		int[] finishNumbers = Arrays.stream(finishLine.split(" ")).mapToInt(Integer::parseInt).toArray();
		finish = new Finish(new Point(finishNumbers[0], finishNumbers[1]),
				new Point(finishNumbers[2], finishNumbers[3]));

		while (scanner.hasNextLine()) {
			String wallLine = scanner.nextLine();
			int[] wallNumbers = Arrays.stream(wallLine.split(" ")).mapToInt(Integer::parseInt).toArray();
			Wall wall = new Wall(new Point(wallNumbers[0], wallNumbers[1]), new Point(wallNumbers[2], wallNumbers[3]));
			walls.add(wall);
		}

		scanner.close();
	}

	public Player getPlayer() {
		return new Player(new Point((int) player.getX(), (int) player.getY()),
				new Point((int) (player.getX() + player.getWidth()), (int) (player.getY() + player.getHeight())));
	}

	public Finish getFinish() {
		return finish;
	}

	public Set<Wall> getWalls() {
		return walls;
	}

	public Dimension getSize() {
		return size;
	}

}
