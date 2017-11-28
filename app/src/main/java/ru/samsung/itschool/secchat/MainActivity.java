package ru.samsung.itschool.secchat;

import java.util.ArrayList;

import ru.samsung.itschool.secchat.R;
import ru.samsung.itschool.task.Task;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final private ArrayList<DHChannel> connections = new ArrayList<DHChannel>();
	final private ArrayList<String> decryptedMessages = new ArrayList<String>();
	final private ArrayList<String> userNames = new ArrayList<String>();

	private DBManager dbManager;

	private String myName;

	Thread motor;

	void processMessages(ArrayList<String> mess) {

		for (String message : mess) {

			DHChannel connection = RequestManager.getInfo(message);
			if (connection.name1 != null && !connection.name1.equals(myName))
				dbManager.addUser(connection.name1);
			if (connection.name2 != null && !connection.name2.equals(myName))
				dbManager.addUser(connection.name2);

			if (connection.name1 != null && connection.name2 != null) {

				DHChannel savedConnection = dbManager.getChannel(
						connection.name1, connection.name2);
				if (savedConnection == null)
					savedConnection = connection;

				if (connection.n1 != null) {

					savedConnection.n1 = connection.n1;
					if (connection.name2.equals(myName)) {

						secureChannelRequestProcess(savedConnection);

					}
				}

				if (connection.n2 != null) {

					savedConnection.n1 = connection.n2;
					if (connection.name2.equals(myName)) {

						String secret = dbManager.getDHSecret(connection.name1);
						if (secret != null) {
							savedConnection.key = DHCrypt.makeKey(
									connection.n2, secret);
						}
					}
				}
				dbManager.addOrUpdateChannel(savedConnection);
			}

			dbManager.addMessage(message);

		}

		refreshViews();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refreshViews();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent i = this.getIntent();
		this.myName = (i.getExtras()).getString("myName");

		dbManager = DBManager.getInstance(this);
		IOManager.init();
		this.setupViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
		motor.interrupt();
	}

	@Override
	protected void onResume() {
		super.onResume();

		motor = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (!Thread.interrupted()) {
						new GetMessages().execute();

						Thread.sleep(3000);
					}

				} catch (InterruptedException e) {
				}
			}
		});
		motor.start();
	}

	private void refreshViews() {
		connections.clear();
		connections.addAll(dbManager.getAllDHConnections());
		userNames.clear();
		userNames.addAll(dbManager.getAllUserNames());
		decryptedMessages.clear();
		ArrayList<String> allMessages = dbManager.getAllMessages();

		for (String message : allMessages) {
			DHChannel channel = RequestManager.getInfo(message);
			String text = RequestManager.getText(message);
			String key = null;
			if (channel != null) {
				key = dbManager.getKey(channel);
			}
			if (channel != null && key != null && text != null) {
				message = channel.name1 + " TO " + channel.name2 + ": "
						+ DHCrypt.decrypt(text, key);
				decryptedMessages.add(message);

			}

		}

		int listviewIds[] = { R.id.messages, R.id.userlist, R.id.channellist };

		for (int id : listviewIds) {
			ListView listView = (ListView) this.findViewById(id);
			ArrayAdapter<?> adapter = ((ArrayAdapter<?>) listView.getAdapter());
			adapter.notifyDataSetChanged();
			listView.smoothScrollToPosition(adapter.getCount() - 1);
		}
	}

	private void secureChannelRequestProcess(final DHChannel connection) {
		dbManager.addOrUpdateChannel(connection);
		new AlertDialog.Builder(this)
				.setTitle("Channel Request")
				.setMessage(
						"Do you want to make secret channel with "
								+ connection.name1 + "?")
				.setPositiveButton("Ok", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						String secret = DHCrypt.genSecret();
						dbManager.setUserSecret(connection.name1, secret);
						String n2 = DHCrypt.makeN2(secret);
						if (n2.equals("1") || secret.equals("1")) {
							Task.showDialog(MainActivity.this,
									"�������� ������� genSecret � makeN ������ DHCrypt.");
						}
						connection.key = DHCrypt.makeKey(connection.n1, secret);
						new SendMessage().execute(RequestManager.buildDHAccept(
								MainActivity.this.myName, connection.name1, n2));
						dbManager.addOrUpdateChannel(connection);
						refreshViews();

					}
				}).setNegativeButton("No, thanks", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						refreshViews();
					}
				}).setCancelable(true)
				.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						refreshViews();
					}
				}).show();
	}

	public void sendMessage(View v) {
		EditText et = (EditText) (this.findViewById(R.id.mess));
		String message = et.getText().toString();
		EditText toUser = (EditText) MainActivity.this.findViewById(R.id.to);
		String to = toUser.getText().toString();
		DHChannel connection = dbManager.getChannel(this.myName, to);
		if (connection != null && connection.key != null) {
			message = DHCrypt.crypthex(message, connection.key);
		}
		new SendMessage().execute(RequestManager.buildMessage(this.myName, to,
				null, null, message));
		new GetMessages().execute();
	}

	private void sendSecureChannelRequest(String to) {

		String secret = DHCrypt.genSecret();
		dbManager.setUserSecret(to, secret);
		String n1 = DHCrypt.makeN1(secret);
		if (n1.equals("1") || secret.equals("1")) {
			Task.showDialog(this,
					"�������� ������� genSecret � makeN ������ DHCrypt.");
		} else {
			new SendMessage().execute(RequestManager.buildDHOffer(
					MainActivity.this.myName, to, n1));
			Toast toast = Toast.makeText(this,
					"Sequre Channel Request sent to " + to, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	private void setupViews() {
		ListView channelsListView = (ListView) this
				.findViewById(R.id.channellist);
		channelsListView.setAdapter(new ChannelsListAdapter(this, connections));
		channelsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(MainActivity.this, ChannelActivity.class);
				DHChannel connection = connections.get(position);
				i.putExtra("name1", connection.name1);
				i.putExtra("name2", connection.name2);
				MainActivity.this.startActivityForResult(i, 0);
			}
		});

		ListView userListView = (ListView) this.findViewById(R.id.userlist);
		userListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, userNames));

		userListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				String username = userNames.get(position);
				EditText toUser = (EditText) MainActivity.this
						.findViewById(R.id.to);
				toUser.setText(username);
			}
		});
		userListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				String to = userNames.get(position);
				MainActivity.this.sendSecureChannelRequest(to);
				return true;
			}

		});

		ListView messageListView = (ListView) this.findViewById(R.id.messages);

		messageListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, decryptedMessages));

		TextView tvmyName = (TextView) this.findViewById(R.id.myname);
		tvmyName.setText(this.myName);

		refreshViews();
	}

	class SendMessage extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String message = params[0];
			IOManager.sendMessage(message);
			return null;
		}
	}

	class GetMessages extends AsyncTask<Void, Void, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ArrayList<String> res = IOManager.getMessages();
			return res;
		}

		@Override
		protected void onPostExecute(ArrayList<String> res) {
			super.onPostExecute(res);
			MainActivity.this.processMessages(res);

		}

	}
}
