package application;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class AdminListPage {
	
	private final DatabaseHelper databaseHelper;
	
	public AdminListPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/**
     * Displays the Admin List page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	public void show(Stage primaryStage, User user) {
		try {
			// Label for the title
			Label titleLabel = new Label("User List");
			titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
						
			// Create columns for table
			TableColumn<User, String> userNames = new TableColumn<>("Username");
			userNames.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
			userNames.setPrefWidth(180); // Set a preferred width for the column
	
			TableColumn<User, String> roles = new TableColumn<>("Roles");
			roles.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
			roles.setPrefWidth(180); // Set a preferred width for the column

			TableView<User> displayTable = new TableView<>();
			displayTable.getColumns().add(userNames);
			displayTable.getColumns().add(roles);
			displayTable.setStyle("-fx-background-color: transparent; -fx-selection-bar: lightblue; -fx-selection-bar-non-focused: lightcyan;");
			
			// Parse users and update table information
			ArrayList<User> users = databaseHelper.returnAllUsers();
			ObservableList<User> userList = FXCollections.observableArrayList(users);
			displayTable.setItems(userList);
			
			// Button to go back to AdminHomePage
			Button backButton = new Button("Back to Home Page");
			backButton.setMaxWidth(Double.MAX_VALUE);
			backButton.setStyle(
				"-fx-background-color: #E0E0E0; -fx-text-fill: #333; " +
				"-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 12px;"
			);
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
			contentCard.getChildren().addAll(titleLabel, displayTable, backButton);

			// StackPane for grey background
			StackPane rootLayout = new StackPane(contentCard);
			rootLayout.setStyle("-fx-background-color: #f3f2ef;");
			rootLayout.setPadding(new Insets(20));
			
	        primaryStage.setScene(new Scene(rootLayout, 800, 600));
	        primaryStage.setTitle("Role Assignment / Removal");
	        primaryStage.show();
			
		} catch(SQLException e) {
			System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
		}
		
	}
}

