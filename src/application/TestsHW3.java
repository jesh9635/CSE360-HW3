package application;

import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * @author James
 * 
 */



public class TestsHW3 extends Application {
	/**
	 * This test attempt to create an answer and add it to the database.
	 * @param databaseHelper
	 */
	public static void test1(DatabaseHelper databaseHelper) {
		try {
	        String questionText = "What does this symbol mean";
	        String answerText = "That symbol means the remainder";
	        Question newQuestion = new Question("student1", questionText, false, 0, 0); //QuestionID = 0
	        databaseHelper.createQuestion(newQuestion);
	        ArrayList<Question> questionList = databaseHelper.getAllQuestions();
	        Answer newAnswer = new Answer("student2", answerText, false, false, 0, questionList.get(0).getQuestionID()); //QuestionID = 0
	        System.out.println("Test 1: Successfully create a new answer. (assertTrue)");
	        System.out.println(databaseHelper.createAnswer(newAnswer) ? "PASS" : "FAIL");
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * This test attempts to create a duplicate answer and show that it isn't allowed.
	 * @param databaseHelper
	 */
	public static void test2(DatabaseHelper databaseHelper) {
		try {
	        String answerText = "That symbol means the remainder";
	        ArrayList<Question> questionList = databaseHelper.getAllQuestions();
	        Answer newAnswer = new Answer("student2", answerText, false, false, 0, questionList.get(0).getQuestionID()); //QuestionID = 0
	        System.out.println("Test 2: Unsuccessfully attempt to create a duplicate answer (assertFalse)");
	        System.out.println(databaseHelper.createAnswer(newAnswer) ? "FAIL" : "PASS");    
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	}
	/**
	 * This test attempts to update an existing answer with a new text value.
	 * @param databaseHelper
	 */
	public static void test3(DatabaseHelper databaseHelper) {
		try {
	        //Test 3: Update the answer with a new value and return its status
	        String updatedAnswerText = "That symbol actually means the division between two numbers";
	        ArrayList<Question> questionList = databaseHelper.getAllQuestions();
	        ArrayList<Answer> answerList = databaseHelper.getAllAnswers(questionList.get(0).getQuestionID());
	        System.out.println("Test 3: Successfully update an existing answer (assertTrue)");
	        System.out.println(databaseHelper.updateAnswer(answerList.get(0).getID(), updatedAnswerText) ? "PASS" : "FAIL");
	            
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * This test attempts to delete an existing answer from the database.
	 * @param databaseHelper
	 */
	public static void test4(DatabaseHelper databaseHelper) {
		try {
	        ArrayList<Question> questionList = databaseHelper.getAllQuestions();
	        ArrayList<Answer> answerList = databaseHelper.getAllAnswers(questionList.get(0).getQuestionID());
	        System.out.println("Test 4: Successfully delete an existing answer (assertTrue)");
	        System.out.println(databaseHelper.deleteAnswer(answerList.get(0).getID()) ? "PASS" : "FAIL");
	        	    
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	}
	/**
	 * This test attempts to submit an empty answer to the database and show that it isn't allowed.
	 * @param databaseHelper
	 */
	public static void test5(DatabaseHelper databaseHelper) {
		try {
	        ArrayList<Question> questionList = databaseHelper.getAllQuestions();
	        ArrayList<Answer> answerList = databaseHelper.getAllAnswers(questionList.get(0).getQuestionID());
	        Answer emptyAnswer = new Answer("student2", "", false, false, 0, questionList.get(0).getQuestionID()); //QuestionID = 0
	        System.out.println("Test 5: Unsuccessfully attempt to submit an empty answer (assertFalse)");
	        System.out.println(databaseHelper.createAnswer(emptyAnswer) ? "FAIL" : "PASS"); //Empty answer
	        		    
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	}
	
    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper databaseHelper = new DatabaseHelper();

        try {
            databaseHelper.connectToDatabase();

            // Create a test user
            User student1 = new User("student1", "123", "student");
            databaseHelper.register(student1);
            databaseHelper.register(new User("student2", "123", "student"));
            //Automated Tests
            test1(databaseHelper);
            test2(databaseHelper);
            test3(databaseHelper);
            test4(databaseHelper);
            test5(databaseHelper);

            // Launch page
            //new StudentMyQuestionsPage(databaseHelper).show(primaryStage, student1);

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}


