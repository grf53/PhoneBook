package com.example.phonebook;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

public class PhoneBookListAdapter extends BaseAdapter {

	private Context mContext;

	private List<PhoneBookItem> mItems = new ArrayList<PhoneBookItem>();

	public PhoneBookListAdapter(Context context) {
		mContext = context;
	}

	public void addItem(PhoneBookItem it) {
		mItems.add(it);
	}

	public void setListItems(List<PhoneBookItem> lit) {
		mItems = lit;
	}

	public boolean areAllItemsSelectable() {
		return false;
	}

	public boolean isSelectable(int position) {
		try {
			return mItems.get(position).isSelectable();
		} catch (IndexOutOfBoundsException ex) {
			return false;
		}
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhoneBookView itemView;

		if (convertView == null) {
			itemView = new PhoneBookView(mContext, mItems.get(position));
		} else {
			itemView = (PhoneBookView) convertView;
			itemView.setIcon(mItems.get(position).getIcon());
			itemView.setText(0, mItems.get(position).getDisplayName());
		}
		CheckBox mCheckBox = (CheckBox)itemView.findViewById(R.id.exportCheckBox);
		mCheckBox.setChecked(mItems.get(position).isBoxChecked());
			
		return itemView;
	}

}
