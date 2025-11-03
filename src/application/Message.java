package application;

public class Message {
	private String mainUser;
	private String sender;
	private String messageText;
	private boolean viewed;
	
	public Message(String mainUser, String sender, String messageText, boolean viewed) {
		this.mainUser = mainUser;
		this.sender = sender;
		this.messageText = messageText;
		this.viewed = viewed;
	}
	
	public String getMainUser() {
		return mainUser;
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getMessageText() {
		return messageText;
	}
	
	public boolean getViewed() {
		return viewed;
	}
}
