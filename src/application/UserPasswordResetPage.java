package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserPasswordResetPage {

	private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public UserPasswordResetPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     * @param user The user whose information is to be used.
     */
    public void show(Stage primaryStage, User user) {
    	// Label for sign in title
    	Label titleLabel = new Label("Create a new password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Password field for the user's password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        // Text field that will be used for show password
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        
        // Show/Hide Password Button
        ToggleButton showPasswordButton = new ToggleButton("Show");
        showPasswordButton.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #0077B5; -fx-font-weight: bold; -fx-cursor: hand;"
        );
        
        // Password Field Container
        StackPane passwordContainer = new StackPane();
        passwordContainer.getChildren().addAll(passwordField, visiblePasswordField, showPasswordButton);
        StackPane.setAlignment(showPasswordButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(showPasswordButton, new Insets(0, 10, 0, 0));
        
        //Sets password to hide initially
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);
        
        // Set visibility when button pressed
        showPasswordButton.setOnAction(e -> {
            if (showPasswordButton.isSelected()) {
                passwordField.setVisible(false);
                visiblePasswordField.setVisible(true);
                showPasswordButton.setText("Hide");
            } else {
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
                showPasswordButton.setText("Show");
            }
        });
        
        // Input field style
        String fieldStyle = "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-padding: 10px;";
        passwordField.setStyle(fieldStyle);
        visiblePasswordField.setStyle(fieldStyle);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Submit Password button style
        Button submitPasswordButton = new Button("Change Password");
        submitPasswordButton.setMaxWidth(Double.MAX_VALUE);
        submitPasswordButton.setStyle(
            "-fx-background-color: #0077B5; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20px; " +
            "-fx-padding: 12px;"
        );
        
        // Submit Password button logic
        submitPasswordButton.setOnAction(a -> {
        	String userName = user.getUserName();
        	String password = passwordField.getText();
        	// TBD: needs to check for username and password validation here
			if(!UserNameRecognizer.isValid(userName)) {
				errorLabel.setText(UserNameRecognizer.userNameRecognizerErrorMessage);
				return; 
			}
			if(!PasswordEvaluator.isValid(password)) {
				errorLabel.setText(PasswordEvaluator.passwordErrorMessage);
				return;
			}
        	try {
        	databaseHelper.setPassword(userName, password);
        	} catch (SQLException e) {
        		System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
        	}
        	new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
        // VBox for the white border containing the content
        VBox contentCard = new VBox(15);
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setPadding(new Insets(40, 40, 40, 40));
        contentCard.setMaxWidth(400);
        contentCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
        contentCard.getChildren().addAll(titleLabel, passwordContainer, errorLabel, submitPasswordButton);

        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));

        //Scene and primaryStage
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}
