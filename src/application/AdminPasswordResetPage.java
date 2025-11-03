package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AdminPasswordResetPage {

    private final DatabaseHelper databaseHelper;

    public AdminPasswordResetPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User currentAdmin) {
    	 // Label to display the title 
        Label titleLabel = new Label("Reset User Password");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Input field for username
        Label instructionLabel = new Label("Enter the username to reset the password for:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        String fieldStyle = "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-padding: 10px;";
        usernameField.setStyle(fieldStyle);
       
        // Label to display the generated temporary password, similar to the invitation page
        Label tempPasswordLabel = new Label("");
        tempPasswordLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0077B5;");
        
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");
        
        Button resetButton = new Button("Reset Password");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        resetButton.setOnAction(e -> {
            String userNameToReset = usernameField.getText().trim();

            if (userNameToReset.isEmpty()) {
                statusLabel.setText("Please enter a username.");
                return;
            }

            if (!databaseHelper.doesUserExist(userNameToReset)) {
                statusLabel.setText("User not found.");
                return;
            }

            try {
                String tempPassword = databaseHelper.getTempPassword(userNameToReset);
                // Display the new password directly on the page
                tempPasswordLabel.setText("Temporary Password: " + tempPassword);
                usernameField.clear(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                statusLabel.setText("An error occurred during password reset.");
            }
        });
        
        // Button to return to AdminHomePage
        Button backButton = new Button("Back to Home Page");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle(
            "-fx-background-color: #E0E0E0; -fx-text-fill: #333; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        backButton.setOnAction(e -> {
            new AdminHomePage(databaseHelper).show(primaryStage, currentAdmin);
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
        contentCard.getChildren().addAll(titleLabel, instructionLabel, usernameField, tempPasswordLabel, resetButton, backButton, statusLabel);

        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));
        
        // Set scene and primary stage
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Reset User Password");
        primaryStage.show();
    }
}
