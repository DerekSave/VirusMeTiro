package player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class SoundPlayer {

	private static AdvancedPlayer player;

	public static void init() {
		try {
			player = new AdvancedPlayer(new FileInputStream("scream.mp3"));
		} catch (FileNotFoundException | JavaLayerException e) {
			e.printStackTrace();
		}
	}

	public static void playScream() {
		new Thread(() -> {
			try {
				player.play();
				init();
			} catch (JavaLayerException e) {
				//e.printStackTrace();
			}
		}).start();
	}
}