package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Label for sign in title
    	Label titleLabel = new Label("Sign in");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
    	// Input field for the user's username
        TextField userNameField = new TextField();
        userNameField.setPromptText("Username");
        
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
        userNameField.setStyle(fieldStyle);
        passwordField.setStyle(fieldStyle);
        visiblePasswordField.setStyle(fieldStyle);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        //Login button style
        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setStyle(
            "-fx-background-color: #0077B5; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20px; " +
            "-fx-padding: 12px;"
        );
        //Login button logic
        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
            	User user=new User(userName, password, "");
            	// Retrieve the user's role from the database using userName
            	String role = databaseHelper.getUserRole(userName);
            	
            	if(!role.isEmpty()) {
            		user.setRole(role);
            		if (databaseHelper.login(user)) {
            			// If the user has a one-time password direct to the UserPasswordResetPage
            			if (databaseHelper.needsNewPassword(userName)) {
            				new UserPasswordResetPage(databaseHelper).show(primaryStage, user);
            			}
                        // If the user has multiple roles direct to the RoleSelectionPage
            			else if (role.contains(",")) {
                            new RoleSelectionPage(databaseHelper).show(primaryStage, user);
                        } 
                        // Otherwise go directly to the appropriate home page
                        else {
                            if (role.trim().equalsIgnoreCase("admin")) {
                                new AdminHomePage(databaseHelper).show(primaryStage, user);
                            } else if (role.trim().equalsIgnoreCase("student")){
                                new StudentHomePage(databaseHelper).show(primaryStage, user);
                            } else {
                                new UserHomePage(databaseHelper).show(primaryStage);
                            }
                        }
                    }
            		else {
            			// Display an error if the login fails
                        errorLabel.setText("Error logging in");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("user account doesn't exists");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setStyle(
            "-fx-background-color: #E0E0E0; -fx-text-fill: #333; -fx-font-size: 14px; " +
            "-fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        cancelButton.setOnAction(a -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        
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
        contentCard.getChildren().addAll(titleLabel, userNameField, passwordContainer, loginButton, cancelButton, errorLabel);

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
