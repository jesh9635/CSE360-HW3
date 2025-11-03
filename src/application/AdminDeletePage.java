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
 * AdminDeletePage allows an admin to delete a user by username, except themselves.
 */
public class AdminDeletePage {

    private final DatabaseHelper databaseHelper;

    public AdminDeletePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the delete user page.
     *
     * @param primaryStage The main application stage.
     * @param currentAdmin The currently logged-in admin user.
     */
    public void show(Stage primaryStage, User currentAdmin) {
       // Label to display the title of the page
        Label titleLabel = new Label("Delete a User");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Add a label where admin can enter usernames to delete
        Label instructionLabel = new Label("Enter the username of the user to delete:");
        TextField usernameField = new TextField();
        String fieldStyle = "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-padding: 10px;";
        usernameField.setStyle(fieldStyle);
        
        // Add another label to show status of the deletion in case of success/error
        Label statusLabel = new Label();

        // Add a button that deletes a user when clicked
        // When clicked, it gets username typed, and goes through several checks
        Button deleteButton = new Button("Delete User");
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        deleteButton.setOnAction(e -> {
            String userNameToDelete = usernameField.getText().trim();

            // Check if the box is empty, show a message if yes
            if (userNameToDelete.isEmpty()) {
                statusLabel.setText("Please enter a username.");
                return;
            }

            // Prevents the admin from deleting themselves
            if (userNameToDelete.equals(currentAdmin.getUserName())) {
            	statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("You cannot delete yourself.");
                return;
            }

            // Checks if the username exists in the database, 
            // if the username is not found, it shows an error message
            if (!databaseHelper.doesUserExist(userNameToDelete)) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("User not found.");
                return;
            }

            // Confirm deletion with Yes/No buttons
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
                "Are you sure you want to delete user \"" + userNameToDelete + "\"?",
                ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        databaseHelper.deleteUser(userNameToDelete);
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText(null);
                        success.setContentText("User \"" + userNameToDelete + "\" successfully deleted.");
                        success.showAndWait();
                        usernameField.clear();
                        statusLabel.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        statusLabel.setText("An error occurred during deletion.");
                    }
                }
            });
        });

        // Add button to go back to AdminHomePage
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
        contentCard.getChildren().addAll(titleLabel, instructionLabel, usernameField, deleteButton, backButton, statusLabel);

        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));

        // Set scene and primaryStage
        Scene scene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Delete a User");
        primaryStage.show();
    }
}


