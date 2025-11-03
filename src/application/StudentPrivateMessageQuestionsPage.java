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
public class StudentPrivateMessageQuestionsPage {
	
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Initializes the QuestionsPage with the database helper and logged in user
     *
     * @param databaseHelper The helper class for interfacing with the database.
     * @param user The user who is currently logged in.
     */
    public StudentPrivateMessageQuestionsPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
    }

    private boolean questionInList(int id, ArrayList<Question> questionList) {
    	if (!questionList.isEmpty()) {
        	for (Question question : questionList) {
        		if (question.getQuestionID() == id) {
        			return true;
        		}
        	}	
    	}
    	return false;
    }
    public void show(Stage primaryStage) {
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
        Label header = new Label("Choose a Question");
        header.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        

        // VBox for displaying existing questions
        VBox listContainer = new VBox(10);
        
        // Get all PMs and if the question that it concerns isn't in question list, add it.
        ArrayList<PrivateMessage> databasePrivateMessages = new ArrayList<PrivateMessage>();
        ArrayList<Question> questionSubset = new ArrayList<Question>();
		try {
			databasePrivateMessages = databaseHelper.getAllPrivateMessages();
			// For each question create a VBox
	        for (PrivateMessage privateMessage : databasePrivateMessages) {
	        	if (privateMessage.getReceiver().equals(currentUser.getUserName())) {
	        		if (!questionInList(privateMessage.getQuestionID(),questionSubset)) {
	        			questionSubset.add(databaseHelper.getQuestionByID(privateMessage.getQuestionID()));
	        		}
	        	}
	        }
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Question question : questionSubset) {
	        VBox questionBox = createQuestionBox(question, primaryStage);
	        listContainer.getChildren().add(questionBox);
		}

        // ScrollPane to allow scrolling through questions
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);

        container.getChildren().addAll(backButton, header, scrollPane);
        
        // Set up the scene and show
        Scene scene = new Scene(container, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Private Message Questions Page");
        primaryStage.show();
    }

    
    /**
     * Creates a VBox for a single question.
     *
     * @param question The question to display.
     * @param primaryStage The parent stage for this application.
     * @return A VBox containing the question.
     */
    private VBox createQuestionBox(Question question, Stage primaryStage) {
    	// VBox to contain all UI elements
        VBox questionBox = new VBox(5);
        questionBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 10;"
        );
        // Label to display question text
        Label questionText = new Label(question.getText());
        questionText.setStyle("-fx-font-size: 14px;");
        questionText.setWrapText(true);
        questionText.prefWidthProperty().bind(primaryStage.widthProperty().subtract(60));

        // HBox to contain all the buttons
        HBox buttonBox = new HBox(10);
        
        // Button to view answers for question
        Button viewButton = new Button("View");
        
        // View answers button logic
        viewButton.setOnAction(e -> {
        	// Go to StudentPrivateMessageUsers and send the question to its show method
            new StudentPrivateMessageUsersPage(databaseHelper, currentUser).show(primaryStage, question);
        });
        buttonBox.getChildren().add(viewButton);
        

        questionBox.getChildren().addAll(questionText, buttonBox);
        return questionBox;
    }
}
