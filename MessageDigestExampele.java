/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.security.MessageDigest;

public class MessageDigestExampele {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// get input message
		String message = args[0];

		/**
		 * generate MD5
		 */
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// completes the hash computation
		byte md5_result_byte[] = md5.digest(message.getBytes());
		// convert to Hex
		String md5_result_str = bytesToHex(md5_result_byte);
		// output
		System.out.println("MD5: " + md5_result_str);

		/**
		 * generate SHA-256
		 */
		MessageDigest sha = MessageDigest.getInstance("SHA");
		// completes the hash computation
		byte sha_result_byte[] = sha.digest(message.getBytes());
		// convert to Hex
		String sha_result_str = bytesToHex(sha_result_byte);
		// output
		System.out.println("SHA: " + sha_result_str);

	}

	protected static String bytesToHex(byte[] bytevalues) {

		StringBuffer hexvalue = new StringBuffer();
		for (byte bytevalue : bytevalues) {
			// transform each byte to two-digit hex value
			hexvalue.append(String.format("%02x", bytevalue));
		}
		return hexvalue.toString();
	}
}
