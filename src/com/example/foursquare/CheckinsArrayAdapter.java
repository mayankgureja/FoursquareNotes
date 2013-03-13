package com.example.foursquare;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckinsArrayAdapter extends ArrayAdapter<String>
{
	private Context context;
	private String[] Locations;
	private String[] Shouts;

	public CheckinsArrayAdapter(Context context, String[] Locations,
			String[] Shouts)
	{
		super(context, R.layout.checkins_row_view, Locations);
		this.context = context;
		this.Locations = Locations;
		this.Shouts = Shouts;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(R.layout.checkins_row_view, parent,
				false);

		TextView textView1 = (TextView) rowView.findViewById(R.id.textView1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.textView2);

		textView1.setText(Locations[pos]);
		textView1.setSelected(true);
		textView2.setText(Shouts[pos]);

		return rowView;
	}

}
