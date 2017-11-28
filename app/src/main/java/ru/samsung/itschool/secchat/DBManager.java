package ru.samsung.itschool.secchat;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private static DBManager dbManager;

	public static DBManager getInstance(Context context) {
		if (dbManager == null) {
			dbManager = new DBManager(context);
		}
		return dbManager;
	}

	/*
	 * TABLE CONNECTIONS (NAME1 TEXT, NAME2 TEXT, N1 TEXT, N2 TEXT, KEY TEXT)
	 * TABLE USERS (NAME TEXT NOT NULL UNIQUE ON CONFLICT IGNORE, DHSECRET TEXT)
	 * TABLE MESSAGES (MESSAGE TEXT)
	 */

	private SQLiteDatabase db;

	private String DB_NAME = "sequrechat.db";

	private DBManager(Context context) {
		db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		createTable();
	}

	void addMessage(String message) {
		db.execSQL("INSERT INTO MESSAGES VALUES(?)", new String[] { message });
	}

	void addOrUpdateChannel(DHChannel connection) {
		if (connection != null)
			addOrUpdateConnection(connection.name1, connection.name2,
					connection.n1, connection.n2, connection.key);
	}

	void addOrUpdateConnection(String name1, String name2, String n1,
			String n2, String key) {

		if (name1 == null || name2 == null || name1.equals(name2))
			return;

		DHChannel connection = getChannel(name1, name2);

		if (connection == null) {
			db.execSQL("INSERT INTO CONNECTIONS VALUES(?, ?, ?, ?, ?)",
					new String[] { name1, name2, n1, n2, key });
			db.execSQL("INSERT INTO CONNECTIONS VALUES(?, ?, ?, ?, ?)",
					new String[] { name2, name1, n2, n1, key });

		} else {
			/*
			 * if (n1 == null && connection.n1 != null) n1 = connection.n1; if
			 * (n2 == null && connection.n2 != null) n2 = connection.n2; if (key
			 * == null && connection.key != null) key = connection.key;
			 */
			db.execSQL(
					"UPDATE CONNECTIONS SET N1 = ?, N2 = ?, KEY = ? WHERE NAME1 = ? AND NAME2 = ?",
					new String[] { n1, n2, key, name1, name2 });
			db.execSQL(
					"UPDATE CONNECTIONS SET N1 = ?, N2 = ?, KEY = ? WHERE NAME1 = ? AND NAME2 = ?",
					new String[] { n2, n1, key, name2, name1 });
		}

	}

	void addUser(String name) {

		db.execSQL("INSERT INTO USERS (NAME, DHSECRET) VALUES(?, ?)",
				new String[] { name, null });
	}

	ArrayList<DHChannel> getAllDHConnections() {

		Cursor cursor = db
				.rawQuery(
						"SELECT * FROM CONNECTIONS WHERE NAME1 < NAME2 LIMIT 100",
						null);
		ArrayList<DHChannel> res = new ArrayList<DHChannel>();
		boolean hasMoreData = cursor.moveToFirst();
		while (hasMoreData) {
			res.add(new DHChannel(cursor.getString(cursor
					.getColumnIndex("NAME1")), cursor.getString(cursor
					.getColumnIndex("NAME2")), cursor.getString(cursor
					.getColumnIndex("N1")), cursor.getString(cursor
					.getColumnIndex("N2")), cursor.getString(cursor
					.getColumnIndex("KEY"))));
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		return res;
	}

	ArrayList<String> getAllMessages() {

		Cursor cursor = db.rawQuery(
				"SELECT * FROM MESSAGES ORDER BY ROWID DESC LIMIT 500", null);
		ArrayList<String> res = new ArrayList<String>();

		boolean hasMoreData = cursor.moveToFirst();
		while (hasMoreData) {
			res.add(new String(cursor.getString(cursor
					.getColumnIndex("MESSAGE"))));
			hasMoreData = cursor.moveToNext();
		}
		Collections.reverse(res);
		cursor.close();
		return res;
	}

	ArrayList<String> getAllUserNames() {

		Cursor cursor = db.rawQuery("SELECT * FROM USERS LIMIT 100", null);
		ArrayList<String> res = new ArrayList<String>();

		boolean hasMoreData = cursor.moveToFirst();
		while (hasMoreData) {
			res.add(new String(cursor.getString(cursor.getColumnIndex("NAME"))));
			hasMoreData = cursor.moveToNext();
		}
		cursor.close();
		return res;
	}

	DHChannel getChannel(String name1, String name2) {
		Cursor cursor = db.rawQuery(
				"SELECT * FROM CONNECTIONS WHERE NAME1 = ? AND NAME2 = ?",
				new String[] { name1, name2 });
		DHChannel channel = null;
		if (cursor.moveToFirst()) {
			channel = new DHChannel(cursor.getString(cursor
					.getColumnIndex("NAME1")), cursor.getString(cursor
					.getColumnIndex("NAME2")), cursor.getString(cursor
					.getColumnIndex("N1")), cursor.getString(cursor
					.getColumnIndex("N2")), cursor.getString(cursor
					.getColumnIndex("KEY")));
		}
		cursor.close();
		return channel;
	}

	String getDHSecret(String username) {
		Cursor cursor = db.rawQuery(
				"SELECT DHSECRET FROM USERS WHERE NAME = ?",
				new String[] { username });
		if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		String res = cursor.getString(cursor.getColumnIndex("DHSECRET"));
		cursor.close();
		return res;

	}

	String getKey(DHChannel channel) {
		String res = null;
		if (channel != null && channel.name1 != null && channel.name2 != null) {
			channel = dbManager.getChannel(channel.name1, channel.name2);
			if (channel != null)
				res = channel.key;
		}

		return res;
	}

	void setUserSecret(String name, String dhSecret) {
		Cursor cursor = db.rawQuery("SELECT * FROM USERS WHERE NAME = ?",
				new String[] { name });
		if (cursor.moveToFirst()) {
			db.execSQL("UPDATE USERS SET DHSECRET = ? WHERE NAME = ?",
					new String[] { dhSecret, name });
		}
		cursor.close();
	}

	private void createTable() {
		db.execSQL("DROP TABLE IF EXISTS CONNECTIONS;");
		db.execSQL("DROP TABLE IF EXISTS USERS;");
		db.execSQL("DROP TABLE IF EXISTS MESSAGES;");
		db.execSQL("CREATE TABLE CONNECTIONS (NAME1 TEXT, NAME2 TEXT, N1 TEXT, N2 TEXT, KEY TEXT);");
		db.execSQL("CREATE TABLE USERS (NAME TEXT NOT NULL UNIQUE ON CONFLICT IGNORE, DHSECRET TEXT);");
		db.execSQL("CREATE TABLE MESSAGES (MESSAGE TEXT);");
	}
}
