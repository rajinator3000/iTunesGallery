/**
 * Created by Raj Patel
 * rdp06976@uga.edu
 */

package cs1302.gallery;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.scene.control.Separator;
import java.io.InputStreamReader;
import java.net.URL;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import javafx.stage.Modality;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import com.google.gson.*;

public class GalleryApp extends Application {

    private TilePane tilePane = new TilePane();
    private ArrayList<String> output = new ArrayList<>();
    private ArrayList<String> used = new ArrayList<>();
    private ArrayList<String> onBoard = new ArrayList<>();
    private boolean isPlaying = true;
    private int counter = 0;
    private Timeline timeline = new Timeline();


    @Override
    /**
     * Adds things into the stage so that they are in
     * the stage when the user starts the program.
     */
    public void start(Stage stage) {

        stage.setTitle("Gallery!");
        stage.setMaxWidth(500);
        stage.setMaxHeight(510);
        stage.sizeToScene();
        stage.show();

        VBox pane = new VBox();


        Menu fileMenu = new Menu("File");   //these are all menus that pop up at the top and have drop down bars that do different things
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(event -> Platform.exit());
        fileMenu.getItems().add(exitItem);

        Menu theme = new Menu("Theme");
        MenuItem newThemes = new MenuItem("New theme 1");
        newThemes.setOnAction(event -> pane.setStyle("-fx-background-color: linear-gradient(to bottom right, antiquewhite, dodgerblue);"));
        MenuItem newThemes2 = new MenuItem("New theme 2");
        newThemes2.setOnAction(event -> pane.setStyle("-fx-background-color: linear-gradient(to bottom right, orangered, paleturquoise);"));
        theme.getItems().addAll(newThemes, newThemes2);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(event -> aboutMe());
        helpMenu.getItems().add(aboutItem);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, theme, helpMenu);




        HBox secondLayer = new HBox();          // play pause that works, the update images that works
        secondLayer.setStyle("-fx-spacing: 7px; -fx-padding: 7px;");



        Button play = new Button("Pause");  //makes a play/pause button that changes based on the user
        play.setOnAction(event -> {
            if(play.getText().equals("Pause"))
            {
                play.setText("Play");
                isPlaying = true;
//                slideShow();
            }
            else if(play.getText().equals("Play"))
            {
                play.setText("Pause");
                isPlaying = false;
//                slideShow();
            }
        });



        Label label = new Label("Search Query: ");  //search bar for user
        label.setStyle("-fx-padding: 5px");
        Button update = new Button("Update Images");
        TextField textField = new TextField("Rock");


        Separator s = new Separator();
        s.setOrientation(Orientation.VERTICAL);
        secondLayer.getChildren().addAll(play, s, label, textField, update);
        pane.getChildren().addAll(menuBar, secondLayer);

        urls(textField);
        setImages(); //uses this to start the program with images

        update.setOnAction(event -> {
            urls(textField);
            setImages();
        });

        pane.getChildren().addAll(tilePane);


        HBox bottom = new HBox();
        ProgressBar progressBar = new ProgressBar();
        Label copyRight = new Label(" Images provided courtesy of iTunes"); //this is the copyright and above is the progress bar
        bottom.getChildren().addAll(progressBar, copyRight);
        pane.getChildren().addAll(bottom);

