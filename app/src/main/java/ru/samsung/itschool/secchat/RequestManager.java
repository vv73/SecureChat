package ru.samsung.itschool.secchat;


public class RequestManager {

	private final static String from = "FROM:";
	private final static String mess = "MESSAGE:";
	private final static String na = "DH_ACCEPT:";
	private final static String nq = "DH_OFFER:";
	private final static String refused = "DH_REFUSED";
	private final static String to = "TO:";

	static String buildDHAccept(String sfrom, String sto, String sn2) {
		return buildMessage(sfrom, sto, null, sn2, null);
	}

	static String buildDHOffer(String sfrom, String sto, String sn1) {
		return buildMessage(sfrom, sto, sn1, null, null);
	}

	static String buildDHRefused(String sfrom, String sto) {
		return buildMessage(sfrom, sto, null, null, refused);
	}

	static String buildMessage(String sfrom, String sto, String sn1,
			String sn2, String smess) {
		String res = "";
		if (sfrom != null && !sfrom.equals(""))
			res += from + sfrom + " ";
		if (sto != null && !sto.equals(""))
			res += to + sto + " ";
		if (sn1 != null && !sn1.equals(""))
			res += nq + sn1 + " ";
		if (sn2 != null && !sn2.equals(""))
			res += na + sn2 + " ";
		if (smess != null)
			res += mess + smess;
		return res;
	}
	
	static DHChannel getInfo(String input) {

		int ind = input.indexOf(mess);
		if (ind != -1) {
			input = input.substring(0, ind);
		}

		String name1 = getLex(input, from);
		String name2 = getLex(input, to);

		String n1 = getLex(input, nq);
		String n2 = getLex(input, na);
		return new DHChannel(name1, name2, n1, n2, null);
	}
	
	static String getLex(String input, String lex) {
		input += " ";
		int ind = input.indexOf(lex);
		String res = null;
		if (ind != -1) {
			int indStart = ind + lex.length();
			res = input.substring(indStart, input.indexOf(' ', indStart));
			
		}
		
		return res;
	}
	static String getText(String input) {

		int ind = input.indexOf(mess);
		if (ind != -1)
			return input.substring(ind + mess.length());
		else
			return null;

	}

}
