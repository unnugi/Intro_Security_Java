/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class X509Client {

	public static void main(String[] args) throws Exception {
		String message = args[0];// "The quick brown fox jumps over the lazy dog.";
		String host = "localhost";
		int port = 7999;

		// Read certificate from file
		FileInputStream f_in = new FileInputStream("X509Certificate.dat");
		ObjectInputStream in = new ObjectInputStream(f_in);
		X509Certificate x509cert = (X509Certificate) in.readObject();
		in.close();
		f_in.close();

		// - check expiration date
		try {
			x509cert.checkValidity();
		} catch (CertificateExpiredException e) {
			System.out.println("The certificate is expired.");
		} catch (CertificateNotYetValidException e) {
			System.out.println("The certificate is not yet valid.");
		}

		// - get public key
		PublicKey public_key_server = x509cert.getPublicKey();

		// - verify public key
		x509cert.verify(public_key_server);

		// -Use the key to encrypt the message above and send it over socket s
		// to the server.
		Socket s = new Socket(host, port);
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, public_key_server);

		CipherOutputStream cipher_out = new CipherOutputStream(
				s.getOutputStream(), cipher);
		cipher_out.write(message.getBytes(), 0, message.getBytes().length);
		cipher_out.flush();
		cipher_out.close();
		s.close();

		System.out.println("Certificate:\n" + x509cert.toString());
		System.out.println("Sent message: " + message);
	}
}