/**
 * 
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * A graphical user interface for file encryption.
 */

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GuiFileEncrypt extends Application {

	/* Main */
	
	public static void main(String[] args) {
		launch(args);
	}
	
	/* public methods */
	
	/**
	 * The start method for the GUI application.
	 */
	@Override
	public void start(Stage primaryStage) {

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
			// Get the respective files.
			File inputFile = getInputFile();
			File outputFile = getOutputFile();
			
			// If the don't exist report to the user.
			if (inputFile != null && outputFile != null) {
				
				// Create the FileEncryptor and encrypt the file.
				FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath());
				fe.textEncrypt();
				// Display information to the user.
				outputKey.setText("" + fe.getEncryptionKey());
				message.setText("Your file has been encrypted!");
			} else {
				message.setText("You failed to properly select files.");
			}
		});
		
		Button dButton = new Button("Decrypt");
		dButton.setOnAction( event -> {
			
			try {
				// Try to parse the user input.
				byte key = Byte.parseByte(inputKey.getText());
				File inputFile = getInputFile();
				File outputFile = getOutputFile();
				
				if (inputFile != null && outputFile != null) {
					
					FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath(), key);
					fe.textDecrypt();
					message.setText("Your file has been decrypted!");
				} else if (key < 0 || key > 127) {
					// Keys must be between 0-127
					message.setText("You entered an invalid key! 0-127.");
				} else {
					message.setText("You failed to properly select files.");
				}
			} catch (NumberFormatException nfe) {
				// Report to the user that they entered invalid input.
				message.setText("You entered an invalid key! 0-127");
			}
		});
		
		// Add the gui elements.
		grid.add(eButton, 0, 0);
		grid.add(dButton, 0, 1);
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
	
	/**
	 * Uses a FileChooser to select a file to recieve input from.
	 *
	 * @return the file to recieve input from.
	 */
	public File getInputFile() {
		Stage stage = new Stage();
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select a file to be encrypted/decrypted.");
		// Dialog to open files.
		File inputFile = chooser.showOpenDialog(stage);
		return inputFile;
	}
	
	/**
	 * Uses a FileChooser to select a file to write output to.
	 * 
	 * @return
	 */
	public File getOutputFile() {
		Stage stage = new Stage();
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Select a file to save the encrypted/decrypted output.");
		// Dialog to create or save files.
		File inputFile = chooser.showSaveDialog(stage);
		return inputFile;
	}
}