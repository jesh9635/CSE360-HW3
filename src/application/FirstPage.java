package application;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * FirstPage class represents the initial screen for the first user.
 * It prompts the user to set up administrator access and navigate to the setup screen.
 */
public class FirstPage {
	
	// Reference to the DatabaseHelper for database interactions
	private final DatabaseHelper databaseHelper;
	public FirstPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

	/**
     * Displays the first page in the provided primary stage. 
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	//VBox layout = new VBox(5);
    	
    	// Label to display the welcome message for the first user
	    Label userLabel = new Label("Hello! You are the first person here. Please select continue to setup administrator access.");
	    userLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        userLabel.setWrapText(true);
        userLabel.setTextAlignment(TextAlignment.CENTER);
        
	    // Button to navigate to the SetupAdmin page
	    Button continueButton = new Button("Continue");
        continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
	    continueButton.setOnAction(a -> {
	        new AdminSetupPage(databaseHelper).show(primaryStage);
	    });

	    // VBox for the white border around the content
        VBox contentCard = new VBox(15);
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setPadding(new Insets(40, 40, 40, 40));
        contentCard.setMaxWidth(400);
        contentCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        contentCard.getChildren().addAll(userLabel, continueButton);
        
        // StackPane for the grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));

        // Set the scene to primary stage
	    Scene firstPageScene = new Scene(rootLayout, 800, 600);
	    primaryStage.setScene(firstPageScene);
	    primaryStage.setTitle("First Page");
    	primaryStage.show();
    }
}
