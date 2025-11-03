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
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	
	private final DatabaseHelper databaseHelper;
	
	public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
	    
	    // label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    adminLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
	    
	    // Invite button to direct admin to invitation page
	    Button inviteButton = new Button("Invite");
	    inviteButton.setStyle(
                "-fx-background-color: #0077B5; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage, user);
        });
        
	    // Button to direct to the AdminRolesPage
	    Button rolesButton = new Button("Add or Remove User Roles");
	    rolesButton.setMaxWidth(Double.MAX_VALUE);
        rolesButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        rolesButton.setOnAction(a -> {
            new AdminRolesPage(databaseHelper).show(primaryStage, user);
        });

		// Button to direct to the AdminListPage
        Button listButton = new Button("List All Users");
        listButton.setMaxWidth(Double.MAX_VALUE);
        listButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        listButton.setOnAction(a -> {
        	new AdminListPage(databaseHelper).show(primaryStage, user);
        });

		// Button to direct to the AdminDeletePage
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setMaxWidth(Double.MAX_VALUE);
        deleteUserButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        deleteUserButton.setOnAction(a -> {
        	new AdminDeletePage(databaseHelper).show(primaryStage, user);
        });

		// Button to direct to the AdminPasswordResetPage
        Button passwordResetButton = new Button("Password Reset");
        passwordResetButton.setMaxWidth(Double.MAX_VALUE);
        passwordResetButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        passwordResetButton.setOnAction(a -> {
            new AdminPasswordResetPage(databaseHelper).show(primaryStage, user);
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
        contentCard.getChildren().addAll(adminLabel, inviteButton, rolesButton, listButton, deleteUserButton, passwordResetButton, logoutButton);
        
        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));
	    
	    // Set scene and primary stage
	    Scene adminScene = new Scene(rootLayout, 800, 600);
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
        primaryStage.show();

    }
}
