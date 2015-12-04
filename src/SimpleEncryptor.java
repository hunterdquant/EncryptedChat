/**
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * Provides encryption for a line of text.
 * 
 * EDIT: This class was edited to work with the encrypted chat.
 * - Now is constructed with a key as an argument.
 * - Some documentation may not fit as a result.
 */


public class SimpleEncryptor extends Encryptor {
	
		
	/**
	 * New Constructor
	 */
	public SimpleEncryptor(int n) {
		setEncryptionKey(n);
	}
	
	/**
	 * Encrypts the input message using specified encryption format. 
	 */
	@Override
	public void textEncrypt() {

		char[] msgChars = getClearText().toCharArray();
		
		int key = getEncryptionKey(); 
		// Add 4 to each char in the message.
		for (int i = 0; i < msgChars.length; i++) {
			msgChars[i] += 4;
		}
		
		String encryptedMsg = "";
		// Our encrypted message is each character xor the key.
		for (char c : msgChars) {
			encryptedMsg += (char)(c ^ key);
		}
		setEncryptedMessage(encryptedMsg);
	}
	
	/**
	 * Encrypts the input message using specified encryption format. 
	 */
	@Override
	public void textDecrypt() {
		
		// Xor each char with the key to yield the clear text char + 4.
		char[] msgChars = getEncryptedMessage().toCharArray();
		for (int i = 0; i < msgChars.length; i++) {
			msgChars[i] = (char)(msgChars[i] ^ getEncryptionKey());
		}
		
		// Subtract 4 from each character and concatenate them to get the clear text message.
		String decrypted = "";
		for (char c : msgChars) {
			decrypted += (char)(c - 4);
		}
		setClearText(decrypted);
	}
}
