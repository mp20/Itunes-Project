package cs1302.gallery;

import javafx.scene.layout.Priority;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Orientation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.util.StringTokenizer;
import java.io.IOException;
import java.net.MalformedURLException;
import javafx.scene.layout.TilePane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.lang.ArrayIndexOutOfBoundsException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import java.time.LocalTime;
import javafx.util.Duration;
import java.util.Random;
import javafx.scene.control.ProgressBar;

/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    private VBox root = new VBox();
    //Menue instance variables
    private BorderPane pain;
    private HBox menuBox;
    private BorderPane layout;
    private MenuItem exit = new MenuItem("Exit");
    private MenuItem about = new MenuItem("About");
    //ToolBar varibales
    private Alert alertDialog;
    private HBox toolBarContainer;
    private Text searchText;
    private Button pauseButton;
    private TextField urlField;
    private Button updateButton;
    private Separator sep;
    private String query;
    private GalleryBackEnd backEnd = new GalleryBackEnd();
    //Main window instance varibales/constants
    private static final String DEFAULT_QUERY =
        "https://itunes.apple.com/search?term=youngThug&limit=100";
    private HBox mainContainer = new HBox();
    private TilePane tile;
    private ImageView imgView;
    private Image def;
    private Image img;
    private String[] urlList;
    private String[] reserve;
    private boolean paused = false;
    private int counter = 1;
    private Timeline timeline;
    private boolean error = false;

    /** {@inheritDoc}
     * Calls all the methods needed to display
     * the application as well as start the timeline.*/
    @Override
    public void init() {
        createMenu();
        createToolBar();
        loadDefault();

        GalleryBackEnd backend = new GalleryBackEnd();

        timeline = new Timeline();

        EventHandler<ActionEvent> handler = event -> {
            if (counter == 1) {
                copyArray();
            }
            swapper();
        };

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();

        //controls the buttons, the eventhandler
        pauseButton.setOnAction(event -> buttonControlHandler());

        //Exit event handler
        exit.setOnAction(event -> Platform.exit());
        //Creates the about me page
        about.setOnAction(event -> backend.aboutMe());
        //UpdateButton even handler
        updateButton.setOnAction(event -> getTextHandler());

        //root
        root.getChildren().addAll(pain,toolBarContainer, mainContainer);
    } //init

    /** {@inheritDoc}
     * Sets up the scene and stage.
     */
    @Override
    public void start(Stage stage) {
        //Set up scene
        Scene scene = new Scene(root);
        stage.setMaxWidth(640);
        stage.setMaxHeight(720);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    } // start

    /**
     * This method is responsible for creating and
     * putting together the Nodes for the menue.
     */
    public void createMenu() {
        MenuBar menBar = new MenuBar();
        Menu File = new Menu("File");
        File.getItems().add(exit);
        MenuBar rightBar = new MenuBar();
        Menu Help = new Menu("Help");
        Help.getItems().add(about);
        menBar.getMenus().addAll(File);
        rightBar.getMenus().addAll(Help);
        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        HBox.setHgrow(spacer, Priority.SOMETIMES);
        menuBox = new HBox(menBar, spacer, rightBar);
        pain = new BorderPane();
        pain.setTop(menuBox);
    } //createMenu

    /**
     * This method is responsible for creating and
     * putting together the Nodes for the tool bar.
     */
    public void createToolBar() {
        toolBarContainer = new HBox(10);
        searchText = new Text("Search Query:");
        pauseButton = new Button("Pause");
        urlField = new TextField("young thug");
        updateButton = new Button("Update Images");
        sep = new Separator(Orientation.VERTICAL);
        toolBarContainer.setHgrow(urlField, Priority.ALWAYS);
        toolBarContainer.setAlignment(Pos.CENTER_LEFT);
        toolBarContainer.setPadding(new Insets(7));
        toolBarContainer.getChildren().addAll(pauseButton, sep, searchText, urlField, updateButton);
    } //createToolBar

    /**
     * This method is responsible for loading the default query.
     */
    public void loadDefault() {
        backEnd.jsonDownloader(DEFAULT_QUERY);
        urlList = backEnd.getUrlList();
        tile = new TilePane();
        tile.setPrefColumns(5);
        for (int i = 0; i < 20; i++) {
            img = new Image(urlList[i],100,100,false,false);
            tile.getChildren().addAll(new ImageView(img));
        }
        mainContainer.getChildren().add(tile);
    } //loadDefault

    /**
     * This method is responsible for loading any query after
     * the default query.
     */
    public void loadAll() {
        //loads images into the viewer and puts it into the container
        try {
            error = false;
            tile = new TilePane();
            tile.setPrefColumns(5);
            for (int i = 0; i < 20; i++) {
                img = new Image(urlList[i],100,100,false,false);
                ImageView newOne = new ImageView(img);
                Platform.runLater(() -> tile.getChildren().addAll(newOne));
            }
            Platform.runLater(() -> {
                mainContainer.getChildren().clear();
                mainContainer.getChildren().add(tile);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            alert(e);
        }
    }

    /**
     * This method is responsible for handling the errors that might
     * come up when trying to acess the query. It will stop timeline and
     * adjust the scene accordingly.
     * @param cause what caused the exception (The type)
     */
    public void alert(Exception cause) {
        //condition true says to not make a reserve since the list had an issue
        error = true;
        Platform.runLater(() -> {
            pauseButton.setText("Play");
            pauseButton.setDisable(true);
            timeline.pause();
            alertDialog = new Alert(AlertType.ERROR);
        });
        TextArea errorText = new TextArea("Please try a different query:" +
             " Main content could not be updated");
        Platform.runLater(() -> {
            alertDialog.getDialogPane().setContent(errorText);
            alertDialog.setResizable(true);
            alertDialog.showAndWait();
        });
    }

    /**
     * This method is responsible for handling the reserved array.
     * the reserved array hold all the exess images that did not originally fit into the
     * screen.
     */
    public void copyArray() {
        try {
            reserve = new String[urlList.length - 20];
            for (int i = 20, j = 0; i < urlList.length; i++, j++) {
                reserve[j] = urlList[i];
            }
        } catch (Exception e) {
            error = true;
        }
    } //copyArray

    /**
     * This method is responsible for handling the swapping that takes place every
     * two seconds. It holds onto the images that did not originally fit into the screen
     * and it rotates back and forth between the ones and display and the ones on this list.
     */
    public void swapper() {
        if (reserve.length > 0) {
            counter = 2;
            Random generator = new Random();
            //holds the random index for an image in display to be swapped
            int randomMain = generator.nextInt(20);
            //hold the random index for an image in our reserves to replace it
            int randomReserve = generator.nextInt(reserve.length);
            //swaps them out and reloads the display
            String temp = urlList[randomMain];
            urlList[randomMain] = reserve[randomReserve];
            reserve[randomReserve] = temp;
            loadAll();
        }
    }

    /**
     * This method is responsible for handling the pause and play actions.
     * depending on the current state of the button when pressed
     *  the timeline and button text will be changed.
     */
    public void buttonControlHandler() {
        if (pauseButton.getText().equals("Pause")) {
            timeline.pause();
            pauseButton.setText("Play");
        } else if (pauseButton.getText().equals("Play")) {
            timeline.play();
            pauseButton.setText("Pause");
        }
    } //buttonControlHandler


    /**
     * This method is responsible for handling the update button.
     * it retrives the quarries and calls loadAll() which then puts it onto
     * display.
     */
    public void getTextHandler() {
        Runnable task = () -> {
            //downloads the json and fills
            query = backEnd.tokenize(urlField.getText());
            query = "https://itunes.apple.com/search?term=" + query + "&limit=100";
            backEnd.jsonDownloader(query);
            urlList = backEnd.getUrlList();
            //load the images
            loadAll();
            //prevents previous reserves from flooding into new search
            if (error == false) {
                copyArray();
                //if new query has no issues reenable
                if (pauseButton.isDisable()) {
                    pauseButton.setDisable(false);
                }
            }
        };
        runNow(task);
    } //getTextHandler

    /**
     * This method is responsible for handling the threads that will be created.
     * @param target target is a runnable passed in to create the thread.
     */
    public static void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(false);
        t.start();
    } // runNow

} // GalleryApp
