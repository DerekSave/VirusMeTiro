import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoTest extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Especifica la ruta del video (ajustada con los espacios y caracteres especiales)
        String videoPath = "file:///C:/Users/herna/Downloads/scary-maze-game-master/scary-maze-game-master/Astronomia%20Meme%20Compilation%20%237.mp4";

        // Configura la reproducción de video usando JavaFX
        Media media = new Media(videoPath);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Configura la ventana para mostrar el video
        StackPane root = new StackPane();
        root.getChildren().add(mediaView);

        Scene scene = new Scene(root, 800, 600);  // Tamaño de la ventana

        // Configura el Stage para mostrar el video
        primaryStage.setTitle("Video Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Inicia la reproducción del video
        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

