import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This application is gets information about the connection for the chat and is thus the main application.
 * 
 * @author Hunter Quant
 */
public class EncryptedChat extends Application {
	
	/* public methods */
	
	/**
	 * Displays and sets up all GUI elements.
	 * 
	 * @param primaryStage The main stage for the application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
				// The name you would like to appear as.
				TextField name = new TextField();
				// Used to encrypt and decrypt. 
				TextField encryptionKey = new TextField();
				// Used for the client to connect to the server.
				TextField ipAddress = new TextField();
				// Used as the port for which the connection is established.
				TextField portNumber = new TextField();
				
				// Set window configurations.
				primaryStage.setTitle("Encrypted Chat Client");
				primaryStage.setResizable(false);
				GridPane grid = new GridPane();
				grid.setAlignment(Pos.CENTER);
				grid.setVgap(10);
				grid.setPadding(new Insets(25, 25, 25, 25));		
				
				// Starts up the server application if the server credentials are in valid form.
				Button serverButton = new Button("Run as server");
				serverButton.setOnAction( event -> {
					if (validateServerParameters(portNumber.getText(), encryptionKey.getText())) {
						ServerApplication server = new ServerApplication(name.getText(), portNumber.getText(), encryptionKey.getText());
						try {
							server.start(primaryStage);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				// Starts up the client application if the client credentials are in valid form.
				Button clientButton = new Button("Run as client");
				clientButton.setOnAction( event -> {
					if (validateClientParameters(portNumber.getText(), encryptionKey.getText(), ipAddress.getText())) {
						ClientApplication client = new ClientApplication(name.getText(), ipAddress.getText(), portNumber.getText(), encryptionKey.getText());
						try {
							client.start(primaryStage);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				// Add nodes to the pane.
				grid.add(name, 1, 0);
				grid.add(encryptionKey, 1, 1);
				grid.add(ipAddress, 1, 2);
				grid.add(portNumber, 1, 3);
				grid.add(new Text("Name: "), 0, 0);
				grid.add(new Text("Key:"), 0, 1);
				grid.add(new Text("IP:"), 0, 2);
				grid.add(new Text("Port:"), 0, 3);
				grid.add(serverButton, 0, 4);
				grid.add(clientButton, 0, 5);
				
				// Set dimensions and display.
				Scene scene = new Scene(grid, 400, 300);
				primaryStage.setScene(scene);
				primaryStage.show();
	}
	
	/**
	 * Verifies that the user input client information is in valid form.
	 * 
	 * @param port Must be between 1024 - 65535
	 * @param key Must by an unsigned byte 0-255
	 * @param ip Must be a valid IP
	 * @return true if valid else false
	 */
	public boolean validateClientParameters(String port, String key, String ip) {
		try {
			// Parse to int and range check.
			int n = Integer.parseInt(key);
			if ( n < 0 || n > 255) {
				return false;
			}
			// Parse to int and range check.
			n = Integer.parseInt(port);
			String [] bytes = ip.split("\\.");
			if (bytes.length != 4 || !(n >= 1024 && n <= 65535)) {
				return false;
			}
			// Parse each element of bytes and range check.
			for (String b : bytes) {
				n = Integer.parseInt(b);
				if ( n < 0 || n > 255) {
					return false;
				}
			}
		} catch (NumberFormatException nfe) {
			// If the numbers could not be parse, then the information if incorrect.
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Verifies that the user input server information is in valid form.
	 * 
	 * @param port Must be between 1024 - 65535
	 * @param key Must by an unsigned byte 0-255
	 * @return true if valid else false
	 */
	public boolean validateServerParameters(String port, String key) {
		try {
			// Parse to int and range check.
			int n = Integer.parseInt(key);
			if ( n < 0 || n > 255) {
				return false;
			}
			// Parse to int and range check.
			n = Integer.parseInt(port);
			if (!(n >= 1024 && n <= 65535)) {
				return false;
			}
		} catch (NumberFormatException nfe) {
			// If the numbers could not be parse, then the information if incorrect.
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
