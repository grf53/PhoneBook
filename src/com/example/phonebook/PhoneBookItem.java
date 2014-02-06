package com.example.phonebook;

import android.graphics.drawable.Drawable;

public class PhoneBookItem {

	private Drawable icon;
	private String displayName;
	private String phoneNumber;
	private boolean boxChecked;

	private boolean mSelectable = true;

	public PhoneBookItem(Drawable icon, String displayName, String phoneNumber) {
		this.icon = icon;
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
		boxChecked = false;
	}
	
	public boolean isSelectable() {
		return mSelectable;
	}

	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}
/*
	public String[] getData() {
		String[] data = {displayName, phoneNumber};
		return data;
	}

	public String getData(int index) {
		if (mData == null || index >= mData.length) {
			return null;
		}
		
		return mData[index];
	}
*/
	public void setData(String displayName, String phoneNumber) {
		this.displayName = displayName;
		this.phoneNumber = phoneNumber;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public boolean isBoxChecked() {
		return boxChecked;
	}

	public void setBoxChecked(boolean boxChecked) {
		this.boxChecked = boxChecked;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return icon;
	}
/*
	public int compareTo(IconTextItem other) {
		if (mData != null) {
			String[] otherData = other.getData();
			if (mData.length == otherData.length) {
				for (int i = 0; i < mData.length; i++) {
					if (!mData[i].equals(otherData[i])) {
						return -1;
					}
				}
			} else {
				return -1;
			}
		} else {
			throw new IllegalArgumentException();
		}
		
		return 0;
	}
*/
}
