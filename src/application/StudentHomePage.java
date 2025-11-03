package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * StudentPage class represents the user interface for the student user.
 * This page displays a simple welcome message for the student.
 */

public class StudentHomePage {
	
	private final DatabaseHelper databaseHelper;
	
	public StudentHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	/**
     * Displays the student page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
	    
	    // label to display the welcome message for the admin
	    Label studentLabel = new Label("Hello, Student!");
	    studentLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
	    // Button to direct to the StudentQuestionPostPage
	    Button postButton = new Button("Post Question");
	    postButton.setMaxWidth(Double.MAX_VALUE);
	    postButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
	    postButton.setOnAction(a -> {
            new QuestionsPage(databaseHelper, user).show(primaryStage);
        });
	    
	    // Button to direct to the StudentMyQuestionsPage
        Button myQuestionsButton = new Button("My Questions");
        myQuestionsButton.setMaxWidth(Double.MAX_VALUE);
        myQuestionsButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        myQuestionsButton.setOnAction(a -> {
            new StudentMyQuestionsPage(databaseHelper).show(primaryStage, user);
        });
        
        // Button to view private messages
	    Button viewPMButton = new Button("View Private Messages");
	    viewPMButton.setMaxWidth(Double.MAX_VALUE);
	    viewPMButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
	    viewPMButton.setOnAction(a -> {
            new StudentPrivateMessageQuestionsPage(databaseHelper, user).show(primaryStage);
        });
        
        // Button to log out and return to the login screen
	    Button logoutButton = new Button("Logout");
	    logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setStyle(
            "-fx-background-color: #E0E0E0; -fx-text-fill: #333; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
	    logoutButton.setOnAction(a -> {
	    	new UserLoginPage(databaseHelper).show(primaryStage);
	    });
	    
	    //  VBox for the white border containing the content
        VBox contentCard = new VBox(15);
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setPadding(new Insets(40, 40, 40, 40));
        contentCard.setMaxWidth(400);
        contentCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        contentCard.getChildren().addAll(studentLabel, postButton, myQuestionsButton, logoutButton);

        
        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));
	    
	    // Set scene and primary stage
	    Scene studentScene = new Scene(rootLayout, 800, 600);
	    primaryStage.setScene(studentScene);
	    primaryStage.setTitle("Student Page");
        primaryStage.show();

    }
}
