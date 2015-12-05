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
public class ClientServerChatApplication extends Application {
	
	/* data members */
	
	/**
	 * The name of the user.
	 */
	public String name;
	
	/**
	 * Used as the machine address to connect to if the application is launched as a client.
	 */
	private String ip;
	
	/**
	 * Used to open the socket.
	 */
	public int port;
	
	/**
	 * Mutual key for encrypting/decrypting.
	 */
	public int key;
	
	/**
	 * The network thread.
	 */
	private NetworkThread networkThread;
	
	/**
	 * Flag for if the network acts as a server.
	 */
	private boolean isServer;
	
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
	 * Creates a ClientServerChatApplication without ip for the server.
	 * 
	 * @param name The users name
	 * @param port The port to open the socket on.
	 * @param key The key for encryption/decryption.
	 */
	public ClientServerChatApplication(String name, String port, String key, boolean isServer) {
		try {
			this.name = name + " > ";
			this.port = Integer.parseInt(port);
			this.key = Integer.parseInt(key);
		} catch (NumberFormatException nfe) {
			// Defaults if there is a parse error.
			this.port = 1995;
			this.key = 64;
		}
		this.isServer = true;
		// Start the network thread
		networkThread = new NetworkThread();
		networkThread.start();
	}

	/**
	 * Creates a ClientServerChatApplication with ip for the client.
	 * 
	 * @param name The users name
	 * @param port The port to open the socket on.
	 * @param key The key for encryption/decryption.
	 */
	public ClientServerChatApplication(String name, String ip, String port, String key, boolean isServer) {
		try {
			this.name = name + " > ";
			this.ip = ip;
			this.port = Integer.parseInt(port);
			this.key = Integer.parseInt(key);
		} catch (NumberFormatException nfe) {
			// Defaults if there is a parse error.
			this.port = 1995;
			this.key = 64;
		}
		this.isServer = false;
		// Start the server thread.
		networkThread = new NetworkThread();
		networkThread.start();
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
						networkThread.writeMessage(message);
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
			// Get the message and write it to the network thread.
			String message = inputArea.getText();
			addText(name + message);
			networkThread.writeMessage(message);
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
		
		// Set the function of the file button
		Button fileButton = new Button("File");
		fileButton.setOnAction( event -> {
			// Open the file sender and decryptor.
			fileSAndD = new FileSenderAndDecryptor(networkThread, key);
			// Start it as a new application.
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
			// Ensure all streams are closed and ports are freed.
			networkThread.close();
			networkThread.interrupt();
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
	 * Inner class, which is a thread for running the network code.
	 * 
	 * @author Hunter Quant
	 */
	public class NetworkThread extends Thread {
		
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
		public NetworkThread() {
			// Initialize the sockets with the user input credentials.
			try {
				// Start the server socket if the client launched the application as a server.
				if (isServer) {
					serverSocket = new ServerSocket(port);
				}
				socket = null;
			} catch (IOException ioe) {
				addText("Port already in use, restart and select an unoccupied port.");
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
					if (isServer) {
						addText("Waiting for a client to connect.");
						socket = serverSocket.accept();
						addText("Connected to a client.");
					} else {
						socket = new Socket(ip, port);
						addText("Connected to a server.");
					}
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
					addText("Disconnected. Reconnect to chat again.");
					break;
				} finally {
					addText("The other user disconnected.");
					// Close the streams.
					try {
						if (socket != null)
							socket.close();
						if (reader != null)
							reader.close();
						if (writer != null)
							writer.close();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
				
			}
			// Ensure the streams are closed.
			try {
				if (socket != null)
					socket.close();
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		/**
		 * Reads the message from the network until seeing a closing tag.
		 */
		private void readMessage() {
			// Encrypts the message.
			SimpleEncryptor enc = new SimpleEncryptor(key);
			// Used to build the message.
			String message = "";
			// Used to get the next line.
			String s = "";
			try {
				// Read until you reach the closing tag.
				while ((s = reader.readLine()) != null && !s.contains(MSG_CLOSE)) {
					// Encrypt each line and build the message.
					enc.setEncryptedMessage(s);
					enc.textDecrypt();
					message += enc.getClearText();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			addText(message);
		}
		 /**
		  * Reads a file from the network and writes it to the system.
		  */
		private void readFile() {
			PrintWriter out = null;
			try {
				out = new PrintWriter(
						new File("encryptedFile"));
				// Had to essentially create a hasNextLine for BufferedReader
				// I was getting an extra new line and thus an extra character after decrypting.
				String s = reader.readLine();
				while (s != null && !s.contains(FILE_CLOSE)) {
					String temp = s;
					s = reader.readLine();
					// If s is null or the end of the file we don't want the last new line.
					if (s == null || s.contains(FILE_CLOSE)) {
						out.print(temp);
					} else {
						out.println(temp);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			out.close();
			addText("File recieved from the other user and saved as \"encryptedFile.txt\"");
		}
		
		/**
		 * Write the message to the network.
		 * 
		 * @param message The message to be sent over the network.
		 */
		public void writeMessage(String message) {
			
			SimpleEncryptor enc = new SimpleEncryptor(key);
			BufferedReader br = new BufferedReader(
								new StringReader(name + message));
			String s = "";
			
			// Start message with the start tag.
			writer.println(MSG_START);
			try {
				// Encrypt each line and write it to the network.
				while ((s = br.readLine()) != null) {
					enc.setClearText(s);
					enc.textEncrypt();
					writer.println(enc.getEncryptedMessage());
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			// End with the closing tag.
			writer.println(MSG_CLOSE);
		}
		
		/**
		 * Write the file to the network.
		 * 
		 * @param outputFile The file to be wrote to the network.
		 */
		public void writeFile(File outputFile) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(
						new FileReader(outputFile.getAbsolutePath()));
				String s = "";
				
				// Start file with the start tag.
				writer.println(FILE_START);
				//Write each line to the network.
				while ((s = in.readLine()) != null) {
					writer.println(s);
				}
				// End with the closing tag.
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
		
		/**
		 * Ensures all streams are closed when closing the application.
		 */
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