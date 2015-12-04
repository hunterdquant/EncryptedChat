/**
 * 
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * A graphical user interface for file encryption.
 */

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileSenderAndDecryptor extends Application {

	private ServerApplication.ServerThread server;
	private ClientApplication.ClientThread client;
	
	private int key;
	
	FileSenderAndDecryptor(Thread t, int key) {
		this.key = key;
		if (t instanceof ServerApplication.ServerThread) {
			server = (ServerApplication.ServerThread)t;
			server = null;
		} else if (t instanceof ClientApplication.ClientThread) {
			client = (ClientApplication.ClientThread)t;
			server = null;
		} else {
			client = null;
			server = null;
		}
	}
	/* public methods */
	
	/**
	 * Displays and sets up all GUI elements.
	 * 
	 * @param primaryStage The main stage for the application.
	 */
	@Override
	public void start(Stage primaryStage) {
			
		primaryStage.setTitle("GUI File Encryptor");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));		
		
		// Button and click event for encryption.
		Button eButton = new Button("Send");
		eButton.setOnAction( event -> {
			// Get the respective files.
			File inputFile = getInputFile();
			File outputFile = getOutputFile();
			
			// If the don't exist report to the user.
			if (inputFile != null) {
				
				// Create the FileEncryptor and encrypt the file.
				FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath(), key);
				fe.textEncrypt();
				// Display information to the user.
				if (server != null) {
					server.writeFile(outputFile);
				} else if (client != null) {
					client.writeFile(outputFile);
				}
			}
		});
		
		Button dButton = new Button("Decrypt");
		dButton.setOnAction( event -> {
			
			try {
				File inputFile = getInputFile();
				File outputFile = getOutputFile();
				
				if (inputFile != null && outputFile != null) {
					
					FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath(), key);
					fe.textDecrypt();
				}
			} catch (NumberFormatException nfe) {
				
			}
		});
		
		// Add the gui elements.
		grid.add(eButton, 0, 0);
		grid.add(dButton, 0, 1);
		
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