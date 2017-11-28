package ru.samsung.itschool.secchat;

import ru.samsung.itschool.secchat.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

	public void enter(View v) {
		Intent i = new Intent(this, MainActivity.class);
		EditText chatserveret = (EditText) this.findViewById(R.id.la_chatserver);
		String chatserver = chatserveret.getText().toString();
		IOManager.server = chatserver;
		EditText user = (EditText) this.findViewById( R.id.username);
		String username = user.getText().toString();
		if (!username.equals("") && username.indexOf(' ') == -1) {
			i.putExtra("myName", username);
			this.startActivity(i);
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}
}
