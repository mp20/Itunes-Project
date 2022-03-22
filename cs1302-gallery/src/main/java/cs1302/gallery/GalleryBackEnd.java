package cs1302.gallery;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.geometry.Orientation;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Arrays;

/**
 * This class handles the backend such as json downloading, tokenizing,
 * etc.
 */
public class GalleryBackEnd {

    private InputStreamReader reader;
    private String imgUrl;
    private String[] list;

    /**
     * Takes in a query for downloading the json from Itunes API and manipulating it.
     * @param query takes in a query and downloads the json from itunes API.
     */
    public void jsonDownloader(String query) {
        try {
            String sUrl = query;
            URL url = new URL(sUrl);
            reader = new InputStreamReader(url.openStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        JsonElement je = JsonParser.parseReader(reader);
        JsonObject root = je.getAsJsonObject();
        JsonArray results = root.getAsJsonArray("results");
        int numResults = results.size();
        //hold our query urls
        String[]  urlList = new String[numResults];
        //fills in the array with the image urls
        for (int i = 0; i < numResults; i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            JsonElement artWork = result.get("artworkUrl100");
            if (artWork == null) {
                continue;
            }
            imgUrl = artWork.toString().replaceAll("\"", "");
            urlList[i] = imgUrl;
        }
        //gets rid of duplicates
        urlList = Arrays.stream(urlList).distinct().toArray(String[]::new);

        //copy urlList array urls to list array
        list = urlList.clone();
    } //jsonDownloader

    /**
     * This method is responsible for tokenizing a query.
     * @param query takes in a query from the user.
     * @return returns the completed query (proper syntax).
     */
    public String tokenize(String query) {
        //formats the query from the user to one acceptable by itunes
        StringTokenizer string = new StringTokenizer(query);
        String completeQuery = "";
        int counter = string.countTokens();
        for (int i = 0; i < counter; i++) {
            completeQuery += string.nextToken();
            if (string.hasMoreTokens()) {
                completeQuery += "+";
            }
        }
        return completeQuery;
    } //tokenize

    /**
     * This method is responsible for creating the about me page on the
     * help menue.
     */
    public void aboutMe() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("ALL ABOUT ME");
        Label label1 = new Label("Pop up window now displayed");
        VBox layout = new VBox(10);
        Label myName = new Label("Ariya Nazari Foroshani");
        Label version = new Label("Version 1.0");
        Label Email = new Label("ariyanazari1380@gmail.com");
        Image img = new Image("file:resources/takeoff.png", 400, 400, false, true);
        ImageView viewer = new ImageView(img);
        layout.getChildren().addAll(myName,version, Email, viewer);
        Scene scene1 = new Scene(layout, 500, 500);
        window.setScene(scene1);
        window.showAndWait();
    }

    /**
     * This method is a getter responsible for get the array of url Strings.
     * @return list returns a list of the urls for each image stores in an array
     * of strings.
     */
    //comment
    public String[] getUrlList() {
        return list;
    }
}
