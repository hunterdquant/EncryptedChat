/**
 * @author Hunter Quant <quanthd@clarkson.edu>
 *
 * Provides encryption for a line of text.
 */


public class SimpleEncryptor extends Encryptor {
	
		
	/**
	 * Default constructor.
	 */
	public SimpleEncryptor() {
		
	}
	
	/**
	 * Encrypts the input message using specified encryption format. 
	 */
	@Override
	public void textEncrypt() {
		
		// Get the sum off all characters.
		char[] msgChars = getClearText().toCharArray();
		int sum = 0;
		for (char c : msgChars) {
			sum += c;
		}
		
		// Sum mod 128 is the encryption key for the message.
		byte key = (byte)(sum % 128);
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
		setEncryptionKey(key);
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
