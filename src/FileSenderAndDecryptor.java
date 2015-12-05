import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Retrieves and encrypts files to then send on the network, also decrypts files.
 * 
 * @author Hunter Quant
 */
public class FileSenderAndDecryptor extends Application {
	
	/* data members */
	
	/**
	 * The network to write the files to.
	 */
	private ClientServerChatApplication.NetworkThread networkThread;
	
	/**
	 * The key for encrypting and decrypting the files.
	 */
	private int key;
	
	/* constructors */
	
	/**
	 * Constructs a FileSenderAndDecryptor.
	 * 
	 * @param networkThread The network to write the files to.
	 * @param key The key for encrypting and decrypting the files.
	 */
	public FileSenderAndDecryptor(ClientServerChatApplication.NetworkThread networkThread, int key) {
		this.networkThread = networkThread;
		this.key = key;
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
		
		// Button and click event for sending a file.
		Button sButton = new Button("Send");
		sButton.setMinSize(100, 50);
		sButton.setOnAction( event -> {
			// Get the respective files.
			File inputFile = getInputFile();
			File outputFile = getOutputFile();
			if (inputFile != null) {
				// Create the FileEncryptor and encrypt the file.
				FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath(), key);
				fe.textEncrypt();
				// Send the file to the network.
				networkThread.writeFile(outputFile);
			}
		});
		
		// Button and click event for decrypting a file.
		Button dButton = new Button("Decrypt");
		dButton.setMinSize(100, 50);
		dButton.setOnAction( event -> {
			File inputFile = getInputFile();
			File outputFile = getOutputFile();
			
			if (inputFile != null && outputFile != null) {
				
				FileEncryptor fe = new FileEncryptor(inputFile, outputFile.getAbsolutePath(), key);
				fe.textDecrypt();
			}
		});
		
		// Add the gui elements.
		grid.add(sButton, 0, 0);
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