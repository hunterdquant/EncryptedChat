import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
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

public class ClientApplication extends Application {

	public int port;
	public String ip;
	public int key;
	
	private String name; 
	
	private ClientThread client;
	
	private final TextArea inputArea = new TextArea();
	private final TextArea chatArea = new TextArea();
	
	public ClientApplication(String name, String ip, String port, String key) {
		try {
			this.name = name + " > ";
			this.port = Integer.parseInt(port);
			System.out.println(this.port);
			this.ip = ip;
			this.key = Integer.parseInt(key);
		} catch (NumberFormatException nfe) {
			// Defaults
			this.port = 1995;
			this.key = 64;
		}
		client = new ClientThread();
		client.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Encrypted Chat Server");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		chatArea.setPrefSize(700, 250);
		chatArea.setMaxSize(700, 250);
		chatArea.setMinSize(700, 250);
		chatArea.setEditable(false);
		chatArea.setWrapText(true);
		inputArea.setPrefSize(700, 100);
		inputArea.setMaxSize(700, 100);
		inputArea.setMinSize(700, 100);
		inputArea.setWrapText(true);
		inputArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						String message = inputArea.getText();
						chatArea.appendText(name + message + '\n');
						client.writeMessage(message);
						inputArea.clear();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								inputArea.positionCaret(0);
							}
						});
					}
				}
			});
		
		Button sendButton = new Button("Send");
		sendButton.setOnAction( event -> {
			String message = inputArea.getText();
			chatArea.appendText(name + message + '\n');
			client.writeMessage(message);
			inputArea.clear();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					inputArea.positionCaret(0);
				}
			});
		});
		
		grid.add(chatArea, 0, 0);
		grid.add(inputArea, 0, 1);
		grid.add(sendButton, 1, 1);
		// Set the column span of the status message.
		//GridPane.setColumnSpan(textFlow, 3);
		
		// Set dimensions and display.
		Scene scene = new Scene(grid, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest( event -> {
			client.close();
			client.interrupt();
		});
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	private void addText(String s) {
		Platform.runLater(new Runnable() { 
			@Override
			public void run(){
				chatArea.appendText(s);;
			}
		});
	}
	
	class ClientThread extends Thread {

		private Socket socket;

		private PrintWriter writer;
		private	BufferedReader reader;
		
		public ClientThread() {
			socket = null;
		}
		
		@Override
		public void run() {
			
			while (!Thread.currentThread().isInterrupted()) {
				try {
					socket = new Socket(ip, port);
					reader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream(), true);
					String message = null;
					while ((message = reader.readLine()) != null) {
						if (message.contains("<*message>")) {
							readMessage(message.substring(11));
						}
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		private void readMessage(String message) {
			SimpleEncryptor enc = new SimpleEncryptor(key);
			enc.setEncryptedMessage(message);
			enc.textDecrypt();
			addText(enc.getClearText() + '\n');
		}
		
		private void readFile(Socket socket) {
			
		}
		
		private void writeMessage(String message) {
			
			SimpleEncryptor enc = new SimpleEncryptor(key);
			enc.setClearText(name + message);
			enc.textEncrypt();
			writer.println("<*message*> " + enc.getEncryptedMessage());
		}
		
		private void writeFile() {
			
		}

		private void close() {
			try {
				if (socket != null) {
					socket.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
