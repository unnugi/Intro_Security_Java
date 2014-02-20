/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

public class X509Server {
	private static Key private_key;

	public static void main(String[] args) throws Exception {
		int port = 7999;
		ServerSocket server = new ServerSocket(port);

		// Load keystore
		KeyStore keystore = KeyStore.getInstance("JKS");
		keystore.load(new FileInputStream(new File("keytool_gen_keys")),
				"abcdef".toCharArray());
		// - get private key
		private_key = keystore.getKey("cch_keys", "abcdef".toCharArray());
		if (private_key == null) {
			System.out.println("Got null key from keystore.");
			return;
		}
		// - get certificate
		X509Certificate x509cert = (X509Certificate) keystore
				.getCertificate("cch_keys");
		if (x509cert == null)
			System.out.println("Got null cert from keystore.");

		// Save certificate to a file (for client)
		FileOutputStream f_out = new FileOutputStream("X509Certificate.dat");
		ObjectOutputStream out = new ObjectOutputStream(f_out);
		out.writeObject(x509cert);
		out.close();

		// waiting for client
		System.out.println("Certificate generated.");
		System.out.println("Waiting for client's message...");

		// Use the key to decrypt the incoming message from socket s.
		// maximum length of the message is 1024 bytes
		byte byte_message[] = new byte[1024];
		byte tmp;
		int i = 0;
		Socket s = server.accept();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, private_key);
		CipherInputStream cipher_in = new CipherInputStream(s.getInputStream(),
				cipher);

		// read in byte by byte
		while ((tmp = (byte) cipher_in.read()) > 0) {
			byte_message[i++] = tmp;
		}

		cipher_in.close();
		s.close();

		// cut off the empty tail
		for (i = 0; i < byte_message.length; i++) {
			if (byte_message[i] == 0) {
				byte_message = Arrays.copyOf(byte_message, i);
				break;
			}
		}

		// print out
		// System.out.println(Arrays.toString(deciphered_msg));
		String message = new String(byte_message, "UTF-8");
		System.out.println("Reveived message: " + message);
	}
}