package maze;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

	private List<MazeReader> levels = new ArrayList<>();

	private int pointer = 0;

	public LevelManager(List<String> fileNames) throws FileNotFoundException {
		for (String fileName : fileNames) {
			MazeReader reader = new MazeReader(new File(fileName));
			levels.add(reader);
		}
	}

	public MazeReader restart() {
		pointer = 0;
		return levels.get(0);
	}

	public MazeReader nextLevel() {
		if (pointer < levels.size() - 1) {
			return levels.get(++pointer);
		} else {
			return null;
		}
	}
	
	public int getIndex() {
		return pointer;
	}

	public boolean isCurrentLevelLast() {
		return pointer == levels.size() - 1;
	}

}
