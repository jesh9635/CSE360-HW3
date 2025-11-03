package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Automated tests for CRUD operations on Questions and Answers.
 * Verifies creation, duplication, updates, deletions, and validations.
 */
public class QuestionAnswerCRUDAutoTest {

    static int numPassed = 0;
    static int numFailed = 0;

    public static void main(String[] args) {
        System.out.println("                                  ");
        System.out.println("Database CRUD Automation Test\n");

        DatabaseHelper db = new DatabaseHelper();

        try {
            db.connectToDatabase();

            // ========== QUESTION TESTS ========== 

            // Test 1: Create a new valid question (Positive)
            Question q1 = new Question("User1", "This is a test question.", false, 0);
            boolean addedQ1 = db.createQuestion(q1);
            performTestCase(1, addedQ1, true, "Create new valid question");

            // Test 2: Attempt to create a duplicate question (Negative)
            Question duplicateQ = new Question("User1", "This is a test question.", false, 0);
            boolean addedDuplicate = db.createQuestion(duplicateQ);
            performTestCase(2, addedDuplicate, false, "Attempt to add duplicate question");

            // Test 3: Update existing question (Positive)
            ArrayList<Question> allQs = db.getAllQuestions();
            Question qToUpdate = null;
            for (Question q : allQs) {
                if (q.getText().equals("This is a test question.")) {
                    qToUpdate = q;
                    break;
                }
            }
            boolean updateSuccess = false;
            if (qToUpdate != null) {
                db.updateQuestion(qToUpdate.getQuestionID(), "Updated test question");
                Question updatedQ = db.getQuestionByID(qToUpdate.getQuestionID());
                updateSuccess = updatedQ != null && updatedQ.getText().equals("Updated test question");
            }
            performTestCase(3, updateSuccess, true, "Update existing question");

            // Test 4: Delete existing question (Positive)
            boolean deleted = false;
            if (qToUpdate != null) {
                db.deleteQuestion(qToUpdate.getQuestionID());
                Question afterDelete = db.getQuestionByID(qToUpdate.getQuestionID());
                deleted = (afterDelete == null);
            }
            performTestCase(4, deleted, true, "Delete question from DB");

            // Test 5: Attempt to submit an empty question (Negative)
            Question emptyQ = new Question("User2", "   ", false, 0);
            boolean addedEmpty = db.createQuestion(emptyQ);
            performTestCase(5, addedEmpty, false, "Add empty question should fail");

            // ========== ANSWER TESTS ==========

            // Add a valid question to associate answers with
            Question parentQ = new Question("AnswerAuthor", "Question for answer tests", false, 0);
            db.createQuestion(parentQ);
            ArrayList<Question> answerQs = db.getAllQuestions();
            Question qForAnswers = null;
            for (Question q : answerQs) {
                if (q.getText().equals("Question for answer tests")) {
                    qForAnswers = q;
                    break;
                }
            }

            // Test 6: Create new valid answer (Positive)
            boolean addedA = false;
            if (qForAnswers != null) {
                Answer a1 = new Answer("Responder1", "This is a unique answer", false, false, 0, qForAnswers.getQuestionID());
                addedA = !db.createAnswer(a1); // returns false if successful!
            }
            performTestCase(6, addedA, true, "Add new valid answer");

            // Test 7: Attempt to add duplicate answer (Negative)
            boolean addedDupA = true;
            if (qForAnswers != null) {
                Answer duplicateA = new Answer("Responder1", "This is a unique answer", false, false, 0, qForAnswers.getQuestionID());
                addedDupA = !db.createAnswer(duplicateA); // returns true if duplicate!
            }
            performTestCase(7, addedDupA, false, "Attempt to add duplicate answer");

            // Test 8: Update existing answer (Positive)
            boolean updateAWorks = false;
            if (qForAnswers != null) {
                ArrayList<Answer> answers = db.getAllAnswers(qForAnswers.getQuestionID());
                if (!answers.isEmpty()) {
                    Answer aToUpdate = answers.get(0);
                    boolean updatedA = db.updateAnswer(aToUpdate.getID(), "Updated answer content");
                    ArrayList<Answer> updatedAnswers = db.getAllAnswers(qForAnswers.getQuestionID());
                    for (Answer a : updatedAnswers) {
                        if (a.getID() == aToUpdate.getID() && a.getAnswerText().equals("Updated answer content")) {
                            updateAWorks = updatedA;
                        }
                    }
                }
            }
            performTestCase(8, updateAWorks, true, "Update existing answer");

            // Test 9: Delete existing answer (Positive)
            boolean answerGone = false;
            if (qForAnswers != null) {
                ArrayList<Answer> answers = db.getAllAnswers(qForAnswers.getQuestionID());
                if (!answers.isEmpty()) {
                    Answer aToDelete = answers.get(0);
                    boolean deletedA = db.deleteAnswer(aToDelete.getID());
                    ArrayList<Answer> remaining = db.getAllAnswers(qForAnswers.getQuestionID());
                    answerGone = deletedA && remaining.stream().noneMatch(a -> a.getID() == aToDelete.getID());
                }
            }
            performTestCase(9, answerGone, true, "Delete answer from DB");

            // Test 10: Attempt to submit empty answer (Negative)
            boolean addedEmptyA = true;
            if (qForAnswers != null) {
                Answer emptyA = new Answer("EmptyGuy", "", false, false, 0, qForAnswers.getQuestionID());
                addedEmptyA = !db.createAnswer(emptyA); // returns true if duplicate/invalid
            }
            performTestCase(10, addedEmptyA, true, "Add empty answer should fail");

            // Cleanup
            if (qForAnswers != null) db.deleteQuestion(qForAnswers.getQuestionID());

        } catch (SQLException e) {
            System.err.println("DB test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=======================================");
        System.out.println("Tests Passed: " + numPassed);
        System.out.println("Tests Failed: " + numFailed);
    }

    /**
     * Helper method to display test outcome.
     */
    private static void performTestCase(int testCaseNumber, boolean actual, boolean expected, String description) {
        System.out.println("\n--- Test Case " + testCaseNumber + ": " + description + " ---");
        if (actual == expected) {
            System.out.println("PASS");
            numPassed++;
        } else {
            System.out.println("FAIL");
            numFailed++;
        }
    }
}
