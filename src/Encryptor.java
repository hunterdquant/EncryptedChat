/**
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * Provides features for Encryptor classes.
 */

public abstract class Encryptor {
	
	private int encryptionKey;
	private String clearText;
	private String encryptedMessage;
	
	public abstract void textEncrypt();
	public abstract void textDecrypt();
	
	/**
	 * Sets the value of the encryption key.
	 * 
	 * @param key - the value to be set as the encryption key.
	 */
	public void setEncryptionKey(int key) {
		encryptionKey = key;
	}
	
	/**
	 * Sets the clear text message to the passed string.
	 * Sets encryptedMessage to null.
	 * 
	 * @param message - The message to be assigned to clearText.
	 */
	public void setClearText(String message) {
		clearText = message;
		encryptedMessage = null;
	}
	
	/**
	 * Sets the encrypted message to the passed string.
	 * Sets clearText to null.
	 * 
	 * @param message - the message to be assigned to the encryptedMessage
	 */
	public void setEncryptedMessage(String message) {
		encryptedMessage = message;
		clearText = null;
	}
	
	/**
	 * Get the encryption key.
	 * 
	 * @return The byte value of encryptionKey
	 */
	public int getEncryptionKey() {
		return encryptionKey;
	}
	
	/**
	 * Gets the encrypted message.
	 * 
	 * @return The string value of encryptedMessage.
	 */
	public String getEncryptedMessage() {
		return encryptedMessage;
	}
	
	/**
	 * Gets the clear text message.
	 * 
	 * @return The string value of clearText.
	 */
	public String getClearText() {
		return clearText;
	}
}
