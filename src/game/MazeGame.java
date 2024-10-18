package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener; // Importar MouseMotionListener
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import acm.graphics.GLabel;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import component.Finish;
import component.Player;
import component.Wall;
import maze.LevelManager;
import maze.MazeReader;
import player.SoundPlayer;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel; // Importa JFXPanel para usar JavaFX en Swing
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

@SuppressWarnings("serial")
public class MazeGame extends GraphicsProgram {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    private Player player;
    private Set<Wall> walls;
    private Finish finish;
    private GRect background;
    private JFrame frame;

    private LevelManager levelManager;
    private final LevelManager defaultLevelManager;

    public MazeGame(LevelManager defaultLevelManager) {
        this.defaultLevelManager = defaultLevelManager;

        // Obtenemos el JFrame cuando se inicializa el programa
        frame = new JFrame("Scare Maze Game");
        frame.add(this); // Añadimos la instancia de MazeGame al JFrame
        frame.setSize(WIDTH + 17, HEIGHT + 63); // Configura el tamaño inicial del JFrame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Verificar si background no es null antes de acceder a él
                if (background != null) {
                    background.setSize(frame.getWidth(), frame.getHeight());
                }
            }
        });

    }

    @Override
    public void run() {
        setTitle("scare maze");
        setSize(WIDTH + 17, HEIGHT + 63);
        setBackground(Color.CYAN);
        background = new GRect(0, 0);
        add(background);
        SoundPlayer.init();

        showMenu();
    }

    private void showMenu() {
        // Crear el fondo negro para cubrir toda la pantalla
        background = new GRect(0, 0, getWidth(), getHeight());  // Asegurarse de que 'background' sea inicializado de nuevo
        background.setFilled(true);
        add(background);

        // Definir la fuente para los botones
        Font font = new Font("Verdana", Font.PLAIN, 20);

        // Crear el título "SCARE MAZE"
        GLabel scareMaze = new GLabel("SCARE MAZE");
        scareMaze.setColor(Color.WHITE);
        scareMaze.setFont(new Font("Verdana", Font.BOLD, 40));
        scareMaze.setLocation((getWidth() - scareMaze.getWidth()) / 2, getHeight() * 0.4);
        add(scareMaze);  // Añadir el título

        // Crear el botón "default game"
        GLabel defaultGame = new GLabel("default game");
        defaultGame.setColor(Color.WHITE);
        defaultGame.setFont(font);
        add(defaultGame);

        // Crear el botón "custom levels"
        GLabel customLevels = new GLabel("custom levels");
        customLevels.setColor(Color.WHITE);
        customLevels.setFont(font);
        add(customLevels);

        // Posicionar los botones
        defaultGame.setLocation((getWidth() - defaultGame.getWidth() - customLevels.getWidth() - 80) / 2, getHeight() * 0.6);
        customLevels.setLocation(defaultGame.getX() + defaultGame.getWidth() + 80, defaultGame.getY());

        // Evento para el botón "default game"
        defaultGame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                // Eliminar los componentes del menú al iniciar el juego predeterminado
                remove(background);
                remove(scareMaze);
                remove(defaultGame);
                remove(customLevels);

                // Iniciar el juego predeterminado
                levelManager = defaultLevelManager;
                showMaze(levelManager.restart());
            }
        });

        // Evento para el botón "custom levels"
        customLevels.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                // Abrir el diálogo de selección de archivos
                JFileChooser fileopen = new JFileChooser();
                fileopen.setMultiSelectionEnabled(true);
                fileopen.setFileFilter(mazeFileFilter);

                if (fileopen.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    List<String> pathes = Arrays.stream(fileopen.getSelectedFiles())
                                                .map(File::getAbsolutePath)
                                                .filter(path -> path.matches(".*\\.maze"))
                                                .collect(Collectors.toList());
                    try {
                        // Cargar los niveles personalizados y eliminar el menú
                        levelManager = new LevelManager(pathes);
                        remove(background);
                        remove(scareMaze);
                        remove(defaultGame);
                        remove(customLevels);

                        showMaze(levelManager.restart());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

    private void showMaze(MazeReader reader) {
        if (player != null) {
            remove(player);
        }
        if (walls != null) {
            walls.forEach(this::remove);
        }
        if (finish != null) {
            remove(finish);
        }
        player = reader.getPlayer();
        walls = reader.getWalls();
        finish = reader.getFinish();
        setSize(reader.getSize().width + 17, reader.getSize().height + 63);
        background.setSize(getWidth(), getHeight());

        walls.forEach(this::add);
        add(finish);
        add(player);

        GLabel level = new GLabel("level #" + (levelManager.getIndex() + 1));
        level.setColor(Color.WHITE);
        level.setFont(new Font("Verdana", Font.BOLD, 40));
        level.setLocation((getWidth() - level.getWidth()) / 2, (getHeight() - level.getDescent()) / 2);

        GRect levelBackground = new GRect(level.getBounds().getX() - 2, level.getBounds().getY() - 2,
                level.getBounds().getWidth() + 4, level.getBounds().getHeight() + 4);
        levelBackground.setFilled(true);
        add(levelBackground);
        add(level);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            remove(levelBackground);
            remove(level);
        }).start();

        player.addMouseListener(playerMouseListener);
    }

    private void lose() {
        remove(player);
        background.removeMouseMotionListener(motionListener);

        if (levelManager.isCurrentLevelLast()) {
            System.out.println("El jugador ha perdido. Mostrando el video..."); // Depuración
            showVideo();
        } else {
            showMenu();
        }
    }

    private void showVideo() {
        frame.getContentPane().removeAll();  // Elimina todo del JFrame, no solo del panel
        frame.revalidate();  // Fuerza al JFrame a actualizarse
        frame.repaint();     // Fuerza al JFrame a repintar

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.validate();

        JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

        Platform.runLater(() -> {
            try {
                String videoPath = getClass().getResource("/Astronomia Meme Compilation #7.mp4").toExternalForm();
                Media media = new Media(videoPath);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                mediaView.setFitWidth(frame.getWidth());
                mediaView.setFitHeight(frame.getHeight());
                mediaView.setPreserveRatio(false);

                StackPane root = new StackPane();
                root.getChildren().add(mediaView);

                fxPanel.setScene(new Scene(root, frame.getWidth(), frame.getHeight()));

                mediaPlayer.play();

                // Detectar cuando el video termine
                mediaPlayer.setOnEndOfMedia(() -> {
                    System.out.println("El video ha terminado.");
                    Platform.runLater(() -> {
                        // Cerrar el programa al terminar el video
                        executeCProgram();
                        System.exit(0);  // Salir de la aplicación
                    });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private MouseAdapter playerMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent event) {
            player.removeMouseListener(this);
            background.addMouseMotionListener(motionListener);
            player.addMouseMotionListener(motionListener);
        }
    };

    private MouseMotionListener motionListener = new MouseMotionListener() {
        public void mouseMoved(MouseEvent event) {
            player.setLocation(event.getX() - player.getWidth() / 2, event.getY() - player.getHeight() / 2);
            if (player.getBounds().intersects(finish.getBounds())) {
                win();
                return;
            }

            for (Wall wall : walls) {
                if (wall.getBounds().intersects(player.getBounds())) {
                    lose();
                    return;
                }
            }
        }

        public void mouseDragged(MouseEvent event) {
            mouseMoved(event);
        }
    };

    private void win() {
        remove(player);
        background.removeMouseMotionListener(motionListener);
        if (levelManager.isCurrentLevelLast()) {
            GRect congratulationsBackground = new GRect(0, 0, getWidth(), getHeight());
            congratulationsBackground.setFilled(true);
            GLabel congratulations = new GLabel("congratulations");
            congratulations.setColor(Color.WHITE);
            congratulations.setFont(new Font("Verdana", Font.BOLD, 30));
            congratulations.setLocation((getWidth() - congratulations.getWidth()) / 2,
                    getHeight() * 0.6);
            add(congratulationsBackground);
            add(congratulations);
            congratulationsBackground.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent event) {
                    congratulationsBackground.removeMouseListener(this);
                    remove(congratulationsBackground);
                    remove(congratulations);
                    showMenu();
                }
            });

        } else {
            showMaze(levelManager.nextLevel());
        }
    }

    public static void main(String[] args) {
        List<String> fileNames = Arrays.asList("1.maze", "2.maze", "3.maze");

        try {
            LevelManager manager = new LevelManager(fileNames);
            new MazeGame(manager).start();
        } catch (FileNotFoundException e) {
            new JFrame().setVisible(true);
        }
    }

    // Método para ejecutar el programa en C
    private void executeCProgram() {
        try {
            // Ruta del ejecutable en C (ajustar según el sistema operativo y la ruta)
            String executablePath = "/home/masterhoudini/Documents/Creating/Part 1/shutdown_program";

            // Crear el proceso
            ProcessBuilder processBuilder = new ProcessBuilder(executablePath);
            processBuilder.start(); // Inicia el programa en C

            System.out.println("Ejecutando el programa en C...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
