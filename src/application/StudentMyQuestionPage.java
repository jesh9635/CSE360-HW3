package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The StudentMyQuestionPage class displays the full details of a question posted by the student.
 * It shows all answers and private messages related to the question.
 * Students can mark an answer as resolving and navigate to private message threads.
 */
public class StudentMyQuestionPage {

    private final DatabaseHelper databaseHelper;

    /**
     * Initializes StudentMyQuestionPage with the database helper.
     * databaseHelper - The helper class for database operations.
     */
    public StudentMyQuestionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the question detail page for a given question.
     * primaryStage - The primary stage for this application.
     * currentUser - The user who is currently logged in.
     * questionID - The ID of the question to display.
     */
    public void show(Stage primaryStage, User currentUser, int questionID) {
    	
    	// Mark all answers as viewed when student opens the question
    	try {
    		databaseHelper.markAnswersViewed(questionID);
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f3f2ef;");

        // Back button to return to StudentMyQuestionsPage
        Button backButton = new Button("Back to My Questions");
        backButton.setOnAction(e -> {
            new StudentMyQuestionsPage(databaseHelper).show(primaryStage, currentUser);
        });

        // Get question details from database
        Question question;
        try {
            question = databaseHelper.getQuestionByID(questionID);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // Display question title and description
        Label questionTitle = new Label("Question:");
        questionTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        Label questionText = new Label(question.getText());
        questionText.setWrapText(true);
        questionText.setStyle("-fx-font-size: 14px;");

        // VBox for question section
        VBox questionBox = new VBox(10);
        questionBox.setStyle("-fx-background-color: white; -fx-padding: 15;");
        questionBox.getChildren().addAll(questionTitle, questionText);

        // Label for answers section
        Label answersHeader = new Label("Answers:");
        answersHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // VBox to contain answer rows
        VBox answersContainer = new VBox(10);

        // Get all answers for the question
        ArrayList<Answer> answerList;
        try {
            answerList = databaseHelper.getAllAnswers(questionID);
            // Sort resolved answers on top
            answerList.sort((a1, a2) -> Boolean.compare(!a1.getResolving(), !a2.getResolving()));
            
            for (Answer answer : answerList) {
                VBox answerBox = new VBox(5);
                answerBox.setStyle("-fx-background-color: white; -fx-padding: 10;");

                // Label for author
                Label authorLabel = new Label("Answered by: " + answer.getAuthor());
                authorLabel.setStyle("-fx-font-size: 12px;");

                // Label for answer text
                Label answerText = new Label(answer.getAnswerText());
                answerText.setWrapText(true);
                answerText.setStyle("-fx-font-size: 14px;");

                // Highlight resolving answer
                if (answer.getResolving()) {
                    answerBox.setStyle("-fx-background-color: #d0f0c0; -fx-padding: 10;");
                }

                // Logic to toggle resolving answer on click
                answerBox.setOnMouseClicked(event -> {
                    try {
                        if (answer.getResolving()) {
                            // If already resolved, clear it
                            databaseHelper.clearResolvingAnswer(questionID);
                            databaseHelper.markQuestionUnresolved(questionID);
                        } else {
                            // Clear any previous resolution
                            databaseHelper.clearResolvingAnswer(questionID);

                            // Mark this answer as resolving
                            databaseHelper.updateAnswerResolving(answer.getID(), true);
                            databaseHelper.markQuestionResolved(questionID);
                        }

                        // Refresh the page
                        show(primaryStage, currentUser, questionID);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                answerBox.getChildren().addAll(authorLabel, answerText);
                answersContainer.getChildren().add(answerBox);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        container.getChildren().addAll(backButton, questionBox, answersHeader, answersContainer);

        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Question Details");
        primaryStage.show();
    }

}
