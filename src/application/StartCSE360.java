package application;

import javafx.application.Application;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;


public class StartCSE360 extends Application {

	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	
	public static void main( String[] args )
	{
		 launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        try {
            databaseHelper.connectToDatabase(); // Connect to the database
            /* Testing Code with initial users. DEBUGGING CODE

			*/
            databaseHelper.register(new User("student1", "123", "student"));
            databaseHelper.register(new User("student2", "123", "student"));
            databaseHelper.register(new User("student3", "123", "student"));
            databaseHelper.register(new User("student4", "123", "student"));
            databaseHelper.register(new User("student5", "123", "student"));
            databaseHelper.register(new User("student6", "123", "student"));
            databaseHelper.register(new User("user", "123", "user"));
            databaseHelper.register(new User("admin", "123", "admin"));
            
            if (databaseHelper.isDatabaseEmpty()) {
            	
            	new FirstPage(databaseHelper).show(primaryStage);
            } else {
            	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
                
            }
        } catch (SQLException e) {
        	System.out.println(e.getMessage());
        }
    }
	

}
