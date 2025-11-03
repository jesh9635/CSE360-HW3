package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import application.User;
import application.Question;
import application.Answer;
import application.Message;
import application.PrivateMessage;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the users who must change password table
	    String oneTimePasswordTable = "CREATE TABLE IF NOT EXISTS oneTimePasswords ("
	    		+ "userName VARCHAR(255), "
	    		+ "password VARCHAR(255))";
	    statement.execute(oneTimePasswordTable);
	    
	    // Create the questions table. (Stores questions posted by users.)
	    String questionTable = "CREATE TABLE IF NOT EXISTS questions ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "userName VARCHAR(255), "
	    		+ "text VARCHAR(1000),"
	    		+ "resolved BOOLEAN DEFAULT FALSE,"
	    		+ "parentID INT DEFAULT 0)";
	    statement.execute(questionTable);
	    
	    // Create the answers table. (Stores answers related to questions.) 
	    String answerTable = "CREATE TABLE IF NOT EXISTS answers ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "questionID INT, "
	    		+ "userName VARCHAR(255), "
	    		+ "text VARCHAR(1000), "
	    		+ "resolving BOOLEAN DEFAULT FALSE, "
	    		+ "viewed BOOLEAN DEFAULT FALSE,"
	    		+ "FOREIGN KEY (questionID) REFERENCES questions(id) ON DELETE CASCADE)"; // Delete answers if question is deleted
	    statement.execute(answerTable);
	    
	    // Create the private messages table. (Stores private messages between users related to a question.)
	    String privateMessageTable = "CREATE TABLE IF NOT EXISTS privateMessages ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "message VARCHAR(1000),"
	    		+ "sender VARCHAR(255), "
	    		+ "receiver VARCHAR(255), "
	    		+ "questionID INT, "
	    		+ "seenStatusReceiver BOOLEAN DEFAULT FALSE, "
	    		+ "FOREIGN KEY (questionID) REFERENCES questions(id) ON DELETE CASCADE)"; // Delete private message if question is deleted
	    statement.execute(privateMessageTable);
	    
	}
	
	
	//-------CRUD Operations------------
	// 1. Create operations
	
	// Adds a question to database
	public boolean createQuestion(Question question) throws SQLException {
		String query;
		PreparedStatement pstmt;
		// If duplicate match return false
		if(doesQuestionExist(question)) {
			return false;
		}
		// otherwise insert new question in database and return true
		else {
			query = "INSERT INTO questions (userName, text, parentID) VALUES (?,?,?)";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, question.getAuthor());
			pstmt.setString(2, question.getText());
			pstmt.setInt(3,  question.getParentQuestionID());
			pstmt.executeUpdate();
			
			return true;
		}
	}

	public boolean createPrivateMessage(PrivateMessage privateMessage) throws SQLException {
		String query;
		PreparedStatement pstmt;
		
		// otherwise insert new question in database and return true
		query = "INSERT INTO privateMessages (message, sender, receiver, questionID) VALUES (?,?,?,?)";
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, privateMessage.getMessage());
		pstmt.setString(2, privateMessage.getSender());
		pstmt.setString(3, privateMessage.getReceiver());
		pstmt.setInt(4, privateMessage.getQuestionID());
		pstmt.executeUpdate();
		return true;
	}	

	// Adds an answer for a question to the database
	public boolean createAnswer(Answer answer) throws SQLException {
		// If there answer already exist return false
		if (doesAnswerExist(answer)) {
			return false;
		}
		// If the answer is empty, return false
		else if (answer.getAnswerText().equals("")) {
			return false;
		}
		// Otherwise insert answer into database and return true
		else {
			String insertAnswer = "INSERT INTO answers (questionID, text, userName) VALUES (?, ?, ?)";
			
	        try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer)) {
	            pstmt.setInt(1, answer.getParentQuestionID());
	            pstmt.setString(2, answer.getAnswerText());
	            pstmt.setString(3, answer.getAuthor());
	            pstmt.executeUpdate();
	        }
	        return true;
		}
	}
	
	
	// 2. Read operations
	
	// Returns all questions in database
	public ArrayList<Question> getAllQuestions() throws SQLException {
		ArrayList<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions ORDER BY id DESC";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Question q = new Question(
                        rs.getString("userName"),
                        rs.getString("text"),
                        rs.getBoolean("resolved"),
                        rs.getInt("id"),
                        rs.getInt("parentID")
                );
                questions.add(q);
            }
        }
        return questions;
	}
	
	// Returns all users in database
	public ArrayList<User> getAllUsers() throws SQLException {
		ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM cse360users ORDER BY userName";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                User u = new User(
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                users.add(u);
            }
        }
        return users;
	}
	
	
	//Returns all privateMessages in database
	public ArrayList<PrivateMessage> getAllPrivateMessages() throws SQLException {
		ArrayList<PrivateMessage> privateMessages = new ArrayList<>();
        String query = "SELECT * FROM privateMessages ORDER BY id ASC";
        try (ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                PrivateMessage p = new PrivateMessage(
                		rs.getString("message"),
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getInt("questionID"),
                        rs.getInt("id"),
                        rs.getBoolean("seenStatusReceiver")
                );
                privateMessages.add(p);
            }
        }
        return privateMessages;
	}
	// Returns a specific question by its ID
	public Question getQuestionByID(int questionID) throws SQLException {
		String query = "SELECT * FROM questions WHERE id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			Question q = new Question(
				rs.getString("userName"),
				rs.getString("text"),
				rs.getBoolean("resolved"),
				rs.getInt("id"),
				rs.getInt("parentID")
			);
			return q;
		}
		return null;
	}
	
	// Returns all answers for a specific question
    public ArrayList<Answer> getAllAnswers(int questionId) throws SQLException {
        ArrayList<Answer> answers = new ArrayList<>();
        String query = "SELECT * FROM answers WHERE questionID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, questionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Answer curr = new Answer(
                    		rs.getString("userName"),
                            rs.getString("text"),
                            rs.getBoolean("resolving"),
                    		rs.getBoolean("viewed"),
                            rs.getInt("id"),
                            rs.getInt("questionID")
                    );
                    answers.add(curr);
                }
            }
        }
        return answers;
    }
	
	 // Checks if a question with the same text already exists
    public boolean doesQuestionExist(Question question) throws SQLException {
        String query = "SELECT COUNT(*) FROM questions WHERE text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question.getText());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
		
	 // Checks if an answer with the same text already exists for a given question
    public boolean doesAnswerExist(Answer answer) throws SQLException {
        String query = "SELECT COUNT(*) FROM answers WHERE questionID = ? AND text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, answer.getParentQuestionID());
            pstmt.setString(2, answer.getAnswerText());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    // 3. Update operations
    
    // Updates the text of a question given its id
	public void updateQuestion(int id, String newText) throws SQLException {
		String updateText = "UPDATE questions SET text = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateText)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
	}
	
	public void updatePrivateMessage(PrivateMessage pm) throws SQLException {
		String updateText = "UPDATE privateMessages SET message = ?, sender = ?, receiver = ?, questionID = ?, seenStatusReceiver = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateText)) {
        	pstmt.setInt(6, pm.getPrivateMessageID());
        	pstmt.setString(1, pm.getMessage());
        	pstmt.setString(2, pm.getSender());
        	pstmt.setString(3, pm.getReceiver());
        	pstmt.setInt(4, pm.getQuestionID());
        	pstmt.setBoolean(5, pm.getSeenStatusReceiver());
            pstmt.executeUpdate();
        }
	}
	// Updates the text of an answer given the id
	public boolean updateAnswer(int answerID, String answerText) throws SQLException {
		String query;
		PreparedStatement pstmt;
		ResultSet rs;
		int questionID = -1;
		
		// get questionid
		query = "SELECT questionID FROM answers WHERE id = ?";
		pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, answerID);
		rs = pstmt.executeQuery();
		if(rs.next()) {
			questionID = rs.getInt("questionID");
		}
		else {
			return false;
		}
		
		// check for exact questionid, answertext match
		query = "SELECT * FROM answers WHERE questionID = ? AND text = ?";
		pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		pstmt.setString(2, answerText);
		rs = pstmt.executeQuery();
		if(rs.next()) {
			return false;
		}
		
		query = "UPDATE answers SET text = ? WHERE id = ?";
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, answerText);
		pstmt.setInt(2, answerID);
		pstmt.executeUpdate();
		return true;
	}

	// Updates the resolving status of a specific answer
	public void updateAnswerResolving(int answerID, boolean isResolving) throws SQLException {
		String query = "UPDATE answers SET resolving = ? WHERE id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setBoolean(1, isResolving);
		pstmt.setInt(2, answerID);
		pstmt.executeUpdate();
	}

	// Clears resolving status from all answers for a given question
	public void clearResolvingAnswer(int questionID) throws SQLException {
		String query = "UPDATE answers SET resolving = FALSE WHERE questionID = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		pstmt.executeUpdate();
	}

	// Marks a question as resolved
	public void markQuestionResolved(int questionID) throws SQLException {
		String query = "UPDATE questions SET resolved = TRUE WHERE id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		pstmt.executeUpdate();
	}
	
	// Marks a question as unresolved
	public void markQuestionUnresolved(int questionID) throws SQLException {
	    String query = "UPDATE questions SET resolved = FALSE WHERE id = ?";
	    PreparedStatement pstmt = connection.prepareStatement(query);
	    pstmt.setInt(1, questionID);
	    pstmt.executeUpdate();
	}
	
	// Marks all answers for a question as viewed
	public void markAnswersViewed(int questionID) throws SQLException {
		String query = "UPDATE answers SET viewed = TRUE WHERE questionID = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		pstmt.executeUpdate();
	}
	
	// 4. Delete operations
	
	
	// Deletes a question from the database
	public void deleteQuestion(int id) throws SQLException {
		String deleteQuestion = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuestion)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
	}
	
	//Deletes a privateMessage from the database
	public void deletePrivateMessage(int id) throws SQLException {
		String deletePrivateMessage = "DELETE FROM privateMessages WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deletePrivateMessage)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
	}
	
	// Deletes an answer from the database
	public boolean deleteAnswer(int answerID) throws SQLException {
		String query = "DELETE FROM answers WHERE id = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, answerID);
		pstmt.executeUpdate();
		return true;
	}
	
	// convoStarter is the person who sent the first private message, sender is who wrote the current message
	public boolean sendMessage(int questionID, String convoStarter, String sender, String text) throws SQLException {
		String query = "INSERT INTO messages (questionID, mainUser, userName, text) VALUES (?,?,?,?)";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, questionID);
		pstmt.setString(2, convoStarter);
		pstmt.setString(3, sender);
		pstmt.setString(4, text);
		pstmt.executeUpdate();
		return true;
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}
	
	// Deletes a user from the database.
	public void deleteUser(String userName) throws SQLException {
		String deleteUser = "DELETE FROM cse360users WHERE userName = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		}
	}
	
	// Removes a user's role from the database.
	public void deleteRole(String userName, String role) throws SQLException {
		String query = "DELETE FROM cse360users WHERE username = ? AND role = ?";
		try(PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		}
	}
	
	// Returns all user information from the database, as an ArrayList of users.
	public ArrayList<User> returnAllUsers() throws SQLException {
		
		ArrayList<User> users = new ArrayList<>();
		
		String query = "SELECT DISTINCT userName AS users FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()) {
			
			// Finds password of user.
			query = "SELECT password FROM cse360users WHERE userName = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, resultSet.getString("users"));
			ResultSet passwords = pstmt.executeQuery();
			String password = "";
			if(passwords.next()) {
				password = passwords.getString("password");
			}
			
			// Finds all roles of user.
			query = "SELECT role FROM cse360users WHERE userName = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, resultSet.getString("users"));
			ResultSet userRoles = pstmt.executeQuery();
			String roles = "";
			while(userRoles.next()) {
				roles += userRoles.getString("role"); // Return the role if user exists
	            roles += ",";
			}
			roles = roles.substring(0,roles.length()-1);
			User user = new User(resultSet.getString("users"),password,roles);
			users.add(user);
		}
		
		return users;
	}
	
	// Generates temporary password for a user.
	public String getTempPassword(String userName) throws SQLException {
		// Generates new password.
		String tempPass = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character temporary password
		
		// Updates cse360users table with new password.
		setPassword(userName, tempPass);
		
		String query = "SELECT userName FROM oneTimePasswords WHERE userName = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, userName);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			// Updates oneTimePasswords with new tempPass for provided user.
			query = "UPDATE oneTimePasswords SET password = ? WHERE userName = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, tempPass);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
		else {
			// Adds user to oneTimePasswords, with tempPass.
			query = "INSERT INTO oneTimePasswords (userName) VALUES (?)";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		}
		
		// Returns generated password.
		return tempPass;
	}
	
	// Sets new password for a user.
	public void setPassword(String userName, String password) throws SQLException {
		String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, password);
		pstmt.setString(2, userName);
		pstmt.executeUpdate();
	}
	
	// Checks if a user needs to change passwords.
	public boolean needsNewPassword(String userName) throws SQLException {
		String query = "SELECT userName FROM oneTimePasswords WHERE userName = ?";
		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setString(1, userName);
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			query = "DELETE FROM oneTimePasswords WHERE userName = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
			return true;
		}
		else {
			return false;
		}
	}
	
	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        String roles = "";
	        while (rs.next()) {
	            roles += rs.getString("role"); // Return the role if user exists
	            roles += ",";
	        }
	        roles = roles.substring(0,roles.length()-1);
	        return roles;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	        Timer timer = new Timer();
	        TimerTask task = new TimerTask() {
	        	@Override
	        	public void run() {
	        		try {
	        			String taskQuery = "DELETE FROM InvitationCodes WHERE code = ?";
	        			PreparedStatement taskpstmt = connection.prepareStatement(taskQuery);
	        			taskpstmt.setString(1, code);
	        			taskpstmt.executeUpdate();
	        		} catch(Exception e) {
	        			e.printStackTrace();
	        		}
	        	}
	        };
	        timer.schedule(task,20000);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
