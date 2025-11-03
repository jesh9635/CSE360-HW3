package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

/**
 * The RoleSelectionPage class displays a  screen for users that have been assigned more than one role.
 * It allows users to navigate to their respective pages based on their role selection or logout.
 */
public class RoleSelectionPage {
	
	private final DatabaseHelper databaseHelper;

    public RoleSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	//Title
	    Label titleLabel = new Label("CSE360 Q&A System");
	    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
	    
	    //Text above drop down menu
	    Label roleLabel = new Label("Choose your role:");
	    roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
	    
	    // Create a drop down menu with list of roles assigned to user
        ChoiceBox<String> dropDownMenu = new ChoiceBox<>();
        String[] userRoles = user.getRole().split(",");
        dropDownMenu.getItems().addAll(userRoles);
        dropDownMenu.setValue(userRoles[0]); // Set the first role as the default
        dropDownMenu.setMaxWidth(Double.MAX_VALUE);
        dropDownMenu.setStyle("-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px;");
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue");
	    continueButton.setMaxWidth(Double.MAX_VALUE);
        continueButton.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        continueButton.setOnAction(a -> {
	    	String selectedRole = dropDownMenu.getValue().trim().toLowerCase();
	    	
	    	if(selectedRole.equals("admin")) {
	    		new AdminHomePage(databaseHelper).show(primaryStage,user);
	    	}
	    	else if(selectedRole.equals("student")) {
	    		new StudentHomePage(databaseHelper).show(primaryStage, user);
	    	}
	    	else {
	    		new UserHomePage(databaseHelper).show(primaryStage);
	    	}
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
	    
        // VBox for the white border around the content
	    VBox contentCard = new VBox(15); // Spacing
        contentCard.setAlignment(Pos.CENTER);
        contentCard.setPadding(new Insets(40, 40, 40, 40));
        contentCard.setMaxWidth(400);
        contentCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 15, 0, 0, 5);"
        );
	    contentCard.getChildren().addAll(titleLabel, roleLabel, dropDownMenu, continueButton, logoutButton);
        VBox.setMargin(roleLabel, new Insets(10, 0, 0, 0)); // Add top margin to the role label

        // StackPane for the grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));

        // Set the scene to primary stage
	    Scene roleScene = new Scene(rootLayout, 800, 600);
	    primaryStage.setScene(roleScene);
	    primaryStage.setTitle("Role Selection");
        primaryStage.show();
    }
}
