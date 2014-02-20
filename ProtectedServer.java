/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ProtectedServer {
	// random generator & date generator
	private double rand1;
	private long timestamp1;
	private byte msg_digest1[];
	private double rand2;
	private long timestamp2;
	private byte msg_digest2[];

	private void makeDigest(String user, String password)
			throws NoSuchAlgorithmException {
		// 1st message digest
		msg_digest1 = Protection.makeDigest(user, password, timestamp1, rand1);

		// 2nd message digest
		msg_digest2 = Protection.makeDigest(msg_digest1, timestamp2, rand2);
	}

	public boolean authenticate(InputStream inStream) throws IOException,
			NoSuchAlgorithmException {
		DataInputStream in = new DataInputStream(inStream);

		byte user_byte[];
		byte msg_digest[];
		int length1, length2;

		/**
		 * get values from client
		 */
		// get length of user name
		length1 = in.readInt();
		// get length of message digest
		length2 = in.readInt();
		// get user name
		user_byte = new byte[length1];
		// get message digest
		msg_digest = new byte[length2];
		// get timestamp 1
		timestamp1 = in.readLong();
		// get timestamp 2
		timestamp2 = in.readLong();
		// get random number 1
		rand1 = in.readDouble();
		// get random number 2
		rand2 = in.readDouble();
		in.readFully(user_byte);
		in.readFully(msg_digest);

		// output stream content
		/*
		 * System.out.println("length:" + length1); System.out.println("length:"
		 * + length2); System.out.println("user: " + Arrays.toString(user));
		 * System.out.println("msg: " + Arrays.toString(msg_digest));
		 * System.out.println("rand1: " + rand1); System.out.println("time1: " +
		 * timestamp1); System.out.println("rand2: " + rand2);
		 * System.out.println("time2: " + timestamp2);
		 */

		// transform from bytes to string
		String user = new String(user_byte, "UTF-8");
		// get password
		String password = lookupPassword(user);
		// make digest
		makeDigest(user, password);

		// output message digests
		// System.out.println(Arrays.toString(msg_digest));
		// System.out.println(Arrays.toString(msg_digest2));

		// compare message digest from client to the one computed at server
		return Arrays.equals(msg_digest, msg_digest2);
	}

	protected String lookupPassword(String user) {
		return "abc123";
	}

	public static void main(String[] args) throws Exception {
		int port = 7999;
		ServerSocket s = new ServerSocket(port);
		Socket client = s.accept();

		ProtectedServer server = new ProtectedServer();

		if (server.authenticate(client.getInputStream()))
			System.out.println("Client logged in.");
		else
			System.out.println("Client failed to log in.");

		s.close();
	}
}