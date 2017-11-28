package ru.samsung.itschool.secchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class IOManager extends Thread {

	private static int lastInd = 1;

    static String server;

	public static void init() {
		lastInd=1;
	}
	
	public static ArrayList<String> getMessages() {
		return processCommand("?start=" + lastInd);
	}

	public static ArrayList<String> sendMessage(String message) {
        String command=""; 
		try {
			command = "?act=add&msg=" + URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
        return processCommand(command); 
	}

	synchronized static private ArrayList<String> processCommand(String command) {
		ArrayList<String> res = new ArrayList<String>();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(server + command);
		HttpResponse response = null;
		try {
			response = client.execute(request);
			if (response != null) {
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));

				String line = null;
				while ((line = reader.readLine()) != null) {
					if (line.length() == 1 && line.charAt(0) == '\uFEFF')
						continue;
					res.add(line.replace("\uFEFF", ""));
				}
				in.close();
				lastInd += res.size();
			}
		} catch (ClientProtocolException e) {
			Log.e("IO", "Http connect Error");

		} catch (IOException e) {
			Log.e("IO", "Http IOError");
		}
		return res;
	}
}
