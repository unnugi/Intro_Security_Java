/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;

public class ElGamalAlice {
	private static BigInteger computeY(BigInteger p, BigInteger g, BigInteger d) {
		// y = g^d mod p
		return g.modPow(d, p);
	}

	private static BigInteger computeK(BigInteger p) {
		// k is relatively prime to p-1
		BigInteger k = BigInteger.TEN;
		BigInteger pMinusOne = p.subtract(BigInteger.ONE);

		while ((k.gcd(pMinusOne).compareTo(BigInteger.ONE) != 0)) {
			k = k.add(BigInteger.ONE);
		}

		return k;
	}

	private static BigInteger computeA(BigInteger p, BigInteger g, BigInteger k) {
		// a = g^k mod p
		return g.modPow(k, p);
	}

	private static BigInteger computeB(String message, BigInteger d,
			BigInteger a, BigInteger k, BigInteger p) {
		// b = ((m-da)*H) mod (p-1),
		// H= k.modInverse(p-1).
		BigInteger m = new BigInteger(message.getBytes());
		BigInteger pMinusOne = p.subtract(BigInteger.ONE);
		BigInteger H = k.modInverse(pMinusOne);

		return m.subtract(d.multiply(a)).multiply(H).mod(pMinusOne);
	}

	/*
	 * private static BigInteger computeB(String message, BigInteger d,
	 * BigInteger a, BigInteger k, BigInteger p) { // (da + kb) mod (p-1) = m //
	 * b = [(p-1)*q + (m-da)]/k BigInteger m = new
	 * BigInteger(message.getBytes()); BigInteger pMinusOne =
	 * p.subtract(BigInteger.ONE);
	 * 
	 * BigInteger temp1 = m.subtract(d.multiply(a)); BigInteger q =
	 * BigInteger.ZERO; BigInteger temp2;
	 * 
	 * // looking for proper q do { q = q.add(BigInteger.ONE); temp2 =
	 * pMinusOne.multiply(q).add(temp1); } while ((temp2.gcd(k).compareTo(k) !=
	 * 0));
	 * 
	 * return temp2.divide(k); }
	 */

	public static void main(String[] args) throws Exception {
		String message = args[0];// "The quick brown fox jumps over the lazy dog!";

		String host = "localhost";
		int port = 7999;
		Socket s = new Socket(host, port);
		ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());

		// You should consult BigInteger class in Java API documentation to find
		// out what it is.
		BigInteger y, g, p; // public key
		BigInteger d; // private key

		int mStrength = 1024; // key bit length
		SecureRandom mSecureRandom = new SecureRandom(); // a cryptographically
															// strong
															// pseudo-random
															// number

		// Create a BigInterger with mStrength bit length that is highly likely
		// to be prime.
		// (The '16' determines the probability that p is prime. Refer to
		// BigInteger documentation.)
		p = new BigInteger(mStrength, 16, mSecureRandom);

		// Create a randomly generated BigInteger of length mStrength-1
		g = new BigInteger(mStrength - 1, mSecureRandom);
		d = new BigInteger(mStrength - 1, mSecureRandom);

		y = computeY(p, g, d);
		// At this point, you have both the public key and the private key. Now
		// compute the signature.

		BigInteger k = computeK(p);
		BigInteger a = computeA(p, g, k);
		BigInteger b = computeB(message, d, a, k, p);

		// send public key
		os.writeObject(y);
		os.writeObject(g);
		os.writeObject(p);

		// send message
		os.writeObject(message);

		// send signature
		os.writeObject(a);
		os.writeObject(b);

		os.close();
		s.close();
	}
}