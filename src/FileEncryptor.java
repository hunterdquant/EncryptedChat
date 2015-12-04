/**
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * Provides encryption for files.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  Provides encryption for a file.
 *  
 * EDIT: This class was edited to work with the encrypted chat.
 * - Now is constructed with a key as an argument.
 * - Some documentation may not fit as a result.
 *  
 * @author Hunter Quant
 */
public class FileEncryptor extends Encryptor{
	
	/* data members */
	
	private File inputFile;
	private String outputFileName;
	
	/* constructors */
	
	/**
	 * 
	 * @param inputFile - A user input file to be encrypted/decrypted.
	 * @param outputFileName - The name of the output file.
	 */
	public FileEncryptor(File inputFile, String outputFileName) {
		this.inputFile = inputFile;
		this.outputFileName =  outputFileName;
	}
	
	/**
	 * 
	 * @param inputFile - A user input file to be encrypted/decrypted.
	 * @param outputFileName - The name of the output file.
	 * @param key - The encryption key used for decrypting.
	 */
	public FileEncryptor(File inputFile, String outputFileName, int key) {
		this.inputFile = inputFile;
		this.outputFileName =  outputFileName;
		setEncryptionKey(key);
	}
	
	/* public methods */
	
	/**
	 * Encrypts the input file using the specified encryption format.
	 */
	@Override
	public void textEncrypt() {
		try {
			BufferedReader in = new BufferedReader(
								new FileReader(inputFile.getAbsolutePath()));
			BufferedWriter out = new BufferedWriter(
								 new FileWriter(outputFileName));
			int c;
			while ((c = in.read()) != -1) {
				out.write((c+4)^getEncryptionKey());
			}
			in.close();
			out.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("Your file could not be found.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Decrypts the input file using the specified encryption format.
	 */
	@Override
	public void textDecrypt() {
		try {
			BufferedReader in = new BufferedReader(
								new FileReader(inputFile.getAbsolutePath()));
			BufferedWriter out = new BufferedWriter(
								 new FileWriter(outputFileName));
			int c;
			while ((c = in.read()) != -1) {
				out.write((c ^ getEncryptionKey()) - 4);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("Your file could not be found.");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		return "FileEncryptor";
	}
}
