package application;

import databasePart1.DatabaseHelper;
import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;

/**
 * Launcher for testing StudentMyQuestionsPage and StudentMyQuestionPage
 * using real DatabaseHelper methods and contained test data.
 */
public class TestStudentMyPages extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper db = new DatabaseHelper();

        try {
            db.connectToDatabase();

            // Create a test user
            User testUser = new User("student1", "Student", "Student");

            // Create test questions authored by student1
            Question q1 = new Question("student1", "According to the template there is 6 slots total for stand ups. Are we good on only having 4? Wasn't sure if there is a specific requirement for how many we need.", false, 1);
            Question q2 = new Question("student1", "Hello! I noticed that while the announcement posted says that there are random teams in the People tab on Canvas, when I go to that tab I see a set of empty groups instead. Are we going to have teams assigned to us or should we form teams ourselves?", false, 2);
            Question q3 = new Question("student1", "Hello! I know we aren't going to cover databases until later in the course, so I'm getting a little bit ahead of myself here, but I'm curious: When I entered my username and password in the foundational code program, where were these saved? Is the database stored locally or in a server? I've looked over the source code but I've never worked with a database before so I'm not entirely sure what I'm looking at.", false, 3);
            Question q4 = new Question("student1", "Are we writing this PasswordRecognizer.java from scratch? Can we use the code in the PasswordEvaluator?", false, 4);
            Question q5 = new Question("student1", "Hello guys, I noticed that I am missing a file named JavaFX SDK in my build path and I may have deleted it. Any suggestions on how I can have it back?", false, 5);
            Question q6 = new Question("student1", "Hello! Are we expected to write JUnit tests for HW1, or is the information shown by the displayDebuggingInfo() and displayInputState() methods sufficient?", false, 6);
            Question q7 = new Question("student1", "is this more like bullet points or are we excepted to write out paragraphs?", false, 7);
            Question q8 = new Question("student1", "Use this Class Project Team Formation thread to introduce yourself to the class and identify team members.\r\n"
            		+ "\r\n"
            		+ "Once you have decided on your teammates, please proceed to Canvas > People > Project Groups, where you will find 14 groups available. Please note that each group should have 5 members\r\n"
            		+ "\r\n"
            		+ "A successful project completion hinges on a well-rounded team, and we've outlined the essential skills that each team should encompass for optimal results:\r\n"
            		+ "\r\n"
            		+ "Java Programming: A foundation in Java programming is crucial for effective coding and implementation.\r\n"
            		+ "\r\n"
            		+ "UML Diagrams: Knowledge of creating UML diagrams ensures clear visualization of project structure and functionality.\r\n"
            		+ "\r\n"
            		+ "JIRA: Utilizing JIRA for task management and issue tracking will enhance project organization and accountability.\r\n"
            		+ "\r\n"
            		+ "Team Management: Effective team management involves sharing comprehensive design details, creating well-defined tasks, setting deadlines, and maintaining clear communication throughout the project.", false, 8);
            
            if (!db.doesQuestionExist(q1)) db.createQuestion(q1);
            if (!db.doesQuestionExist(q2)) db.createQuestion(q2);
            if (!db.doesQuestionExist(q3)) db.createQuestion(q3);
            if (!db.doesQuestionExist(q4)) db.createQuestion(q4);
            if (!db.doesQuestionExist(q5)) db.createQuestion(q5);
            if (!db.doesQuestionExist(q6)) db.createQuestion(q6);
            if (!db.doesQuestionExist(q7)) db.createQuestion(q7);
            if (!db.doesQuestionExist(q8)) db.createQuestion(q8);


            // Create test answers for question 1
            Answer a1 = new Answer("anonymous", "6 is the expected number", false, false, 101, 1);
            Answer a2 = new Answer("anonymous", "Ah I forgot to disable the first-day automatic announcement from last semester. That won't happen again. I'll send an announcement in a minute with the group forming details.", true, false, 102, 2);
            Answer a3 = new Answer("anonymous", "Windows: C:\\Users\\<user>\\FoundationDatabase.mv.db\r\n"
            		+ "Linux: /home/<user>/FoundationDatabase.mv.db\r\n"
            		+ "\r\n"
            		+ "Mac will be something similar at the root of your user. If you want to retest the initial admin sign in you have to delete the database, I've found it easiest to just do it manually by deleting that file", false, false, 101, 3);
            Answer a4 = new Answer("anonymous", "No, you don’t start from scratch. Copy the provided PasswordEvaluator into HW1 and modify it.", true, false, 102, 4);
            Answer a5 = new Answer("anonymous", "Follow this tutorial- https://canvas.asu.edu/courses/236306/files/113231786?module_item_id=17565852", false, false, 101, 5);
            Answer a6 = new Answer("anonymous", "We're using the latest for this course.", true, false, 102, 5);
            Answer a7 = new Answer("anonymous", "I would recommend renaming your file so that you can Identify the old and then redownload the full file from the videos. This way if you have new code, you can move it over without losing it, but you want to start on a good foot. ", false, false, 101, 7);
            Answer a8 = new Answer("anonymous", "Any method works", true, false, 102, 6);
            Answer a9 = new Answer("anonymous", "I think the idea is to agree on that sort of thing with your group. My take is that it should be technical but descriptive. So, something like a blend of both those things. What & Why (brief).", false, false, 101, 7);

            if (!db.doesAnswerExist(a1)) db.createAnswer(a1);
            if (!db.doesAnswerExist(a2)) db.createAnswer(a2);
            if (!db.doesAnswerExist(a3)) db.createAnswer(a3);
            if (!db.doesAnswerExist(a4)) db.createAnswer(a4);
            if (!db.doesAnswerExist(a5)) db.createAnswer(a5);
            if (!db.doesAnswerExist(a6)) db.createAnswer(a6);
            if (!db.doesAnswerExist(a7)) db.createAnswer(a7);
            if (!db.doesAnswerExist(a8)) db.createAnswer(a8);
            if (!db.doesAnswerExist(a9)) db.createAnswer(a9);

            // Launch page
            new StudentMyQuestionsPage(db).show(primaryStage, testUser);

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
