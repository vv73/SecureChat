package ru.samsung.itschool.secchat;


import ru.samsung.itschool.task.Task;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelActivity extends Activity {

	private DHChannel channel;
	private DBManager dbManager;

	public void close(View v) {
		finish();
	}

	public void savekey(View v) {
		EditText key = (EditText) findViewById(R.id.ca_key);
		channel.key = key.getText().toString();
		if (channel.key.equals(""))
			channel.key = null;
		dbManager.addOrUpdateChannel(channel);
		finish();


	}

	public void crack(View v) {
		String N1 = ((TextView) findViewById(R.id.n1)).getText().toString();
		String N2 = ((TextView) findViewById(R.id.n2)).getText().toString();
		String secret = "";
		////////////////////////////////////////
		//Добавьте сюда код вычисления ключа

		////////////////////////////////////////
		EditText key = (EditText) findViewById(R.id.ca_key);

		if (secret.equals(""))
		{
			Task.showDialog(this, "Try to do it yourself :)");
		}
		else
		{
			key.setText(DHCrypt.makeKey(N1, secret));
		}
	}


	private void showData() {
		TextView name1 = (TextView) findViewById(R.id.ca_userName1);
		name1.setText(channel.name1);
		TextView name2 = (TextView) findViewById(R.id.ca_username2);
		name2.setText(channel.name2);
		if (channel.n1 != null) {
			TextView n1 = (TextView) findViewById(R.id.n1);
			n1.setText(channel.n1.toString());
		}
		if (channel.n2 != null) {
			TextView n1 = (TextView) findViewById(R.id.n2);
			n1.setText(channel.n2.toString());
		}

		if (channel.key != null) {
			EditText key = (EditText) findViewById(R.id.ca_key);
			key.setText(channel.key + "");
		}
		ImageView state = (ImageView) this.findViewById(R.id.ca_state);
		if (channel.key != null && !channel.key.equals("")) {
			state.setImageResource(R.drawable.smkey);
		} else if (channel.n1 != null && channel.n2 != null) {
			state.setImageResource(R.drawable.smclosed);
		} else if (channel.n1 != null) {
			state.setImageResource(R.drawable.smrightarrow);
		} else if (channel.n2 != null) {
			state.setImageResource(R.drawable.smleftarrow);
		} else {
			state.setImageResource(R.drawable.smopened);
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel);

		Intent i = this.getIntent();
		String name1 = (i.getExtras()).getString("name1");
		String name2 = (i.getExtras()).getString("name2");
		dbManager = DBManager.getInstance(this);
		channel = dbManager.getChannel(name1, name2);

		showData();

	}
}
