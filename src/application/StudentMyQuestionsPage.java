package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * StudentMyQuestionsPage displays a list of questions posted by the current student. 
 */
public class StudentMyQuestionsPage {

    private final DatabaseHelper databaseHelper;
    private User currentUser;

    public StudentMyQuestionsPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
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

    /**
     * Displays the student's personal questions
     *
     * @param primaryStage The main application stage.
     * @param user         The currently logged-in student user.
     */
    public void show(Stage primaryStage, User user) {
        this.currentUser = user;

        // VBox for background with padding and background color
        VBox backgroundBox = new VBox(20);
        backgroundBox.setPadding(new Insets(20));
        backgroundBox.setStyle("-fx-background-color: #f3f2ef;");

        // Back button to return to the student home page
        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> new StudentHomePage(databaseHelper).show(primaryStage, currentUser));

        // Label for heading
        Label pageTitle = new Label("My Questions");
        pageTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // VBox that contains a list of all questions
        VBox questionsBox = new VBox(10);

        try {
            // Fetch all questions and filter for those by the current user
            ArrayList<Question> allQuestions = databaseHelper.getAllQuestions();
            ArrayList<Question> userQuestions = new ArrayList<>();
            for (Question q : allQuestions) {
                if (q.getAuthor().equals(currentUser.getUserName())) {
                    userQuestions.add(q);
                }
            }
            
            // Sort resolved first, then unresolved, by descending question ID
            userQuestions.sort((q1, q2) -> {
                if (q1.getResolved() != q2.getResolved()) {
                	return Boolean.compare(q2.getResolved(), q1.getResolved());
                }
                return Integer.compare(q2.getQuestionID(), q1.getQuestionID()); // newest first
            });

            // Call createQuestionBox for each question posted by current user
            if (userQuestions.isEmpty()) {
                questionsBox.getChildren().add(new Label("You have not posted any questions yet."));
            } else {
                for (Question question : userQuestions) {
                    VBox questionBox = createQuestionBox(question, primaryStage);
                    questionsBox.getChildren().add(questionBox);
                }
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            questionsBox.getChildren().add(new Label("Error loading questions."));
        }

        // ScrollPane to allow scrolling through list of questions
        ScrollPane scrollPane = new ScrollPane(questionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        backgroundBox.getChildren().addAll(backButton, pageTitle, scrollPane);

        // Set up the scene and show the stage
        Scene scene = new Scene(backgroundBox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Questions");
        primaryStage.show();
    }

    /**
     * Creates a VBox that contains the visual representation of a single question.
     *
     * @param question     The question to display.
     * @param primaryStage The primary stage of the application.
     * @return A VBox containing the question's details and action buttons.
     */
    private VBox createQuestionBox(Question question, Stage primaryStage) {
        // VBox for a single question
        VBox questionBox = new VBox(5);
        questionBox.setStyle("-fx-background-color: white; -fx-padding: 10;");
        
        // HBox to hold author string and resolved status
        HBox authorLine = new HBox(5);
        authorLine.setAlignment(Pos.CENTER_LEFT);
        
        // If the question is resolved add resolved label
        if (question.getResolved()) {
        	// Label for resolved status
        	Label resolvedLabel = new Label("[Resolved]");
            resolvedLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: green;");
            authorLine.getChildren().add(resolvedLabel);
        }

        // Label for the question text 
        Label questionText = new Label(question.getText());
        questionText.setStyle("-fx-font-size: 14px;");
        questionText.setWrapText(true);
        questionText.prefWidthProperty().bind(primaryStage.widthProperty().subtract(60));

        // HBox to contain answer, unread answers, and message counts
        HBox statsBox = new HBox(15);
        statsBox.setPadding(new Insets(5, 0, 5, 0));
        try {
            // Answer count
            int answerCount = databaseHelper.getAllAnswers(question.getQuestionID()).size();
            Label answersLabel = new Label("Answers: " + answerCount);

            // Unread answer count
            ArrayList<Answer> answers = databaseHelper.getAllAnswers(question.getQuestionID());
            int unreadCount = 0;
            for (Answer a : answers) {
                if (!a.getViewed()) {
                    unreadCount++;
                }
            }
            Label unreadLabel = new Label("Unread: " + unreadCount);
            
            // If their are unread messages make text bold
            if (unreadCount > 0) {
                unreadLabel.setStyle("-fx-font-weight: bold;");
            }

            // Private messages count
            //HashMap<String, ArrayList<Message>> messages = question.getMessages();
            //int messageCount = (messages != null) ? messages.values().stream().mapToInt(ArrayList::size).sum() : 0;
            int conversationCount = 0;
            int unreadMessageCount = 0;
            try {
            	ArrayList<PrivateMessage> privateMessages = databaseHelper.getAllPrivateMessages();
            	ArrayList<User> users = databaseHelper.getAllUsers();
            	ArrayList<User> userSubset = new ArrayList<User>();
            	for (PrivateMessage privateMessage : privateMessages) {
            		//Check if private message is being received by the currentUser, from a user that isn't in the local list.
            		//The user is added to the list and the conversation count is incremented if they aren't already in the list
            		if (privateMessage.getReceiver().equals(currentUser.getUserName()) && privateMessage.getQuestionID() == question.getQuestionID()) {
    	        		if(!userInList(privateMessage.getSender(), userSubset)) {
    	        			for (User user : users) {
    	        				if (user.getUserName().equals(privateMessage.getSender())) {
    	        					userSubset.add(user);
    	        					conversationCount++;
    	        					break;
    	        				}
    	        			}
    	        		}
            		}
            		//Check if private message is being received by the currentUser and hasn't been seen
            		//If it hasn't been seen, increment the unreadMessageCount
            		if (privateMessage.getReceiver().equals(currentUser.getUserName()) && !privateMessage.getSeenStatusReceiver() && privateMessage.getQuestionID() == question.getQuestionID()) {
            			unreadMessageCount++;
            		}
            	}
            } catch (SQLException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
            Label conversationLabel = new Label("Conversations: " + conversationCount);
            Label unreadMessagesLabel = new Label("Unread Messages: " + unreadMessageCount);
            statsBox.getChildren().addAll(answersLabel, unreadLabel, conversationLabel, unreadMessagesLabel);

        } catch (SQLException e) {
            statsBox.getChildren().add(new Label("Could not load stats."));
        }


        // HBox to contain view, edit and delete buttons
        HBox buttonBox = new HBox(10);

        // Button to view answers for the question
        Button viewAnswersButton = new Button("View Answers");
        viewAnswersButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0077B5;");
        viewAnswersButton.setOnAction(e -> {
            new StudentMyQuestionPage(databaseHelper).show(primaryStage, currentUser, question.getQuestionID());
        });
        buttonBox.getChildren().add(viewAnswersButton);

        // Edit and delete buttons are only shown if the current user is the author
        if (currentUser.getUserName().equals(question.getAuthor())) {
            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #555;"); 
            editButton.setOnAction(e -> {
                // Create a new stage for the edit window
                Stage editStage = new Stage();
                VBox editLayout = new VBox(10);
                editLayout.setPadding(new Insets(15));

                // TextArea pre-filled with orginal question text
                TextArea editTextArea = new TextArea(question.getText());
                editTextArea.setWrapText(true);
                editTextArea.setPrefSize(400, 150);

                // Save button to submit the changes
                Button saveButton = new Button("Save");
                saveButton.setOnAction(saveEvent -> {
                    String newText = editTextArea.getText();
                    if (!newText.isEmpty()) {
                        try {
                            // Update the question in the database
                            databaseHelper.updateQuestion(question.getQuestionID(), newText);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        show(primaryStage, currentUser); 
                        editStage.close(); 
                    }
                });

                editLayout.getChildren().addAll(new Label("Edit your question:"), editTextArea, saveButton);
                Scene editScene = new Scene(editLayout);
                editStage.setScene(editScene);
                editStage.setTitle("Edit Question");
                editStage.show();
            });

            // Button to delete the question
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #D32F2F;");
            deleteButton.setOnAction(a -> {
                try {
                    // Delete the question from the database
                    databaseHelper.deleteQuestion(question.getQuestionID());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                show(primaryStage, currentUser);
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
    			// TODO Auto-generated catch block
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

        questionBox.getChildren().addAll(authorLine, questionText, statsBox, buttonBox);
        
        return questionBox;
    }
}
