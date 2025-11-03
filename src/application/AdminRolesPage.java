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
import java.util.ArrayList;


/**
 * AdminRolesPage class handles the role adding and removal functions that an Admin user is able to perform.
 * Admin provides username and role, and selects to add or remove the role from the account associated with the username.
*/

public class AdminRolesPage {
	
	private final DatabaseHelper databaseHelper;
	private String label;
	private boolean hasError;
	
	public AdminRolesPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		label = "";
		hasError = false;
	}
	
	public AdminRolesPage(DatabaseHelper databaseHelper, String label, boolean hasError) {
        this.databaseHelper = databaseHelper;
        this.label = label;
        this.hasError = hasError;
    }
	
	public void show(Stage primaryStage, User user) {
		// Label for title
		Label titleLabel = new Label("Role Assignment / Removal");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
		// Input fields for userName, password, and invitation code
		TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        String fieldStyle = "-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-padding: 10px;";
        userNameField.setStyle(fieldStyle);
        
        ChoiceBox<String> roleField = new ChoiceBox<>();
        roleField.getItems().addAll("Admin", "Student", "Instructor", "Staff", "Reviewer");
        roleField.setValue("Admin");
        roleField.setMaxWidth(Double.MAX_VALUE);
        roleField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px;");
      
        // Label to display error messages for invalid input or search issues, or success messages
        Label errorLabel = new Label();
        if(hasError) {
        	errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        	errorLabel.setText(label);
        }
        else {
        	errorLabel.setStyle("-fx-text-fill: blue; -fx-font-size: 12px;");
        	errorLabel.setText(label);
        }
        
        // Button to add role
        Button addRole = new Button("Add Role");
        addRole.setMaxWidth(Double.MAX_VALUE);
        addRole.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        
        // Button to delete role
        Button removeRole = new Button("Remove Role");
        removeRole.setMaxWidth(Double.MAX_VALUE);
        removeRole.setStyle(
            "-fx-background-color: #0077B5; -fx-text-fill: white; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        
        // Button to direct back to AdminHomePage
        Button backButton = new Button("Back to Home Page");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setStyle(
            "-fx-background-color: #E0E0E0; -fx-text-fill: #333; " +
            "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
        );
        
        addRole.setOnAction(a -> {
        	String userName = userNameField.getText();
        	String role = roleField.getValue().toLowerCase();
        	
        	try {
        		// Check if the user exists
        		if(databaseHelper.doesUserExist(userName)) {
        			// Check if user already has role
        			if(!databaseHelper.getUserRole(userName).contains(role)) {
        				// Find user password
        				String password = "";
        				ArrayList<User> users = databaseHelper.returnAllUsers();
        				for(User u : users) {
        					if(u.getUserName() == userName) {
        						password = u.getPassword();
        						break;
        					}
        				}
        				// Register new user
        				databaseHelper.register(new User(userName, password, role));
        				new AdminRolesPage(databaseHelper,"Role added to user successfully!",false).show(primaryStage, user);
        			}
        			else {
        				new AdminRolesPage(databaseHelper,"User already has this role!",true).show(primaryStage, user);
        			}
        		}
        		else {
        			new AdminRolesPage(databaseHelper,"User not found! Please check spelling and try again.",true).show(primaryStage, user);
        		}
        	} catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        removeRole.setOnAction(a -> {
        	String userName = userNameField.getText();
        	String role = roleField.getValue().toLowerCase();
        	
        	try {
        		// Check if the user exists
        		if(databaseHelper.doesUserExist(userName)) {
        			// Check if user already has role
        			if(!databaseHelper.getUserRole(userName).contains(role)) {
        				new AdminRolesPage(databaseHelper,"User does not have this role!",true).show(primaryStage, user);
        			}
        			else if(user.getUserName().equals(userName) && user.getRole().equals(role)) {
        				new AdminRolesPage(databaseHelper,"You cannot remove your own Admin role!",true).show(primaryStage, user);
        			}
        			else if(!databaseHelper.getUserRole(userName).contains(",")) {
        				new AdminRolesPage(databaseHelper,"User only has one role!",true).show(primaryStage, user);
        			}
        			else {
        				// Delete the user's role
        				databaseHelper.deleteRole(userName,role);
        				new AdminRolesPage(databaseHelper,"Role deleted from user successfully!",false).show(primaryStage, user);
        			}
        		}
        		else {
        			new AdminRolesPage(databaseHelper,"User not found! Please check spelling and try again.",true);
        		}
        	} catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        backButton.setOnAction(a->{
        	new AdminHomePage(databaseHelper).show(primaryStage, user);
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
        contentCard.getChildren().addAll(titleLabel, userNameField, roleField, errorLabel, addRole, removeRole, backButton);
        
        // StackPane for grey background
        StackPane rootLayout = new StackPane(contentCard);
        rootLayout.setStyle("-fx-background-color: #f3f2ef;");
        rootLayout.setPadding(new Insets(20));
        
        // Set scene and primaryStage
        primaryStage.setScene(new Scene(rootLayout, 800, 600));
        primaryStage.setTitle("Role Assignment / Removal");
        primaryStage.show();
	}
}

