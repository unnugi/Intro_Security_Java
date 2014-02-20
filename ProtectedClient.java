/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

public class ProtectedClient {

	// random generator & date generator
	private Random rand_gen = new Random();
	private Date date = new Date();
	private double rand1;
	private long timestamp1;
	private byte msg_digest1[];
	private double rand2;
	private long timestamp2;
	private byte msg_digest2[];

	private void makeDigest(String user, String password)
			throws NoSuchAlgorithmException {
		/**
		 * 1st message digest using a timestamp, a random number, user name and
		 * password
		 */
		rand1 = rand_gen.nextDouble();
		timestamp1 = date.getTime();
		msg_digest1 = Protection.makeDigest(user, password, timestamp1, rand1);

		/**
		 * 2nd message digest using a timestamp, a random number, the result of
		 * 1st message digest
		 */
		rand2 = rand_gen.nextDouble();
		timestamp2 = date.getTime();
		msg_digest2 = Protection.makeDigest(msg_digest1, timestamp2, rand2);
	}

	public void sendAuthentication(String user, String password,
			OutputStream outStream) throws IOException,
			NoSuchAlgorithmException {

		DataOutputStream out = new DataOutputStream(outStream);

		makeDigest(user, password);

		/**
		 * output stream
		 */
		// length of user name
		out.writeInt(user.getBytes().length);
		// length of message digest
		out.writeInt(msg_digest2.length);
		// timestamp 1
		out.writeLong(timestamp1);
		// timestamp 2
		out.writeLong(timestamp2);
		// random number 1
		out.writeDouble(rand1);
		// random number 2
		out.writeDouble(rand2);
		// user name
		out.write(user.getBytes(), 0, user.getBytes().length);
		// message digest
		out.write(msg_digest2, 0, msg_digest2.length);
		out.flush();

		// output stream content
		/*
		 * System.out.println("length: " + user.getBytes().length);
		 * System.out.println("length: " + msg_digest2.length);
		 * System.out.println("user: " + Arrays.toString(user.getBytes()));
		 * System.out.println("msg: " + Arrays.toString(msg_digest2));
		 * System.out.println("rand1: " + rand1); System.out.println("time1: " +
		 * timestamp1); System.out.println("rand2: " + rand2);
		 * System.out.println("time2: " + timestamp2);
		 */
	}

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 7999;
		String user = "George";
		String password = args[0];// "abc123";
		Socket s = new Socket(host, port);

		ProtectedClient client = new ProtectedClient();
		client.sendAuthentication(user, password, s.getOutputStream());

		s.close();

	}
}