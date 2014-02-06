package com.example.phonebook;

import com.example.phonebook.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhoneBookView extends RelativeLayout {

	private ImageView mIcon;

	private TextView mText0;
	//private TextView mText1;
	private CheckBox mCheckBox;

	public PhoneBookView(Context context, final PhoneBookItem item) {
		super(context);

		// Layout Inflation
		((LayoutInflater)(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.listitem, this, true);

		// Set Icon
		mIcon = (ImageView) findViewById(R.id.photoImageView);
		mIcon.setImageDrawable(item.getIcon());

		// Set Text 01
		mText0 = (TextView) findViewById(R.id.nameTextView);
		mText0.setText(item.getDisplayName());
		
		mCheckBox = (CheckBox) findViewById(R.id.exportCheckBox);
	}
	
	public void setText(int index, String data) {
		switch(index){
		case 0:
			mText0.setText(data);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	public void setIcon(Drawable icon) {
		mIcon.setImageDrawable(icon);
	}
	public void setBoxChecked(boolean boxChecked) {
		mCheckBox.setChecked(boxChecked);
	}
}
