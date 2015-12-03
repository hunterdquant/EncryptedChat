import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
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
						String message = textArea.getText();
						textFlow.getChildren().add(new Text(SERVER_NAME + message + '\n'));
						server.writeMessage(message);
						textArea.clear();
					}
				}
			});
		
		Button sendButton = new Button("Send");
		sendButton.setOnAction( event -> {
			String message = textArea.getText();
			textFlow.getChildren().add(new Text(SERVER_NAME + message + '\n'));
			server.writeMessage(message);
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
		primaryStage.setOnCloseRequest( event -> {
			server.interrupt();
		});
		primaryStage.show();
	}
	
	private void addText(String s) {
		System.out.println("im here");
		Platform.runLater(new Runnable() { 
			@Override
			public void run(){
				textFlow.getChildren().add(new Text(s));
			}
		});
	}
	
	class ServerThread extends Thread {
		
		private ServerSocket serverSocket;
		private Socket socket;

		private PrintWriter writer;
		private Scanner reader;
		
		public ServerThread() {
			try {
				serverSocket = new ServerSocket(55554);
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
					reader = new Scanner(socket.getInputStream());
					writer = new PrintWriter(socket.getOutputStream(), true);
					while (true && !Thread.currentThread().isInterrupted()) {
						String s = reader.next();
						System.out.println(s);
						if (s.equals("<*message*>")) {
							readMessage(reader);
						} else if (s.equals("<*file*>")) {
							readFile(socket);
						}
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} 
			}
			try {
				serverSocket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		private void readMessage(Scanner reader) {
			System.out.println("we in this1");
			String s = "";
			if (reader.hasNextLine()) {
				s = CLIENT_NAME + reader.nextLine() + '\n';
				System.out.println("we in this2");
			}
			while (reader.hasNextLine()) {
				s += " > " + reader.nextLine() + '\n';
			}
			System.out.println("im here 1");
			addText(s);
		}
		
		private void readFile(Socket socket) {
			
		}
		
		private void writeMessage(String message) {
			
			String s = "";
			BufferedReader reader = new BufferedReader(
									new StringReader(message));
			try {
				writer.println("<*message*>");
				while ((s = reader.readLine()) != null) {
					writer.println(s);
				}
				System.out.println("written1");
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		private void writeFile() {
			
		}
	}
}
