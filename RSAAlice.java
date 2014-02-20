/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class RSAAlice {
	private static Key public_key;
	private static Key private_key;

	protected static void generate_key() throws NoSuchAlgorithmException {
		int mStrength = 1024; // key bit length
		SecureRandom mSecureRandom = new SecureRandom();

		KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");
		key_gen.initialize(mStrength, mSecureRandom);
		KeyPair keys = key_gen.generateKeyPair();
		public_key = (Key) keys.getPublic();
		private_key = (Key) keys.getPrivate();
	}

	protected static byte[] encrypt_func(byte[] msg, Cipher cipher)
			throws IllegalBlockSizeException, BadPaddingException, IOException {
		ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
		byte[] cache;
		int block_size = 117;
		int cnt = 0;

		// encipher the message block by block
		while (cnt < msg.length) {
			if ((msg.length - cnt) >= block_size) {
				cache = cipher.doFinal(msg, cnt, block_size);
			} else {
				cache = cipher.doFinal(msg, cnt, msg.length - cnt);
			}
			byte_stream.write(cache, 0, cache.length);
			cnt += block_size;
		}

		byte[] encrypted_msg = byte_stream.toByteArray();
		byte_stream.close();
		return encrypted_msg;
	}

	public static void main(String[] args) throws Exception {
		String message = args[0];// "The quick brown fox jumps over the lazy dog.!!";
		String host = "localhost";
		int port = 7999;

		// Generate a RSA key.
		generate_key();
		System.out.println("Key generated.");

		// Store it in a file.
		FileOutputStream f_out = new FileOutputStream("RSAKeyFileAlice.dat");
		ObjectOutputStream out = new ObjectOutputStream(f_out);
		out.writeObject(public_key);
		out.close();

		// Waiting for key sharing
		System.out.println("After all keys are saved in the files.");
		System.out.print("Press Enter to continue...");
		Scanner scanIn = new Scanner(System.in);
		scanIn.nextLine();
		scanIn.close();

		// Read the public key from the file generated by server.
		FileInputStream f_in = new FileInputStream("RSAKeyFileBob.dat");
		ObjectInputStream in = new ObjectInputStream(f_in);
		Key public_key_Bob = (Key) in.readObject();

		// Encryption
		byte[] enciphered_msg;
		byte[] byte_message = message.getBytes();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		// - encipher by Alice's private key
		cipher.init(Cipher.ENCRYPT_MODE, private_key);
		enciphered_msg = encrypt_func(byte_message, cipher);

		// - encipher by Bob's public key
		cipher.init(Cipher.ENCRYPT_MODE, public_key_Bob);
		enciphered_msg = encrypt_func(enciphered_msg, cipher);

		// Send to server
		Socket s = new Socket(host, port);
		ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
		os.writeObject(enciphered_msg);
		os.flush();
		os.close();
		s.close();

		System.out.println(message);
	}
}