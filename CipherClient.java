/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

public class CipherClient {
	public static void main(String[] args) throws Exception {
		String message = args[0];// "The quick brown fox jumps over the lazy dog.";
		String host = "localhost";
		int port = 7999;

		// YOU NEED TO DO THESE STEPS:
		// -Generate a DES key.
		KeyGenerator key_gen = KeyGenerator.getInstance("DES");
		key_gen.init(new SecureRandom());
		Key key = key_gen.generateKey();

		// -Store it in a file.
		FileOutputStream f_out = new FileOutputStream("KeyFile.dat");
		ObjectOutputStream out = new ObjectOutputStream(f_out);
		out.writeObject(key);
		out.close();

		System.out.print("Press Enter to continue...");
		Scanner scanIn = new Scanner(System.in);
		scanIn.nextLine();
		scanIn.close();

		// -Use the key to encrypt the message above and send it over socket s
		// to the server.
		Socket s = new Socket(host, port);
		Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		CipherOutputStream cipher_out = new CipherOutputStream(
				s.getOutputStream(), cipher);
		cipher_out.write(message.getBytes(), 0, message.getBytes().length);
		cipher_out.flush();
		cipher_out.close();
		s.close();

		// output message bytes
		// System.out.println(Arrays.toString(message.getBytes()));
	}
}