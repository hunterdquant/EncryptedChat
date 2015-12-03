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
	
	public final String SERVER_NAME = "Mario > ";
	public final String CLIENT_NAME = "Luigi > ";
	
	private ClientThread client;
	
	private final TextArea textArea = new TextArea();
	private final TextFlow textFlow = new TextFlow();
	
	public ClientApplication(String port, String ip, String key) {
		try {
			this.port = Integer.parseInt(port);
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
		grid.setPadding(new Insets(25, 25, 25, 25));		
		
		textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode().equals(KeyCode.ENTER)) {
						String message = textArea.getText();
						textFlow.getChildren().add(new Text(CLIENT_NAME + message + '\n'));
						client.writeMessage(message);
						textArea.clear();
					}
				}
			});
		
		Button sendButton = new Button("Send");
		sendButton.setOnAction( event -> {
			String message = textArea.getText();
			textFlow.getChildren().add(new Text(CLIENT_NAME + message + '\n'));
			client.writeMessage(message);
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
			client.interrupt();
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
	
	class ClientThread extends Thread {

		private Socket socket;

		private PrintWriter writer;
		private	Scanner reader;
		
		public ClientThread() {
			socket = null;
		}
		
		@Override
		public void run() {
			
			while (!Thread.currentThread().isInterrupted()) {
				try {
					socket = new Socket(ip, 55554);
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
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} 
			System.out.println("written2");
		}
		
		private void writeFile() {
			
		}
	}
}
