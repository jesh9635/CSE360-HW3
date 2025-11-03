package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;
import databasePart1.DatabaseHelper;

/**
 * The QuestionsPage class displays the main Q&A page.
 * It allows users to view existing questions, add new questions, and navigate to the answer page for a specific question.
 * Users can also edit and delete their own questions.
 */
public class StudentPrivateMessageUsersPage {
	
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Initializes the QuestionsPage with the database helper and logged in user
     *
     * @param databaseHelper The helper class for interfacing with the database.
     * @param user The user who is currently logged in.
     */
    public StudentPrivateMessageUsersPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
    }

    private boolean userInList(String userName, ArrayList<User> userList) {
    	if (!userList.isEmpty()) {
        	for (User user : userList) {
        		if (user.getUserName().equals(userName)) {
        			return true;
        		}
        	}	
    	}
    	return false;
    }
    public void show(Stage primaryStage, Question currentQuestion) {
    	// VBox for containing all UI elements in page
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f3f2ef;");

        // Back button 
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new StudentHomePage(databaseHelper).show(primaryStage, currentUser);
        });
        
        // Label for a header
        Label header = new Label("Choose a User");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        

        // VBox for displaying existing questions
        VBox listContainer = new VBox(10);
        
        // Get all PMs and if the question that it concerns isn't in question list, add it.
        ArrayList<PrivateMessage> databasePrivateMessages = new ArrayList<PrivateMessage>();
        ArrayList<User> databaseUsers = new ArrayList<User>();
        ArrayList<User> userSubset = new ArrayList<User>();
		try {
			databasePrivateMessages = databaseHelper.getAllPrivateMessages();
			databaseUsers = databaseHelper.getAllUsers();
			// For each question create a VBox
	        for (PrivateMessage privateMessage : databasePrivateMessages) {
	        	if (privateMessage.getReceiver().equals(currentUser.getUserName()) && privateMessage.getQuestionID() == currentQuestion.getQuestionID()) {
	        		if(!userInList(privateMessage.getSender(), userSubset)) {
	        			for (User user : databaseUsers) {
	        				if (user.getUserName().equals(privateMessage.getSender())) {
	        					userSubset.add(user);
	        					break;
	        				}
	        			}
	        		}
	        	}
	        }
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (User user : userSubset) {
	        VBox userBox = createUserBox(user, primaryStage, currentQuestion);
	        listContainer.getChildren().add(userBox);
		}

        // ScrollPane to allow scrolling through questions
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);

        container.getChildren().addAll(backButton, header, scrollPane);
        
        // Set up the scene and show
        Scene scene = new Scene(container, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Private Message Users Page");
        primaryStage.show();
    }

    
    /**
     * Creates a VBox for a single question.
     *
     * @param question The question to display.
     * @param primaryStage The parent stage for this application.
     * @return A VBox containing the question.
     */
    private VBox createUserBox(User user, Stage primaryStage, Question currentQuestion) {
    	// VBox to contain all UI elements
        VBox userBox = new VBox(5);
        userBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 10;"
        );
        // Label to display user text
        Label userText = new Label(user.getUserName());
        userText.setStyle("-fx-font-size: 14px;");
        userText.setWrapText(true);
        userText.prefWidthProperty().bind(primaryStage.widthProperty().subtract(60));

        // HBox to contain all the buttons
        HBox buttonBox = new HBox(10);
        
        // Button to view answers for user
        Button viewButton = new Button("View");
        
        // View answers button logic
        viewButton.setOnAction(e -> {
        	// Go to StudentPrivateMessageUsers and send the question to its show method
        	new StudentPrivateMessagePage(databaseHelper, currentUser).show(primaryStage, currentQuestion, user);
            //new AnswerPage(databaseHelper, currentUser).show(primaryStage, question);
        });
        buttonBox.getChildren().add(viewButton);
        

        userBox.getChildren().addAll(userText, buttonBox);
        return userBox;
    }
}
