package maze;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.program.Program;
import component.Finish;
import component.Player;
import component.Wall;

@SuppressWarnings("serial")
public class MazeFactory extends GraphicsProgram {

	private List<GRect> cache = new ArrayList<>();
	private int cachePointer = -1;

	private int currentFigure = WALL_ID;

	private static final int WALL_ID = 1;
	private static final int FINISH_ID = 2;
	private static final int PLAYER_ID = 3;

	public static final int DEFAULT_WIDTH = 800;
	public static final int DEFAULT_HEIGHT = 600;

	@Override
	public void run() {
		setTitle("maze factory");
		setBackground(Color.BLACK);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		GLabel bigInfo = new GLabel("draw rectangles to create maze");
		bigInfo.setFont(new Font("Verdana", 0, 30));
		bigInfo.setLocation((getWidth() - bigInfo.getWidth()) / 2, 400);
		bigInfo.setColor(Color.WHITE);
		add(bigInfo);

		GLabel smallInfo = new GLabel("press [1] to draw walls, [2] to draw finish, [3] to add player");
		smallInfo.setFont(new Font("Verdana", 0, 14));
		smallInfo.setLocation((getWidth() - smallInfo.getWidth()) / 2, bigInfo.getY() + 40);
		smallInfo.setColor(Color.WHITE);
		add(smallInfo);

		pause(3000);
		remove(bigInfo);
		remove(smallInfo);
		setBackground(Color.CYAN);

		GRect background = new GRect(0, 0, 2000, 2000);
		background.setColor(new Color(0, 0, 0, 0));
		add(background);

		GRect colorLabel = new GRect(-10, -10, 10, 10);
		colorLabel.setFilled(true);
		colorLabel.setColor(Color.RED);
		colorLabel.setFillColor(Wall.WALL_COLOR);
		add(colorLabel);

		addKeyListeners(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == String.valueOf(WALL_ID).charAt(0)) {
					currentFigure = WALL_ID;
					colorLabel.setColor(Color.PINK);
					colorLabel.setFillColor(Wall.WALL_COLOR.brighter().brighter());

				} else if (event.getKeyChar() == String.valueOf(FINISH_ID).charAt(0)) {
					currentFigure = FINISH_ID;
					colorLabel.setColor(Color.PINK);
					colorLabel.setFillColor(Finish.FINISH_COLOR.brighter().brighter());

				} else if (event.getKeyChar() == String.valueOf(PLAYER_ID).charAt(0)) {
					currentFigure = PLAYER_ID;
					colorLabel.setColor(Color.PINK);
					colorLabel.setFillColor(Player.PLAYER_COLOR.brighter().brighter());
				} else if (event.getKeyCode() == 83 && event.isControlDown()) {
					save();
				} else if (event.getKeyCode() == 90 && event.isControlDown()) {
					undo();
				} else if (event.getKeyCode() == 89 && event.isControlDown()) {
					redo();
				}
			}

			public void keyReleased(KeyEvent event) {
			}

