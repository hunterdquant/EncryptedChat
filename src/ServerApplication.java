import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
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
	
	public String name;
	
	private ServerThread server;
	
	private final TextArea inputArea = new TextArea();
	private final TextArea chatArea = new TextArea();
	
	public ServerApplication(String name, String port, String key) {
		try {
			this.name = name + " > ";
			this.port = Integer.parseInt(port);
			System.out.println(this.port);
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
						server.writeMessage(message);
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
			server.writeMessage(message);
			inputArea.clear();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					inputArea.positionCaret(0);
				}
			});
		});
		
		Button fileButton = new Button("File");
		fileButton.setOnAction( event -> {
			FileSenderAndDecryptor  fsad = new FileSenderAndDecryptor(server, key);
			fsad.start(new Stage());
		});
		
		grid.add(chatArea, 0, 0);
		grid.add(inputArea, 0, 1);
		grid.add(sendButton, 1, 1);
		grid.add(fileButton, 1, 2);
		// Set the column span of the status message.
		//GridPane.setColumnSpan(textFlow, 3);
		
		// Set dimensions and display.
		Scene scene = new Scene(grid, 800, 400);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest( event -> {
			server.close();
			server.interrupt();
		});
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	private void addText(String s) {
		Platform.runLater(new Runnable() { 
			@Override
			public void run() {
				chatArea.appendText(s);
			}
		});
	}
	
	class ServerThread extends Thread {
		
		private ServerSocket serverSocket;
		private Socket socket;

		private PrintWriter writer;
		private BufferedReader reader;
		
		public ServerThread() {
			try {
				serverSocket = new ServerSocket(port);
				socket = null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			
			while (!Thread.currentThread().isInterrupted()) {
				try {
					socket = serverSocket.accept();
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
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		
		public void writeMessage(String message) {
			
			SimpleEncryptor enc = new SimpleEncryptor(key);
			enc.setClearText(name + message);
			enc.textEncrypt();
			writer.println("<*message> " + enc.getEncryptedMessage());
		}
		
		public void writeFile(File file) {
			
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
