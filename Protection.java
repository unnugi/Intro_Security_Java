/**********************************************\
* Course: Introduction to Security 
* Final Project
* Student: Xiaoxiao Yu
* E-mail: xiy38@pitt.edu
* Last modified: 2013/04/03
\**********************************************/

import java.io.*;
import java.security.*;

public class Protection
{
	/**
	 * Convert a long integer and a double into a byte array
	 * @param t : a long integer
	 * @param q : a double value
	 * @return  : a byte array
	 */
	public static byte[] makeBytes(long t, double q) 
	{    
		try 
		{
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(byteOut);
			dataOut.writeLong(t);
			dataOut.writeDouble(q);
			return byteOut.toByteArray();
		}
		catch (IOException e) 
		{
			return new byte[0];
		}
	}	

	/**
	 * generate a SHA hash value with message, time stamp, and a random number
	 * @param mush : message
	 * @param t2   : time stamp
	 * @param q2   : random number
	 * @return     : SHA byte array
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] makeDigest(byte[] mush, long t2, double q2) throws NoSuchAlgorithmException 
	{
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(mush);
		md.update(makeBytes(t2, q2));
		return md.digest();
	}
	
	/**
	 * generate a SHA hash value with the following inputs
	 * @param user      : user name
	 * @param password  : password
	 * @param t1        : time stamp
	 * @param q1        : random number
	 * @return          : SHA byte array
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] makeDigest(String user, String password,
									long t1, double q1)
									throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(user.getBytes());
		md.update(password.getBytes());
		md.update(makeBytes(t1, q1));
		return md.digest();
	}
}
