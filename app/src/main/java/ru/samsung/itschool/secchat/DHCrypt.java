package ru.samsung.itschool.secchat;

import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Random;

public class DHCrypt {

	static final BigInteger G = new BigInteger("3");
	static final BigInteger P = new BigInteger("3571");

	public static String genSecret() {
		BigInteger secret = BigInteger.ONE;
		// ///////////////////////////////////////
        secret = new BigInteger(10, new Random()).add(BigInteger.TEN);
		// ///////////////////////////////////////
		return secret.toString();
	}

	public static String makeKey(String sN2, String ssecret) {
		BigInteger key = BigInteger.ONE;
		// ///////////////////////////////////////
		try {key = new BigInteger(sN2).modPow(new BigInteger(ssecret), P);}
		catch(Exception e){
			Log.e( "DH","DH data error");}
		// ///////////////////////////////////////
		return key.toString();
	}

	public static String makeN1(String ssecret) {
		return makeN(ssecret);
	}

	public static String makeN2(String ssecret) {
		return makeN(ssecret);
	}

	public static String makeN(String ssecret) {
		BigInteger N = BigInteger.ONE;
		// ///////////////////////////////////////
		N = G.modPow(new BigInteger(ssecret), P);
		// ///////////////////////////////////////
		return N.toString();
	}

	public static byte[] fromHexString(String hextext) {
		byte[] res = new byte[hextext.length() / 2];
		for (int i = 0; i < hextext.length(); i += 2) {
			try {
				res[i / 2] = (byte) (Integer.parseInt(
						hextext.substring(i, i + 2), 16));
			} catch (NumberFormatException e) {

				return ("..." + hextext).getBytes(Charset.defaultCharset());
			}
		}
		return res;
	}

	public static String toHexString(byte[] ba) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < ba.length; i++)
			str.append(String.format("%02x", ba[i]));
		return str.toString();
	}

	static byte[] crypt(byte array[], String skey) {
		BigInteger key = new BigInteger(skey);
		long seed = (key.mod(new BigInteger(Long.MAX_VALUE + ""))).longValue();
		Random r = new Random(seed);

		byte[] keys = new byte[array.length];
		r.nextBytes(keys);
		for (int i = 0; i < array.length; i++) {
			array[i] ^= keys[i];
		}

		return array;

	}

	static String crypthex(String text, String skey) {
		byte[] array = crypt(text.getBytes(Charset.defaultCharset()), skey);
		return toHexString(array);
	}

	static String decrypt(String hexcrypt, String skey) {
		byte[] array = crypt(fromHexString(hexcrypt), skey);
		return new String(array, Charset.defaultCharset());
	}

	static BigInteger makeBigInteger(String str) {
		BigInteger res;
		try {
			res = new BigInteger(str);
		} catch (NumberFormatException e) {
			res = BigInteger.ONE;
		}
		return res;
	}

}