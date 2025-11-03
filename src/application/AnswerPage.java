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
 * The AnswerPage class displays the answers for a specific question.
 * It allows users to view existing answers, add new answers, and edit or delete their own answers.
 */
public class AnswerPage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    /**
     * Initializes AnswerPage with the database helper and the current user.
     *
     * @param databaseHelper The helper class for database.
     * @param user The user who is currently logged in.
     */
    public AnswerPage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.currentUser = user;
    }
    
    /**
     * Displays the answer page for a given question.
     *
     * @param primaryStage The primary stage for this application.
     * @param question The question for which to display the answers.
     */
    public void show(Stage primaryStage, Question question) {
    	//VBox to contain all UI elements
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f3f2ef;");

        // Label to display header
        Label questionHeader = new Label("Question:");
        questionHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        // VBox for containing elements
        VBox questionBox = new VBox(5);
        questionBox.setStyle("-fx-background-color: white; -fx-padding: 15;");
        // Label for author
        Label questionAuthorLabel = new Label("Asked by: " + question.getAuthor());
        questionAuthorLabel.setStyle("-fx-font-size: 12px");
        // Label for displaying text
        Label questionTextLabel = new Label(question.getText());
        questionTextLabel.setStyle("-fx-font-size: 14px;");
        questionTextLabel.setWrapText(true);
        
        // HBox with Button for asking clarifying question, and Button for parent question
        HBox newQuestionBox = new HBox(10);
        Button clarifyingQuestionButton = new Button("Ask Relevant Question");
        clarifyingQuestionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;");
        clarifyingQuestionButton.setOnAction(e -> {
        	// Popup Stage for asking clarifying question
        	VBox newQuestionLayout = new VBox(10);
        	Scene newQuestionScene = new Scene(newQuestionLayout);
            Stage newQuestionStage = new Stage();
        	
        	// TextArea for the user to input their new question
        	TextArea newQuestionText = new TextArea();
        	newQuestionText.setPromptText("Type your question here");
        	newQuestionText.setWrapText(true);
        	newQuestionText.setPrefHeight(100);
        	
        	HBox newQuestionButtonBox = new HBox(15);
        	// Button for posting a relevant question
        	Button submitQuestionButton = new Button("Submit Question");
        	submitQuestionButton.setStyle("-fx-background-color: #0077B5; -fx-text-fill: white; -fx-font-weight: bold;");
        	submitQuestionButton.setOnAction(a -> {
        		if(!newQuestionText.getText().isEmpty()) {
        			Question newQuestion = new Question(currentUser.getUserName(), newQuestionText.getText(), false, 0, question.getQuestionID());
        			try {
        				if(databaseHelper.createQuestion(newQuestion)) {
        					show(primaryStage, databaseHelper.getAllQuestions().get(0));
        					newQuestionStage.close();
        				} else {
        					Alert alert = new Alert(Alert.AlertType.WARNING);
    					    alert.setTitle("Duplicate Question");
    					    alert.setHeaderText("This question already exists.");
    					    alert.setContentText("Please ask a different question.");
    					    alert.showAndWait();
        				}
        			} catch(SQLException e0) {
        				e0.printStackTrace();
        			}
        		}
        	});
        	Button cancelButton = new Button("Cancel");
        	cancelButton.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333; -fx-font-weight: bold;");
        	cancelButton.setOnAction(a1 -> {
        		newQuestionStage.close();
        	});
        	newQuestionButtonBox.getChildren().addAll(submitQuestionButton, cancelButton);
        	
        	
        	newQuestionLayout.setPadding(new Insets(15));
        	newQuestionLayout.getChildren().addAll(new Label("Ask relevant question:"), newQuestionText, newQuestionButtonBox);
            newQuestionStage.setScene(newQuestionScene);
            newQuestionStage.setTitle("Ask Relevant Question");
            newQuestionStage.show();
        });
        
        if(question.getParentQuestionID() != 0) {
        	Button parentQuestionButton = new Button("View Parent Question");
        	parentQuestionButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;");
        	parentQuestionButton.setOnAction(e -> {
        		try {
        			show(primaryStage, databaseHelper.getQuestionByID(question.getParentQuestionID()));
        		} catch(SQLException e1) {
        			e1.printStackTrace();
        		}
        	});
        	newQuestionBox.getChildren().addAll(clarifyingQuestionButton, parentQuestionButton);
        } else {
        	newQuestionBox.getChildren().addAll(clarifyingQuestionButton);
        }
        
        // Add the question elements to the questionBox
        questionBox.getChildren().addAll(questionAuthorLabel, questionTextLabel, newQuestionBox);

        // VBox for the Post an Answer section
        VBox addAnswerBox = new VBox(10);
        addAnswerBox.setPadding(new Insets(15));
        addAnswerBox.setStyle("-fx-background-color: white;");
        
        // Label for the header of the Post an Answer section
        Label addAnswerHeader = new Label("Post an Answer:");
        addAnswerHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // TextArea for the user to input their answer
        TextArea newAnswerText = new TextArea();
        newAnswerText.setPromptText("Type here...");
        newAnswerText.setWrapText(true);
        
        // Button for posting a new answer
        Button submitAnswerButton = new Button("Submit Answer");
        submitAnswerButton.setStyle("-fx-background-color: #0077B5; -fx-text-fill: white; -fx-font-weight: bold;");
        submitAnswerButton.setOnAction(e0 -> {
            String answerText = newAnswerText.getText();
            // If the input field is not empty, create an Answers object and add the answer
            if (!answerText.isEmpty()) {
                Answer newAnswer = new Answer(currentUser.getUserName(), newAnswerText.getText(), false, false, 0, question.getQuestionID());
                // Add answer to the database; if it's a duplicate, display an error
                try {
					if (databaseHelper.doesAnswerExist(newAnswer)) {
					    Alert alert = new Alert(Alert.AlertType.WARNING);
					    alert.setTitle("Duplicate Answer");
					    alert.setHeaderText("This answer already exists.");
					    alert.setContentText("Please provide a different answer.");
					    alert.showAndWait();
					} else {
						databaseHelper.createAnswer(newAnswer);
					    show(primaryStage, question);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
        });

        addAnswerBox.getChildren().addAll(addAnswerHeader, newAnswerText, submitAnswerButton);

        // Display the list of answers
        Label answersHeader = new Label("Answers:");
        answersHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // VBox to contain the list of answers
        VBox answerListContainer = new VBox(10);
        // Store list of answers in array list
        ArrayList<Answer> answerList;
		try {
			answerList = databaseHelper.getAllAnswers(question.getQuestionID());
			
			// Iterate through the ArrayList and create a VBox for each answer
	        for (Answer answer : answerList) {
	        	// VBox will contain UI elements
	            VBox answerBox = new VBox(5);
	            answerBox.setStyle(
	                    "-fx-background-color: white; " +
	                    "-fx-padding: 10;"
                );
	            answerBox.setStyle("-fx-background-color: white; -fx-padding: 10;");
	            // If answer is solution highlight green
	            if (answer.getResolving()) {
	                answerBox.setStyle(
	                    "-fx-background-color: #d0f0c0; " + 
	                    "-fx-padding: 10;"
	                );
	            }
	            
	            // Label for author 
	            Label authorLabel = new Label("Answered by: " + answer.getAuthor());
	            authorLabel.setStyle("-fx-font-size: 12px;");
	            
	            // Label for displaying answer
	            Label answerLabel = new Label(answer.getAnswerText());
	            answerLabel.setStyle("-fx-font-size: 14px;");
	            answerLabel.setWrapText(true);
	            
	            // HBox for containing buttons
	            HBox answerButtonBox = new HBox(10);
	            
	            // If logged in user is the author then add edit and delete buttons
	            if (currentUser.getUserName().equals(answer.getAuthor())) {
	            	// Button for edit
	                Button editAnswerButton = new Button("Edit");
	                editAnswerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;");
	                
	                // Edit button logic
	                editAnswerButton.setOnAction(e2 -> {
	                	// New stage for edit screen
	                    Stage editStage = new Stage();
	                    
	                    //VBox for storing UI elements in new page
	                    VBox editLayout = new VBox(10);
	                    editLayout.setPadding(new Insets(15));
	                    
	                    // TextArea object for input field preloaded with answer
	                    TextArea editTextArea = new TextArea(answer.getAnswerText());
	                    editTextArea.setWrapText(true);
	                    editTextArea.setPrefSize(400, 150);
	                    
	                    // Button to push changes
	                    Button updateButton = new Button("Update");
	                    // Update Button Logic
	                    updateButton.setOnAction(a -> {
	                        String newText = editTextArea.getText();
	                        // If input field is not empty than call updateAnswer method, then close new page
	                        if (!newText.isEmpty()) {
	                            try {
									databaseHelper.updateAnswer(answer.getID(), newText);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
	                            show(primaryStage, question); // Refresh answers page
	                            editStage.close(); // Close edit window
	                        }
	                    });

	                    editLayout.getChildren().addAll(new Label("Edit your answer:"), editTextArea, updateButton);
	                    Scene editScene = new Scene(editLayout);
	                    editStage.setScene(editScene);
	                    editStage.setTitle("Edit Answer");
	                    editStage.show();
	                });
	                
	                // Button to delete answer
	                Button deleteAnswerButton = new Button("Delete");
	                deleteAnswerButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F;");
	                
	                // Delete button logic
	                deleteAnswerButton.setOnAction(e3 -> {
	                    try {
							databaseHelper.deleteAnswer(answer.getID());
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
	                    show(primaryStage, question); // Refresh page
	                });

	                answerButtonBox.getChildren().addAll(editAnswerButton, deleteAnswerButton);
	            }

	            answerBox.getChildren().addAll(authorLabel, answerLabel, answerButtonBox);
	            answerListContainer.getChildren().add(answerBox);
	        }
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

        // Button to go back to the questions page
        Button backButton = new Button("Back");
        // Logic for the back button
        backButton.setOnAction(e3 -> {
            new QuestionsPage(databaseHelper, currentUser).show(primaryStage);
        });

        container.getChildren().addAll(backButton, questionHeader, questionBox, answersHeader, answerListContainer, addAnswerBox);

        // Set up the Scene and show the stage
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("View Answers");
        primaryStage.show();
    }
}