        Scene scene = new Scene(pane);
        stage.setScene(scene);





    } // start


    /**
     * This method will take the text from the search bar and
     * split it up so it us a valid itunes url
     * @param txt the text that is inputted by the user
     */
    public void urls(TextField txt)
    {
        try
        {
            ArrayList<String> tempOutput = new ArrayList<>();
            ArrayList<String> tempUsed = new ArrayList<>();
            ArrayList<String> tempOnBoard = new ArrayList<>();

            tempOutput.addAll(output);
            tempUsed.addAll(used);
            tempOnBoard.addAll(onBoard);


            String sUrl = "https://itunes.apple.com/search?term=";
            String search = txt.getText();
            search = search.replaceAll(" ", "+");
            sUrl = sUrl + search + "&entity=album";

            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream()); //gets the actual url

            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(reader);

            JsonObject root = je.getAsJsonObject();                      // root of response
            JsonArray results = root.getAsJsonArray("results");          // "results" array
            int numResults = results.size();                             // "results" array size
            if(numResults >= 20) {
                output.clear();
                used.clear();
                onBoard.clear();
                for (int i = 0; i < numResults; i++) {
                    JsonObject result = results.get(i).getAsJsonObject();    // object i in array
                    JsonElement artworkUrl100 = result.get("artworkUrl100"); // artworkUrl100 member
                    if (artworkUrl100 != null) {                             // member might not exist
                        String artUrl = artworkUrl100.getAsString();        // get member as string
                        output.add(artUrl); //saves all of the urls in this arrayList
                    } // if
                } // for{
            }
        }
        catch (Exception e) //used for any exception
        {
            System.out.println("Error!" + e);
        }
    }


    /**
     * This method will take the urls and make them
     * show up to the user in the gui window as
     * cover art.
     */
    public void setImages()
    {
        int count = 0;

        if(output.size() < 20) //makes sure 20 cover arts
        {
            error();
        }
        else {
            if(tilePane.getChildren().size() == 20)
            {
                for(int i = 0; i < tilePane.getChildren().size(); i++) //clears tilepane for new entries
                {
                    tilePane.getChildren().clear();
                }
                tilePane.getChildren().clear();
            }
            while (count < 20) {

                if(output.size() == 0)
                {
                    output = used;
                    used.clear();
                }

                int size = output.size(); //adds new images and removes old ones so they arent reused
                int random = (int)(Math.random() * size);
                Image image = new Image(output.get(random));

                onBoard.add(output.get(random));
                output.remove(random);

                ImageView iView = new ImageView(image);
                tilePane.getChildren().add(iView);
                count++;
            }
            slideShow();
        }
    }

    /**
     * This method is used to just change one method on the
     * tilepane so that it can update every 2 seconds.
     */
    public void setOneImage()
    {
        int random = (int)(Math.random() * output.size() - 1);
        int random2 = (int)(Math.random() * 20);
        tilePane.getChildren().remove(random2); //removes the old art
        Image image = new Image(output.get(random)); //gets one image from the list to add randomly
        ImageView iView = new ImageView(image);

        used.add(onBoard.get(random2));
        onBoard.remove(random2);

        tilePane.getChildren().add(random2, iView);

        onBoard.add(random2, output.get(random)); //adds the new art
        output.remove(random);

        if(output.size() == 0)
        {
            output.addAll(used);
            used.clear();
        }
    }

    /**
     * This method makes the images swtich
     * every 2 seconds to the user in the gui.
     */
    public void slideShow()
    {
            EventHandler<ActionEvent> handler = event -> setOneImage();
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
//            Timeline timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(keyFrame);

            if(isPlaying) { //if the button is play, it will go here

                timeline.play();
            }
            else if(!isPlaying)
            {
                timeline.pause();
            }
    }


    /**
     * This method is used to make a new window
     * for an error that there are less than 20
     * images present to find album work for.
     */
    public void error()
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Error!");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText("There are less than 20 images, so the cover arts can not be updated!");

        Button button = new Button("Close");
        button.setOnAction(event -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);

        window.showAndWait();


    }


    /**
     * Makes a pop up window that gives some basic
     * information about myself along with a picture of me.
     */
    public void aboutMe()
    {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("About RAJ-PATEL!");
        window.setMinWidth(250);

        ImageView me = new ImageView(new Image("http://i64.tinypic.com/4vofi8.jpg"));
        me.setFitHeight(200);
        me.setFitWidth(200);

        Label name = new Label("Raj Patel");

        Label email = new Label("rdp06976@uga.edu");

        Label versionNumber = new Label("Version: 1.0.0");

        Button button = new Button("Close");
        button.setOnAction(event -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(me, name, email, versionNumber, button);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);

        window.showAndWait();
    }

    public static void main(String[] args) {
	try {
	    Application.launch(args);
	} catch (UnsupportedOperationException e) {
	    System.out.println(e);
	    System.err.println("If this is a DISPLAY problem, then your X server connection");
	    System.err.println("has likely timed out. This can generally be fixed by logging");
	    System.err.println("out and logging back in.");
	    System.exit(1);
	} // try
    } // main

} // GalleryApp
