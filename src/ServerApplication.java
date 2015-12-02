import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ServerApplication extends Application {
	
	public int port;
	public int key;
	
	public final String SERVER_NAME = "Mario > ";
	public final String CLIENT_NAME = "Luigi > ";
	
	private ServerThread server;
	
	private final TextArea textArea = new TextArea();
	private final TextFlow textFlow = new TextFlow();
	
	public ServerApplication(String port, String key) {
		try {
			this.port = Integer.parseInt(port);
			this.key = Integer.parseInt(key);
		} catch (NumberFormatException nfe) {
			// Defaults
			this.port = 1995;
			this.key = 64;
		}
		server = new ServerThread();
		server.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Encrypted Chat Server");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));		
		
		textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						textFlow.getChildren().add(new Text(textArea.getText()));
						textArea.clear();
					}
				}
			});
		
		Button sendButton = new Button("Send");
		sendButton.setOnAction( event -> {
			textFlow.getChildren().add(new Text(textArea.getText()));
			textArea.clear();
		});
		
		grid.add(textFlow, 0, 0);
		grid.add(textArea, 0, 1);
		grid.add(sendButton, 1, 1);
		// Set the column span of the status message.
		//GridPane.setColumnSpan(textFlow, 3);
		
		// Set dimensions and display.
		Scene scene = new Scene(grid, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	class ServerThread extends Thread {
		
		private ServerSocket serverSocket;
		
		public ServerThread() {
			try {
				serverSocket = new ServerSocket(port);			
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			
			while (true) {
				BufferedReader reader = null;
				
				try {
					Socket socket = serverSocket.accept();
					reader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String s = reader.readLine();
					if (s.equals("<*message*>")) {
						readMessage(reader);
					} else if (s.equals("<*file*>")) {
						readFile(socket);
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (reader  != null) {
						try {
							reader.close();
						} catch (IOException ioe) {
							// TODO Auto-generated catch block
							ioe.printStackTrace();
						}
					}
				}
			}
		}
		
		private void readMessage(BufferedReader reader) {
			String s = "";
			try {
				while ((s = reader.readLine()) != null && !(s.equals("<*file*>"))) {
					
				}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			}
		}
		
		private void readFile(Socket socket) {
			
		}
		
		private void writeMessage() {
			
		}
		
		private void writeFile() {
			
		}
	}
}