			public void keyTyped(KeyEvent event) {
			}

		});

		background.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				startPoint = new Point(event.getX(), event.getY());
				currentRect = new GRect(startPoint.x, startPoint.y, 0, 0);
				currentRect.setFilled(true);

				if (currentFigure == WALL_ID) {
					currentRect.setFillColor(Wall.WALL_COLOR);
					currentRect.setColor(Wall.WALL_COLOR);
				} else if (currentFigure == FINISH_ID) {
					currentRect.setFillColor(Finish.FINISH_COLOR);
					currentRect.setColor(Finish.FINISH_COLOR);
				} else if (currentFigure == PLAYER_ID) {
					currentRect.setFillColor(Player.PLAYER_COLOR);
					currentRect.setColor(Player.PLAYER_COLOR);
				}

				add(currentRect);
			}

			public void mouseReleased(MouseEvent event) {
				if (currentRect.getFillColor().equals(Player.PLAYER_COLOR)) {
					cache.stream().filter(rect -> rect.getFillColor().equals(Player.PLAYER_COLOR))
							.forEach(rect -> remove(rect));

				} else if (currentRect.getFillColor().equals(Finish.FINISH_COLOR)) {
					cache.stream().filter(rect -> rect.getFillColor().equals(Finish.FINISH_COLOR))
							.forEach(rect -> remove(rect));
				}

				while (cachePointer < cache.size() - 1) {
					cache.remove(cache.size() - 1);
				}

				cache.add(currentRect);
				cachePointer++;
			}
		});

		background.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent event) {
				colorLabel.setLocation(event.getX() + 10, event.getY() - 5);
				colorLabel.sendForward();
			}

			public void mouseDragged(MouseEvent event) {
				mouseMoved(event);
				int eventX = event.getX();
				int eventY = event.getY();

				if (eventX > getWidth()) {
					eventX = getWidth();
				} else if (eventX < 0) {
					eventX = 0;
				}

				if (eventY > getHeight()) {
					eventY = getHeight();
				} else if (eventY < 0) {
					eventY = 0;
				}

				double width = Math.abs(startPoint.x - eventX);
				double height = Math.abs(startPoint.y - eventY);

				if (currentFigure == PLAYER_ID) {
					width = Math.min(width, height);
					height = width;

				}

				double x = eventX < startPoint.x ? startPoint.x - width : startPoint.x;
				double y = eventY < startPoint.y ? startPoint.y - height : startPoint.y;

				currentRect.setLocation(x, y);
				currentRect.setSize(width, height);
			}
		});
	}

	private void save() {
		JFileChooser fileopen = new JFileChooser();
		fileopen.setFileFilter(mazeFileFilter);

		if (fileopen.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			File file = fileopen.getSelectedFile();
			String format = file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();

			if (!format.equals("maze")) {
				file = new File(file.getAbsolutePath() + ".maze");
			}

			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.write(getWidth() + " " + getHeight() + "\n");

				List<GRect> players = cache.subList(0, cachePointer + 1).stream()
						.filter(rect -> rect.getFillColor().equals(Player.PLAYER_COLOR)).collect(Collectors.toList());

				GRect player = players.get(players.size() - 1);
				writer.append((int) player.getX() + " " + (int) player.getY() + " "
						+ (int) (player.getX() + player.getWidth()) + " " + (int) (player.getY() + player.getHeight())
						+ "\n");

				List<GRect> finishes = cache.subList(0, cachePointer + 1).stream()
						.filter(rect -> rect.getFillColor().equals(Finish.FINISH_COLOR)).collect(Collectors.toList());

				GRect finish = finishes.get(finishes.size() - 1);
				writer.append((int) finish.getX() + " " + (int) finish.getY() + " "
						+ (int) (finish.getX() + finish.getWidth()) + " " + (int) (finish.getY() + finish.getHeight())
						+ "\n");

				List<GRect> walls = cache.subList(0, cachePointer).stream()
						.filter(rect -> rect.getFillColor().equals(Wall.WALL_COLOR)).collect(Collectors.toList());

				for (int i = 0; i < walls.size(); i++) {
					GRect wall = walls.get(i);
					writer.append((int) wall.getX() + " " + (int) wall.getY() + " "
							+ (int) (wall.getX() + wall.getWidth()) + " " + (int) (wall.getY() + wall.getHeight()));
					if (i != walls.size() - 1) {
						writer.append("\n");
					}
				}

				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static FileFilter mazeFileFilter = new FileFilter() {
		public boolean accept(File file) {
			String fileName = file.getName();
			String format = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			return file.isDirectory() || format.equals("maze");
		}

		public String getDescription() {
			return "maze model file (.maze)";
		}
	};

	private void undo() {
		if (cachePointer >= 0) {
			GRect rect = cache.get(cachePointer);
			if (rect.getFillColor().equals(Finish.FINISH_COLOR)) {
				List<GRect> finishes = cache.subList(0, cachePointer).stream()
						.filter(next -> next.getFillColor().equals(Finish.FINISH_COLOR)).collect(Collectors.toList());
				if (finishes.size() != 0) {
					add(finishes.get(finishes.size() - 1));
				}
			} else if (rect.getFillColor().equals(Player.PLAYER_COLOR)) {
				List<GRect> players = cache.subList(0, cachePointer).stream()
						.filter(next -> next.getFillColor().equals(Player.PLAYER_COLOR)).collect(Collectors.toList());
				if (players.size() != 0) {
					add(players.get(players.size() - 1));
				}
			}
			remove(cache.get(cachePointer--));
		}
	}

	private void redo() {
		if (cachePointer < cache.size() - 1) {
			GRect rect = cache.get(++cachePointer);
			if (rect.getFillColor().equals(Finish.FINISH_COLOR)) {
				List<GRect> finishes = cache.subList(0, cachePointer).stream()
						.filter(next -> next.getFillColor().equals(Finish.FINISH_COLOR)).collect(Collectors.toList());
				if (finishes.size() != 0) {
					remove(finishes.get(finishes.size() - 1));
				}
			} else if (rect.getFillColor().equals(Player.PLAYER_COLOR)) {
				List<GRect> players = cache.subList(0, cachePointer).stream()
						.filter(next -> next.getFillColor().equals(Player.PLAYER_COLOR)).collect(Collectors.toList());
				if (players.size() != 0) {
					remove(players.get(players.size() - 1));
				}
			}
			add(rect);
		}
	}

	private Point startPoint;
	private GRect currentRect;

	public static void main(String[] args) {
		Program program = new MazeFactory();
		program.start();
	}
}
