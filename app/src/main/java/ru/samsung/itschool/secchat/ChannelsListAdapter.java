package ru.samsung.itschool.secchat;

import java.util.ArrayList;

import ru.samsung.itschool.secchat.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChannelsListAdapter extends ArrayAdapter<DHChannel> {

	private final ArrayList<DHChannel> channels;
	private final Context context;

	public ChannelsListAdapter(Context context, ArrayList<DHChannel> channels) {
		super(context, R.layout.channelrowlayout, channels);
		this.context = context;
		this.channels = channels;
	}

	@Override
	public View getView(int position, View rowView, ViewGroup parent) {
		if (rowView == null)
		{	
		   LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		   rowView = inflater.inflate(R.layout.channelrowlayout, parent, false);
		}
		TextView username1 = (TextView) rowView.findViewById(R.id.username1);
		TextView username2 = (TextView) rowView.findViewById(R.id.username2);
		ImageView state = (ImageView) rowView.findViewById(R.id.cr_state);
		DHChannel channel = channels.get(position);
		username1.setText(channel.name1);
		username2.setText(channel.name2);
		if (channel.key != null)
		{
			state.setImageResource(R.drawable.smkey);
		}
		else if (channel.n1 != null && channel.n2 != null)
		{
			state.setImageResource(R.drawable.smclosed);
		}
		else if (channel.n1 != null)
		{
			state.setImageResource(R.drawable.smrightarrow);
		}
		else if (channel.n2 != null)
		{
			state.setImageResource(R.drawable.smleftarrow);
		}
		else 
		{
			state.setImageResource(R.drawable.smopened);
		}
		
		return rowView;
	}
}
