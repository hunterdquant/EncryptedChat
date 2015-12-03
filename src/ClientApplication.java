import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	private FileSenderAndDecryptor  fileSAndD;
	
	private final TextArea inputArea = new TextArea();
	private final TextArea chatArea = new TextArea();
	
	private final String MSG_START = "<*MESSAGE>";
	private final String MSG_CLOSE = "<MESSAGE*>";
	private final String FILE_START = "<*FILE>";
	private final String FILE_CLOSE = "<FILE*>";
	
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
		
		Button fileButton = new Button("File");
		fileButton.setOnAction( event -> {
			fileSAndD = new FileSenderAndDecryptor(client, key);
			fileSAndD.start(new Stage());
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
						if (message.contains(MSG_START)) {
							readMessage(message.substring(MSG_START.length()));
						} else if (message.contains(FILE_START)) {
							readFile();
						}
						System.out.println("waiting in run");
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
			String s = "";
			try {
				while (!message.contains(MSG_CLOSE) && (s += reader.readLine()).contains(MSG_CLOSE)) {
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			SimpleEncryptor enc = new SimpleEncryptor(key);
			message += s;
			enc.setEncryptedMessage(message.substring(0, message.length()-(MSG_CLOSE.length()-1)));
			enc.textDecrypt();
		
			addText(enc.getClearText() + '\n');
		}
		
		private void readFile() {
			PrintWriter out = null;
			try {
				out = new PrintWriter(
						new File("encryptedFile.txt"));
				String s = "";
				while ((s = reader.readLine()) != null && !s.contains(FILE_CLOSE)) {
					System.out.println(s);
					out.println(s);
				}
			} catch (IOException ioe) {
				
			}
			out.close();
			addText("File saved as \"encryptedFile.txt\"\n");
		}
		
		private void writeMessage(String message) {
			
			SimpleEncryptor enc = new SimpleEncryptor(key);
			enc.setClearText(name + message);
			enc.textEncrypt();
			writer.println(MSG_START + enc.getEncryptedMessage() + MSG_CLOSE);
		}

		public void writeFile(File outputFile) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(
						new FileReader(outputFile.getAbsolutePath()));
				String s = "";
				System.out.println("preparing");
				writer.println(FILE_START);
				while ((s = in.readLine()) != null) {
					System.out.println(s);
					writer.println(s);
				}
				writer.println(FILE_CLOSE);
				addText("File sent!\n");
			} catch (FileNotFoundException fnfe) {
				addText("File not found!\n");
				return;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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
