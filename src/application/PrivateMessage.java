package application;


/**
 * The Question class defines a single question in the Q&A system
 * It holds the question author, text, resolution status, seen status, and ID
 */


public class PrivateMessage{
	//Set public and private static variables
	private String message;
	private String sender;
	private String receiver;
	private int questionID;
	private int privateMessageID;
	private boolean seenStatusReceiver;
	/**
     * Initializes new PrivateMessage object
     * 
     * @param sender              The sender of the private message.
     * @param receiver            The receiver of the private message.
     * @param questionID          The unique id of the question that the privateMessage relates to.
     * @param privateMessageID    The unique id for the privateMessage.
     * @param seenStatusSender    The status of whether message has been seen.
     * @param seenStatusReceiver  The status of whether message has been seen.
     */
	//Set public and private static functions
	public PrivateMessage(String message, String sender, String receiver, int questionID, int privateMessageID, boolean seenStatusReceiver) {
		this.message = message;
		this.sender = sender;
		this.receiver = receiver;
		this.questionID = questionID;
		this.privateMessageID = privateMessageID;
		this.seenStatusReceiver = seenStatusReceiver;
	}
	public String getMessage() {
		return message;
	}
	public String getSender() {
		return sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public int getQuestionID() {
		return questionID;
	}
	public int getPrivateMessageID() {
		return privateMessageID;
	}

	public boolean getSeenStatusReceiver() {
		return seenStatusReceiver;
	}
	public void setSeenStatusReceiver(boolean newValue) {
		this.seenStatusReceiver = newValue;
	}
}
