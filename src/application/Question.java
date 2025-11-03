package application;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Question class defines a single question in the Q&A system
 * It holds the question author, text, resolution status, seen status, and ID
 */
public class Question {
	private String author;
	private String text;
	private boolean resolved;
	private int questionID;
	private int parentQuestionID;
	private HashMap<String, ArrayList<Message>> privateMessages;
	
	/**
     * Initializes new Question object
     * 
     * @param author          The author of the question.
     * @param text            The text of the question.
     * @param resolved 		  Resolution status flag.
     * @param questionID      The unique id for the question.
     */
	public Question(String author, String text, boolean resolved, int questionID) {
		this.author = author;
		this.text = text;
		this.resolved = resolved;
		this.questionID = questionID;
		parentQuestionID = 0;
	}
	
	public Question(String author, String text, boolean resolved, int questionID, int parentQuestionID) {
		this.author = author;
		this.text = text;
		this.resolved = resolved;
		this.questionID = questionID;
		this.parentQuestionID = parentQuestionID;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getText() {
		return text;
	}
	
	public boolean getResolved() {
		return resolved;
	}
	
	public int getQuestionID() {
		return questionID;
	}
	
	public int getParentQuestionID() {
		return parentQuestionID;
	}
	
	public HashMap<String, ArrayList<Message>> getMessages() {
		return privateMessages;
	}
}
