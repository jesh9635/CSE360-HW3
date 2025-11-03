package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
public class QuestionsPage {
	
    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Initializes the QuestionsPage with the database helper and logged in user
     *
     * @param databaseHelper The helper class for interfacing with the database.
     * @param user The user who is currently logged in.
     */
    public QuestionsPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
    }

    public void show(Stage primaryStage) {
    	// VBox for containing all UI elements in page
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f3f2ef;");

        // Back button to return to the student home page
        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, currentUser));
        
        // HBox for the search bar and button
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(0, 0, 10, 0));
        searchBox.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setStyle("-fx-font-size: 14px; -fx-background-radius: 8px; -fx-border-color: #ccc; -fx-border-radius: 8px; -fx-padding: 7px;");
        javafx.scene.layout.HBox.setHgrow(searchField, javafx.scene.layout.Priority.ALWAYS); // Resize search bar

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #0077B5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8px 15px;");


        searchBox.getChildren().addAll(searchField, searchButton);
        
        // VBox for containing new question section
        VBox addQuestionBox = new VBox(10);
        addQuestionBox.setPadding(new Insets(15));
        addQuestionBox.setStyle("-fx-background-color: white");
        
        // Label for a header
        Label askQuestionLabel = new Label("Ask a new question:");
        askQuestionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // TextArea object for input field
        TextArea newQuestionText = new TextArea();
        newQuestionText.setPromptText("Type your question here");
        newQuestionText.setWrapText(true);
        newQuestionText.setPrefHeight(100);
        
        // Button to submit new question
        Button submitQuestionButton = new Button("Submit Question");
        submitQuestionButton.setStyle("-fx-background-color: #0077B5; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-padding: 8px 15px;");
        submitQuestionButton.setOnAction(e -> {
            String questionText = newQuestionText.getText();
            if (!questionText.isEmpty()) {
                Question question = new Question(currentUser.getUserName(), newQuestionText.getText(), false, 0);
                // Add question to database
                try {
					if (databaseHelper.createQuestion(question)) {
					    // Refresh the page to show the new question
					    show(primaryStage);
					} else {
					    // Show an alert if the question is a duplicate
					    Alert alert = new Alert(Alert.AlertType.WARNING);
					    alert.setTitle("Duplicate Question");
					    alert.setHeaderText("This question already exists.");
					    alert.setContentText("Please ask a different question.");
					    alert.showAndWait();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });

        addQuestionBox.getChildren().addAll(askQuestionLabel, newQuestionText, submitQuestionButton);

        // VBox for displaying existing questions
        VBox listContainer = new VBox(10);
        
		// Get all questions from database and store in array list
        ArrayList<Question> questionsList;
		try {
			questionsList = databaseHelper.getAllQuestions();
			
			// Sort resolved first then unresolved
            questionsList.sort((q1, q2) -> {
                if (q1.getResolved() != q2.getResolved()) {
                	return Boolean.compare(q2.getResolved(), q1.getResolved());
                }
                return Integer.compare(q2.getQuestionID(), q1.getQuestionID()); // newest first
            });
			
			// For each question create a VBox
	        for (Question question : questionsList) {
	            VBox questionBox = createQuestionBox(question, primaryStage);
	            listContainer.getChildren().add(questionBox);
	        }
	        
	        // If search button clicked display only questions that match keyword
	        searchButton.setOnAction(e -> {
	            String searchTerm = searchField.getText().toLowerCase();
	            listContainer.getChildren().clear(); // Clear the existing list

	            for (Question question : questionsList) {
	                if (question.getText().toLowerCase().contains(searchTerm)) {
	                    VBox questionBox = createQuestionBox(question, primaryStage);
	                    listContainer.getChildren().add(questionBox);
	                }
	            }
	        });
	        
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
        // ScrollPane to allow scrolling through questions
        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);

        container.getChildren().addAll(backButton, addQuestionBox, searchBox, scrollPane);
        
        // Set up the scene and show
        Scene scene = new Scene(container, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Q&A Home");
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
        
        // HBox containing author and resolved status
        HBox authorLine = new HBox(5);
        authorLine.setAlignment(Pos.CENTER_LEFT);
        
        // Label for author field
        Label authorLabel = new Label("Asked by: " + question.getAuthor());
        authorLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
        
        // If the question is resolved, add resolved flag
        if (question.getResolved()) {
        	Label resolvedLabel = new Label("[Resolved]");
            resolvedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
            authorLine.getChildren().add(resolvedLabel);
        }

        
        // Label to display question text
        Label questionText = new Label(question.getText());
        questionText.setStyle("-fx-font-size: 14px;");
        questionText.setWrapText(true);
        questionText.prefWidthProperty().bind(primaryStage.widthProperty().subtract(60));

        // HBox to contain the view, edit and delete buttons
        HBox buttonBox = new HBox(10);
        
        // Button to view answers for question
        Button viewAnswersButton = new Button("View Answers");
        viewAnswersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0077B5;");
        // View answers button logic
        viewAnswersButton.setOnAction(e -> {
        	// Create new instance of AnswersPage and show
            new AnswerPage(databaseHelper, currentUser).show(primaryStage, question);
        });
        buttonBox.getChildren().add(viewAnswersButton);
        
        // If the current user is the author of the question, show edit and delete buttons
        if (currentUser.getUserName().equals(question.getAuthor())) {
            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;");          
            editButton.setOnAction(e -> {
                // Open a new window for editing
                Stage editStage = new Stage();
                VBox editLayout = new VBox(10);
                editLayout.setPadding(new Insets(15));
                
                // Input field with question to edit
                TextArea editTextArea = new TextArea(question.getText());
                editTextArea.setWrapText(true);
                editTextArea.setPrefSize(400, 150);
                
                // Save button
                Button saveButton = new Button("Save");
                saveButton.setOnAction(saveEvent -> {
                    String newText = editTextArea.getText();
                    if (!newText.isEmpty()) {
                        // Update text using updateQuestion method
                        try {
							databaseHelper.updateQuestion(question.getQuestionID(), newText);
						} catch (SQLException e1) {
							e1.printStackTrace();
						} 
                        show(primaryStage); // Refresh the main page
                        editStage.close(); // Close the edit window
                    }
                });

                editLayout.getChildren().addAll(editTextArea, saveButton);
                Scene editScene = new Scene(editLayout);
                editStage.setScene(editScene);
                editStage.setTitle("Edit Question");
                editStage.show();
            });
            
            // Button to delete the question
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F;");
            deleteButton.setOnAction(a -> {
                // Delete the question from the database
                try {
					databaseHelper.deleteQuestion(question.getQuestionID());
				} catch (SQLException e) {
					e.printStackTrace();
				}
                show(primaryStage);
            });
            boolean hasPrivateMessages = false;
            try {
            	ArrayList<PrivateMessage> privateMessages = databaseHelper.getAllPrivateMessages();
            	for (PrivateMessage privateMessage : privateMessages) {
            		if (privateMessage.getReceiver().equals(currentUser.getUserName()) && privateMessage.getQuestionID() == question.getQuestionID()) {
            			hasPrivateMessages = true;
            			break;
            		}
            	}
            } catch (SQLException e1) {
    			e1.printStackTrace();
    		}
            if (hasPrivateMessages) {
                // Button to view private messages
                Button viewPMButton = new Button("View Private Messages");
                viewPMButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0077B5;");
                viewPMButton.setOnAction(a -> {
                    new StudentPrivateMessageUsersPage(databaseHelper, currentUser).show(primaryStage, question);
                });
                buttonBox.getChildren().add(viewPMButton);
            }
            buttonBox.getChildren().addAll(editButton, deleteButton);
        }
        
     // If the current user is not the author of the question, show send private message button
        if (!currentUser.getUserName().equals(question.getAuthor())) {
            Button pmButton = new Button("Send Private Message");
            pmButton.setOnAction(a -> {
                // Open PM Box
            	// Open a new window for editing
                Stage pmStage = new Stage();
                VBox pmLayout = new VBox(10);
                pmLayout.setPadding(new Insets(15));
                
                // Input field with question to pm
                TextArea pmTextArea = new TextArea();
                pmTextArea.setWrapText(true);
                pmTextArea.setPrefSize(400, 150);
                
                HBox pmButtonBox = new HBox(10);
                // Save button
                Button sendButton = new Button("Send");
                sendButton.setOnAction(sendEvent -> {
                    String newText = pmTextArea.getText();
                    if (!newText.isEmpty()) {
                    	//Send Private Message
                    	PrivateMessage p = new PrivateMessage(newText, currentUser.getUserName(), question.getAuthor(), question.getQuestionID(), 0, false);
                        try {
							databaseHelper.createPrivateMessage(p);
						} catch (SQLException e1) {
							e1.printStackTrace();
						} 
                        show(primaryStage); // Refresh the main page
                        pmStage.close(); // Close the pm window
                    }
                });
                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(sendEvent -> {
                        pmStage.close(); // Close the pm window
                });
                pmButtonBox.getChildren().addAll(sendButton, cancelButton);
                pmLayout.getChildren().addAll(pmTextArea, pmButtonBox);
                Scene pmScene = new Scene(pmLayout);
                pmStage.setScene(pmScene);
                pmStage.setTitle("Private Message");
                pmStage.show();
            });
            //buttonBox.getChildren().add(pmButton);
        }
        if (!currentUser.getUserName().equals(question.getAuthor())) {
	        Button viewPMButton = new Button("Send Message");
	        viewPMButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0077B5;");
	        viewPMButton.setOnAction(a -> {
	    		try {
	    			ArrayList<User> users = databaseHelper.getAllUsers();
	    			User otherUser = null;
	    			for (User user : users) {
	    				if (user.getUserName().equals(question.getAuthor())) {
	    					otherUser = user;
	        				break;
	    				}
	    			}
	    			if (otherUser != null) {
	                	new StudentPrivateMessagePage(databaseHelper, currentUser).show(primaryStage, question, otherUser);
	    			}
	
	    		} catch (SQLException e1) {
	    			e1.printStackTrace();
	    		}
	        });
	        buttonBox.getChildren().add(viewPMButton);
        }

        questionBox.getChildren().addAll(authorLine, questionText, buttonBox);
        return questionBox;
    }
}
