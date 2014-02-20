/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

public class ElGamalBob {
	private static boolean verifySignature(BigInteger y, BigInteger g,
			BigInteger p, BigInteger a, BigInteger b, String message) {
		// check y^a * a^b mod p == g^m mod p
		// or (y^a mod p)(a^b mod p) mod p == g^m mod p

		BigInteger m = new BigInteger(message.getBytes());
		BigInteger temp1;
		BigInteger temp2;

		temp1 = y.modPow(a, p);
		temp2 = a.modPow(b, p);
		temp1 = temp1.multiply(temp2).mod(p);
		temp2 = g.modPow(m, p);

		return (temp1.compareTo(temp2) == 0);
	}

	public static void main(String[] args) throws Exception {
		int port = 7999;
		ServerSocket s = new ServerSocket(port);
		Socket client = s.accept();
		ObjectInputStream is = new ObjectInputStream(client.getInputStream());

		// read public key
		BigInteger y = (BigInteger) is.readObject();
		BigInteger g = (BigInteger) is.readObject();
		BigInteger p = (BigInteger) is.readObject();

		// read message
		String message = (String) is.readObject();

		// read signature
		BigInteger a = (BigInteger) is.readObject();
		BigInteger b = (BigInteger) is.readObject();

		boolean result = verifySignature(y, g, p, a, b, message);

		System.out.println(message);

		if (result == true)
			System.out.println("Signature verified.");
		else
			System.out.println("Signature verification failed.");

		is.close();
		s.close();
	}
}