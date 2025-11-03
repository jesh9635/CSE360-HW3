package application;

/**
 * The Answer class represents a single answer object for a question.
 * Object contains an answer ID, text, author, parent question id,
 * viewed status, and resolution flag.
 */
public class Answer {
	private String author;
	private String answerText;
	private boolean resolving;
	private boolean viewed;
	private int ansID;
	private int questionID;
	
	/**
     * Initializes Answer object
     *
     * @param author            The author of the answer.
     * @param answerText        The text of the answer.
     * @param resolving			Resolution status flag.
     * @param viewed			Answer seen status flag.
     * @param answerId          The id for the answer.
     * @param parentQuestionId  The id of the original question
     */
	public Answer(String author, String answerText, boolean resolving, boolean viewed, int ansID, int parentQuestionID) {
		this.author = author;
		this.answerText = answerText;
		this.resolving = resolving;
		this.viewed = viewed;
		this.ansID = ansID;
		this.questionID = parentQuestionID;
	}
	
	// Returns the author of the answer
	public String getAuthor() {
		return author;
	}
	
	// Returns answer as string
	public String getAnswerText() {
		return answerText;
	}
	
	// Returns resolved status
	public boolean getResolving() {
		return resolving;
	}
	
	// Returns viewed status
	public boolean getViewed() {
		return viewed;
	}
	
	// Returns id of answer object
	public int getID() {
		return ansID;
	}
	
	// Returns of id of original question
	public int getParentQuestionID() {
		return questionID;
	}
}
