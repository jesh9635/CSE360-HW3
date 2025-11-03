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
public class StudentPrivateMessagePage {
	
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Initializes the QuestionsPage with the database helper and logged in user
     *
     * @param databaseHelper The helper class for interfacing with the database.
     * @param user The user who is currently logged in.
     */
    public StudentPrivateMessagePage(DatabaseHelper databaseHelper, User user) {
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
    public void show(Stage primaryStage, Question currentQuestion, User otherUser) {
    	// VBox for containing all UI elements in page
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f3f2ef;");

        // Back button 
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
        	new StudentHomePage(databaseHelper).show(primaryStage, currentUser);
        	//new StudentPrivateMessageUsersPage(databaseHelper, currentUser).show(primaryStage, currentQuestion);
        });
        
        // Label for a header
        Label header = new Label("Private Message Chain");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        
        VBox addNewMessageBox = new VBox(10);
        addNewMessageBox.setPadding(new Insets(15));
        addNewMessageBox.setStyle("-fx-background-color: white");
        
        
        // Label for a header
        Label textBoxLabel = new Label("Post a response:");
        textBoxLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // TextArea object for input field
        TextArea newMessageText = new TextArea();
        newMessageText.setPromptText("Type your message here");
        newMessageText.setWrapText(true);
        newMessageText.setPrefHeight(100);
        
        // Button to submit new question
        Button submitMessageButton = new Button("Post");
        submitMessageButton.setOnAction(e -> {
            String messageText = newMessageText.getText();
            if (!messageText.isEmpty()) {
                PrivateMessage p = new PrivateMessage(messageText, currentUser.getUserName(), otherUser.getUserName(), currentQuestion.getQuestionID(), 0, false);
                // Add question to database
                try {
					if (databaseHelper.createPrivateMessage(p)) {
					    // Refresh the page to show the new question
					    show(primaryStage, currentQuestion, otherUser);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });
        addNewMessageBox.getChildren().addAll(textBoxLabel, newMessageText, submitMessageButton);

        // VBox for displaying existing questions
        VBox listContainer = new VBox(10);
        
        // Get all PMs and if the question that it concerns isn't in question list, add it.
        ArrayList<PrivateMessage> databasePrivateMessages = new ArrayList<PrivateMessage>();
        ArrayList<PrivateMessage> privateMessageSubset = new ArrayList<PrivateMessage>();
		try {
			databasePrivateMessages = databaseHelper.getAllPrivateMessages();
			// For each question create a VBox
	        for (PrivateMessage privateMessage : databasePrivateMessages) {
	        	if (privateMessage.getQuestionID() == currentQuestion.getQuestionID()) {
	        		//Check if sender and receiver are currentUser and otherUser or vice versa
	        		if ((privateMessage.getSender().equals(currentUser.getUserName())
    				&& privateMessage.getReceiver().equals(otherUser.getUserName()))
    				||  (privateMessage.getReceiver().equals(currentUser.getUserName()) 
					&& privateMessage.getSender().equals(otherUser.getUserName()))) {
	        			if (privateMessage.getReceiver().equals(currentUser.getUserName())){
	        				privateMessage.setSeenStatusReceiver(true);
	        				databaseHelper.updatePrivateMessage(privateMessage);
	        			}
	        			privateMessageSubset.add(privateMessage);
	        		}
	        	}
	        }
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (privateMessageSubset.isEmpty()) {
			Label emptyPrivateMessage = new Label("You have no messages");
			listContainer.getChildren().add(emptyPrivateMessage);
		} else {
			for (PrivateMessage privateMessage : privateMessageSubset) {
		        VBox privateMessageBox = createPrivateMessageBox(privateMessage, primaryStage);
		        listContainer.getChildren().add(privateMessageBox);
			}		
		}


        // ScrollPane to allow scrolling through questions
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);

        container.getChildren().addAll(backButton, header, scrollPane, addNewMessageBox);
        
        // Set up the scene and show
        Scene scene = new Scene(container, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Private Message Conversation Page");
        primaryStage.show();
    }

    
    /**
     * Creates a VBox for a single question.
     *
     * @param question The question to display.
     * @param primaryStage The parent stage for this application.
     * @return A VBox containing the question.
     */
    private VBox createPrivateMessageBox(PrivateMessage privateMessage, Stage primaryStage) {
    	// VBox to contain all UI elements
        VBox privateMessageBox = new VBox(5);
        privateMessageBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 10;"
        );
        
        // Label for author field
        Label authorLabel = new Label(privateMessage.getSender()+": ");
        authorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        // Label to display privateMessage text
        Label messageText = new Label(privateMessage.getMessage());
        messageText.setStyle("-fx-font-size: 14px;");
        messageText.setWrapText(true);
        messageText.prefWidthProperty().bind(primaryStage.widthProperty().subtract(60));


        privateMessageBox.getChildren().addAll(authorLabel, messageText);
        return privateMessageBox;
    }
}
