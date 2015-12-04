import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The chat application for the connection hosting user.
 * 
 * @author Hunter Quant
 */
public class ServerApplication extends Application {
	
	/* data members */
	
	/**
	 * Used to open the socket.
	 */
	public int port;
	
	/**
	 * Mutual key for encrypting/decrypting.
	 */
	public int key;
	
	/**
	 * The name of the user.
	 */
	public String name;
	
	/**
	 * The network thread.
	 */
	private ServerThread server;
	
	/**
	 * Application for sending and decrypting.
	 */
	private FileSenderAndDecryptor  fileSAndD;
	
	/**
	 * Message input field.
	 */
	private final TextArea inputArea = new TextArea();
	
	/**
	 * Message display field.
	 */
	private final TextArea chatArea = new TextArea();
	
	/**
	 * Starting and closing tags for messages.
	 */
	private final String MSG_START = "<*MESSAGE>";
	private final String MSG_CLOSE = "<MESSAGE*>";
	
	/**
	 * Starting and closing tags for files.
	 */
	private final String FILE_START = "<*FILE>";
	private final String FILE_CLOSE = "<FILE*>";
	
	/* constructors */
	
	/**
	 * Creates a ServerApplication.
	 * 
	 * @param name The users name
	 * @param port The port to open the socket on.
	 * @param key The key for encryption/decryption.
	 */
	public ServerApplication(String name, String port, String key) {
		try {
			this.name = name + " > ";
			this.port = Integer.parseInt(port);
			System.out.println(this.port);
			this.key = Integer.parseInt(key);
		} catch (NumberFormatException nfe) {
			// Defaults if there is a parse error.
			this.port = 1995;
			this.key = 64;
		}
		// Start the server thread.
		server = new ServerThread();
		server.start();
	}

	/* public methods */
	
	/**
	 * Displays and sets up all GUI elements.
	 * 
	 * @param primaryStage The main stage for the application.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		primaryStage.setTitle("Encrypted Chat Server");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		
		// TextArea parameters.
		chatArea.setPrefSize(700, 250);
		chatArea.setMaxSize(700, 250);
		chatArea.setMinSize(700, 250);
		chatArea.setEditable(false);
		chatArea.setWrapText(true);
		inputArea.setPrefSize(700, 100);
		inputArea.setMaxSize(700, 100);
		inputArea.setMinSize(700, 100);
		inputArea.setWrapText(true);
		inputArea.setPrefRowCount(1);
		
		// Set the function of the enter key.
		inputArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					// If the hit the enter key.
					if (event.getCode().equals(KeyCode.ENTER)) {
						// Get the text and send it.
						String message = inputArea.getText();
						addText(name + message);
						server.writeMessage(message);
						inputArea.clear();
						// Reposition the cursor.
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								inputArea.positionCaret(0);
							}
						});
					}
				}
			});
		
		// Set the function of the send button.
		Button sendButton = new Button("Send");
		sendButton.setOnAction( event -> {
			// Get the message and write it to the network using the server thread.
			String message = inputArea.getText();
			addText(name + message);
			server.writeMessage(message);
			inputArea.clear();
			// Reset the cursor.
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					inputArea.positionCaret(0);
				}
			});
		});
		sendButton.setPadding(new Insets(15, 15, 15, 15));
		
		Button fileButton = new Button("File");
		fileButton.setOnAction( event -> {
			fileSAndD = new FileSenderAndDecryptor(server, key);
			fileSAndD.start(new Stage());
		});
		fileButton.setPadding(new Insets(15, 20, 15, 20));
		
		// Added nodes to the grid.
		VBox vBox = new VBox(sendButton, fileButton);
		grid.add(chatArea, 0, 0);
		grid.add(inputArea, 0, 1);
		grid.add(vBox, 1, 1);
		
		// Set dimensions and display.
		Scene scene = new Scene(grid, 800, 400);
		primaryStage.setScene(scene);
		// Window parameters
		primaryStage.setOnCloseRequest( event -> {
			server.close();
			server.interrupt();
		});
		primaryStage.setResizable(false);
		primaryStage.show();
	}
	
	/* private methods */
	
	/**
	 * Adds the message to the chatArea.
	 * 
	 * @param message The message to be added.
	 */
	private void addText(String message) {
		Platform.runLater(new Runnable() { 
			@Override
			public void run() {
				chatArea.appendText(message + '\n');
			}
		});
	}
	
	/**
	 * Inner class, which is a thread for running the server code.
	 * 
	 * @author Hunter Quant
	 */
	class ServerThread extends Thread {
		
		/**
		 * The server socket.
		 */
		private ServerSocket serverSocket;
		
		/**
		 * The socket connected to.
		 */
		private Socket socket;

		/**
		 * The writer that is open on the output stream from the socket.
		 */
		private PrintWriter writer;
		
		/**
		 * The reader which is open on the input stream from the socket.
		 */
		private BufferedReader reader;
		
		/* constructors */
		
		/**
		 * Creates a ServerThread.
		 */
		public ServerThread() {
			// Initialize the sockets with the user input credentials.
			try {
				serverSocket = new ServerSocket(port);
				socket = null;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		/* public methods */
		
		/**
		 * Called at the start of the thread.
		 */
		@Override
		public void run() {
			
			// Loop till the thread is interupted.
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// Establish the connection.
					socket = serverSocket.accept();
					// Init reader/writer.
					reader = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					writer = new PrintWriter(socket.getOutputStream(), true);
					String message = null;
					// Continue reading from the network.
					while ((message = reader.readLine()) != null) {
						// Check for the message flag.
						if (message.contains(MSG_START)) {
							readMessage();
						// Check for the file flag.
						} else if (message.contains(FILE_START)) {
							readFile();
						}
					}
				} catch (IOException ioe) {
					addText("Disconnected from the client\n");
				} finally {
					// Close the streams.
					try {
						if (socket != null)
							socket.close();
						if(reader != null)
							reader.close();
						if(writer != null)
							writer.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
			//Close the streams
			try {
				if (socket != null)
					socket.close();
				if(reader != null)
					reader.close();
				if(writer != null)
					writer.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		/**
		 * Reads the message from the network untill seeing a closing tag.
		 * 
		 * @param message
		 */
		private void readMessage() {
			SimpleEncryptor enc = new SimpleEncryptor(key);
			String message = "";
			String s = "";
			try {
				while ((s = reader.readLine()) != null && !s.contains(MSG_CLOSE)) {
					enc.setEncryptedMessage(s);
					enc.textDecrypt();
					message += enc.getClearText();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			addText(message);
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
				ioe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.close();
			addText("File saved as \"encryptedFile.txt\"");
		}
		
		public void writeMessage(String message) {
		
			SimpleEncryptor enc = new SimpleEncryptor(key);
			BufferedReader br = new BufferedReader(
								new StringReader(name + message));
			String s = "";
			System.out.println("preparing");
			writer.println(MSG_START);
			try {
				while ((s = br.readLine()) != null) {
					enc.setClearText(s);
					enc.textEncrypt();
					writer.println(enc.getEncryptedMessage());
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			writer.println(MSG_CLOSE);
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
				addText("File sent!");
			} catch (FileNotFoundException fnfe) {
				addText("File not found!");
				return;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
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
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
