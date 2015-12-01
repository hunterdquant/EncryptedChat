/**
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * Serves as a client, which prompts the user for their desired form of encryption.
 */

import java.io.File;
import java.util.Scanner;

public class Encrypt {

	/**
	 * Usage:
	 * To run without any arguments. Will prompt the user for input.
	 *  - java Encrypt
	 * To bypass the prompt to encrypt a message.
	 *  - java Encrypt -e "<message>"
	 * To Bypass the prompt to decrypt a message.
	 *  - java Encrypt -d "<message>" <key>
	 * To bypass the prompt to encrypt a file.
	 *  - java Encrypt -fe "<input file>" "<output file>"
	 * To Bypass the prompt to decrypt a file.
	 *  - java Encrypt -fd "<input file>" "<output file>" <key>
	 * 
	 * @param args - Command line arguments.
	 */
	public static void main(String[] args) {
		
		Encryptor encryptor = null;
		try {
			if (args.length == 0) {
				prompt();
			// If the chose to bypass the prompt to encrypt.
			} else if (args[0].equals("-e")) {
				encryptor = new SimpleEncryptor();
				encryptor.setClearText(args[1]);
				encryptor.textEncrypt();
				System.out.println("Your encrypted message is: " + encryptor.getEncryptedMessage());
				System.out.println("The encryption key for your encrypted message is: " 
									+ encryptor.getEncryptionKey() + '\n');
			// If they chose to bypass the prompt to decrypt.
			} else if (args[0].equals("-d")) {
				encryptor = new SimpleEncryptor();
				encryptor.setEncryptedMessage(args[1]);
				encryptor.setEncryptionKey(Byte.parseByte(args[2]));
				encryptor.textDecrypt();
				System.out.println("The decrypted message is: " + encryptor.getClearText());
			// If they chose to bypass the prompt to encrypt a file.
			} else if (args[0].equals("-fe")) {
				File inputFile = new File(args[1]);
				Scanner in = new Scanner(System.in);
				// Continue to prompt until they enter a valid file.
				while (!inputFile.exists()) {
					System.out.println("The file you entered does not exist. Try again: ");
					inputFile = new File(in.nextLine());
				}
				in.close();
				// Check for proper argument numbers.
				if (args.length  == 3) {
					encryptor = new FileEncryptor(inputFile, args[2]);
					encryptor.textEncrypt();
					System.out.println("Your file has been encrypted!");
					System.out.println("Your encryption key is: " + encryptor.getEncryptionKey());
				} else if (args.length == 2) {
					encryptor = new FileEncryptor(inputFile, "encryptedFile.txt");
					encryptor.textEncrypt();
					System.out.println("Your file has been encrypted!");
					System.out.println("Your encryption key is: " + encryptor.getEncryptionKey());
				}
			// If they chose to bypass the prompt to decrypt a file.
			} else if (args[0].equals("-fd")) {
				File inputFile = new File(args[1]);
				Scanner in = new Scanner(System.in);
				while (!inputFile.exists()) {
					System.out.println("The file you entered does not exist. Try again: ");
					inputFile = new File(in.nextLine());
					
				}
				in.close();
				if (args.length == 4) {
					encryptor = new FileEncryptor(inputFile, args[2], Byte.parseByte(args[3]));
					encryptor.textDecrypt();
					System.out.println("Your file has been decrypted!");
				} else if (args.length == 3) {
					encryptor = new FileEncryptor(inputFile, "decryptedFile.txt", Byte.parseByte(args[2]));
					encryptor.textDecrypt();
					System.out.println("Your file has been decrypted!");
				}
			} else {
				System.out.println("You seem to have entered invalid arguments.");
			}
		// Catch the exception if the user entered too many arguments.
		} catch (ArrayIndexOutOfBoundsException aie) {
			// Tell the user of their mistake and terminate.
			System.out.println("You seem to have entered invalid arguments.");
		// Catch any other exception.
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.out.println("You seem to have entered invalid arguments.");
		}
		System.out.println("Terminating the program.");
	}
	
	/**
	 * Prompts the user for whether they want to encrypt or decrypt a line of text of a file..
 	 *
	 */
	public static void prompt() {
		Encryptor encryptor;
		Scanner in = new Scanner(System.in);
		System.out.print("Would you like to encrypt/decrypt a file or text? F/T: ");
		switch (in.nextLine().toLowerCase()) {
			case "t": {
				encryptor = new SimpleEncryptor();
				System.out.print("Would you like to encrypt or decrypt? E/D: ");
				switch (in.nextLine().toLowerCase()) {
					case "e": {
						System.out.print("\nEnter the message you would like to encrypt: ");
						encryptor.setClearText(in.nextLine());
						encryptor.textEncrypt();
						System.out.println("\nYour encrypted message is: " + encryptor.getEncryptedMessage());
						System.out.println("The encryption key for your encrypted message is: " 
											+ encryptor.getEncryptionKey() + '\n');
						break;
					}
					case "d": {
						System.out.print("\nEnter the message you would like to decrypt: ");
						encryptor.setEncryptedMessage(in.nextLine());
						System.out.print("Enter the encryption key for your message: ");
						encryptor.setEncryptionKey(in.nextByte());
						encryptor.textDecrypt();
						System.out.println("\nThe decrypted message is: " + encryptor.getClearText());
						break;
					}
					default: {
						System.out.println("You entered invalid input.");
					}
				}
				break;
			}
			case "f": {
				System.out.print("Would you like to encrypt or decrypt? E/D: ");
				switch (in.nextLine().toLowerCase()) {
					case "e": {
						System.out.print("Enter the name of the file you would like to encrypt: ");
						File inputFile = new File(in.nextLine());
						while (!inputFile.exists()) {
							System.out.print("The file you entered does not exist. Try again: ");
							inputFile = new File(in.nextLine());
						}
						System.out.print("Enter a file name for the output file. Press enter for the default: ");
						String s = in.nextLine();
						encryptor = new FileEncryptor(inputFile, (s.equals("")) ? "encryptedFile.txt" : s);
						encryptor.textEncrypt();
						System.out.println("Your file has been encrypted!");
						System.out.println("Your encryption key is: " + encryptor.getEncryptionKey());
						break;
					}
					case "d": {
						System.out.print("Enter the name of the file you would like to decrypt: ");
						File inputFile = new File(in.nextLine());
						while (!inputFile.exists()) {
							System.out.print("The file you entered does not exist. Try again: ");
							inputFile = new File(in.nextLine());
						}
						System.out.print("Enter a file name for the output file. Press enter for the default: ");
						String s = in.nextLine();
						System.out.print("Enter your encryption key: ");
						encryptor = new FileEncryptor(inputFile, (s.equals("")) ? "decryptedFile.txt" : s, Byte.parseByte(in.next()));
						encryptor.textDecrypt();
						System.out.println("Your file has been decrypted!");
						break;
					}
					default: {
						System.out.println("You entered invalid input.");
					}
				}
				break;
			}
			default : {
				System.out.println("You entered invalid input.");
			}
		}
				
		in.close();
	}
}
