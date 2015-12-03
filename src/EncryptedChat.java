import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EncryptedChat extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
				// Used to get the encryption key from a user to decrypt a file.
				TextField encryptionKey = new TextField();
				
				TextField ipAddress = new TextField();
				
				TextField portNumber = new TextField();
				
				TextField name = new TextField();
					
				primaryStage.setTitle("Encrypted Chat Client");
				GridPane grid = new GridPane();
				grid.setAlignment(Pos.CENTER);
				grid.setVgap(10);
				grid.setPadding(new Insets(25, 25, 25, 25));		
				
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
	
	public boolean validateClientParameters(String port, String key, String ip) {
		try {
			int n = Integer.parseInt(key);
			if ( n < 0 || n > 255) {
				return false;
			}
			n = Integer.parseInt(port);
			String [] bytes = ip.split("\\.");
			System.out.println(ip);
			if (bytes.length != 4 || !(n >= 1024 && n <= 65535)) {
				return false;
			}
			for (String b : bytes) {
				n = Integer.parseInt(b);
				if ( n < 0 || n > 255) {
					return false;
				}
			}
		} catch (NumberFormatException nfe) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean validateServerParameters(String port, String key) {
		try {
			int n = Integer.parseInt(key);
			if ( n < 0 || n > 255) {
				return false;
			}
			n = Integer.parseInt(port);
			if (!(n >= 1024 && n <= 65535)) {
				return false;
			}
		} catch (NumberFormatException nfe) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
