import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ServerApplication extends Application {
	
	public String port;
	public String key;
	
	public ServerApplication(String port, String key) {
		this.port = port;
		this.key = key;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Used to get the encryption key from a user to decrypt a file.
		TextField inputKey = new TextField();
		// Displays operation status to the user.
		Text message = new Text("Welcome!");
		// Displays the encryption key for the file that was just encrypted.
		Text outputKey = new Text(); 
			
		primaryStage.setTitle("GUI File Encryptor");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));		
		
		// Button and click event for encryption.
		Button eButton = new Button("Encrypt");
		eButton.setOnAction( event -> {
			
		});
		
		// Add the gui elements.
		grid.add(eButton, 0, 0);
		grid.add(outputKey, 2, 0);
		grid.add(inputKey, 2, 1);
		grid.add(message, 0, 2);
		grid.add(new Text("Key:"), 1, 0);
		grid.add(new Text("Key:"), 1, 1);
		// Set the column span of the status message.
		GridPane.setColumnSpan(message, 3);
		
		// Set dimensions and display.
		Scene scene = new Scene(grid, 400, 200);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
